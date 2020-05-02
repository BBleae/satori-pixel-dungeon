package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.weapon.missiles.darts.Dart;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WornDartTrap extends Trap {

    {
        color = GREY;
        shape = CROSSHAIR;

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
            final WornDartTrap trap = this;
            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[target.pos]) {
                Actor.add(new Actor() {

                    {
                        //it's a visual effect, gets priority no matter what
                        actPriority = VFX_PRIO;
                    }

                    @Override
                    protected boolean act() {
                        final Actor toRemove = this;
                        ((MissileSprite) SatoriPixelDungeon.scene().recycle(MissileSprite.class)).
                                reset(pos, finalTarget.sprite, new Dart(), () -> {
                                    int dmg = Random.NormalIntRange(1, 4) - finalTarget.drRoll();
                                    finalTarget.damage(dmg, trap);
                                    if (finalTarget == Dungeon.hero && !finalTarget.isAlive()) {
                                        Dungeon.fail(trap.getClass());
                                    }
                                    Sample.INSTANCE.play(Assets.SND_HIT, 1, 1, Random.Float(0.8f, 1.25f));
                                    finalTarget.sprite.bloodBurstA(finalTarget.sprite.center(), dmg);
                                    finalTarget.sprite.flash();
                                    Actor.remove(toRemove);
                                    next();
                                });
                        return false;
                    }
                });
            } else {
                finalTarget.damage(Random.NormalIntRange(1, 4) - finalTarget.drRoll(), trap);
            }
        }
    }
}
