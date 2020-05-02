package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GrimTrap extends Trap {

    {
        color = GREY;
        shape = LARGE_DOT;

        canBeHidden = false;
    }

    @Override
    public void activate() {
        Char target = Actor.findChar(pos);

        //find the closest char that can be aimed at
        if (target == null) {
            for (Char ch : Actor.chars()) {
                Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                if (bolt.collisionPos == ch.pos &&
                        (target == null || Dungeon.level.trueDistance(pos, ch.pos) < Dungeon.level.trueDistance(pos, target.pos))) {
                    target = ch;
                }
            }
        }

        if (target != null) {
            final Char finalTarget = target;
            final GrimTrap trap = this;
            int damage;

            //almost kill the player
            if (finalTarget == Dungeon.hero && ((float) finalTarget.HP / finalTarget.HT) >= 0.9f) {
                damage = finalTarget.HP - 1;
                //kill 'em
            } else {
                damage = finalTarget.HP;
            }

            final int finalDmg = damage;

            Actor.add(new Actor() {

                {
                    //it's a visual effect, gets priority no matter what
                    actPriority = VFX_PRIORITY;
                }

                @Override
                protected boolean act() {
                    final Actor toRemove = this;
                    ((MagicMissile) finalTarget.sprite.parent.recycle(MagicMissile.class)).reset(
                            MagicMissile.SHADOW,
                            DungeonTilemap.tileCenterToWorld(pos),
                            finalTarget.sprite.center(),
                            () -> {
                                finalTarget.damage(finalDmg, trap);
                                if (finalTarget == Dungeon.hero) {
                                    Sample.INSTANCE.play(Assets.SND_CURSED);
                                    if (!finalTarget.isAlive()) {
                                        Dungeon.fail(GrimTrap.class);
                                        GLog.n(Messages.get(GrimTrap.class, "ondeath"));
                                    }
                                } else {
                                    Sample.INSTANCE.play(Assets.SND_BURNING);
                                }
                                finalTarget.sprite.emitter().burst(ShadowParticle.UP, 10);
                                Actor.remove(toRemove);
                                next();
                            });
                    return false;
                }
            });
        } else {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 10);
            Sample.INSTANCE.play(Assets.SND_BURNING);
        }
    }
}
