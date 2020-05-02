package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.effects.Effects;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.effects.Pushing;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Elastic;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfBlastWave extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_BLAST_WAVE;

        collisionProperties = Ballistica.PROJECTILE;
    }

    public int min(int lvl) {
        return 1 + lvl;
    }

    public int max(int lvl) {
        return 5 + 3 * lvl;
    }

    public void zap2(Ballistica bolt) {
        onZap(bolt);
    }

    @Override
    protected void onZap(Ballistica bolt) {
        Sample.INSTANCE.play(Assets.SND_BLAST);
        BlastWave.blast(bolt.collisionPos);

        //presses all tiles in the AOE first
        for (int i : PathFinder.NEIGHBOURS9) {
            Dungeon.level.pressCell(bolt.collisionPos + i);
        }

        //throws other chars around the center.
        for (int i : PathFinder.NEIGHBOURS8) {
            Char ch = Actor.findChar(bolt.collisionPos + i);

            if (ch != null) {
                processSoulMark(ch, chargesPerCast());
                if (ch.alignment != Char.Alignment.ALLY) ch.damage(damageRoll(), this);

                if (ch.isAlive()) {
                    Ballistica trajectory = new Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT);
                    int strength = 1 + Math.round(level() / 2f);
                    throwChar(ch, trajectory, strength);
                } else if (ch == Dungeon.hero) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                }
            }
        }

        //throws the char at the center of the blast
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(), this);

            if (ch.isAlive() && bolt.path.size() > bolt.dist + 1) {
                Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
                int strength = level() + 3;
                throwChar(ch, trajectory, strength);
            }
        }

    }

    public static void throwChar(final Char ch, final Ballistica trajectory, int power) {
        int dist = Math.min(trajectory.dist, power);

        if (ch.properties().contains(Char.Property.BOSS))
            dist /= 2;

        if (dist == 0 || ch.properties().contains(Char.Property.IMMOVABLE)) return;

        if (Actor.findChar(trajectory.path.get(dist)) != null) {
            dist--;
        }

        final int newPos = trajectory.path.get(dist);

        if (newPos == ch.pos) return;

        final int finalDist = dist;
        final int initialpos = ch.pos;

        Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
            public void call() {
                if (initialpos != ch.pos) {
                    //something caused movement before pushing resolved, cancel to be safe.
                    ch.sprite.place(ch.pos);
                    return;
                }
                ch.pos = newPos;
                if (ch.pos == trajectory.collisionPos && ch.isAlive()) {
                    ch.damage(Random.NormalIntRange((finalDist + 1) / 2, finalDist), this);
                    Paralysis.prolong(ch, Paralysis.class, Random.NormalIntRange((finalDist + 1) / 2, finalDist));
                }
                Dungeon.level.occupyCell(ch);
                if (ch == Dungeon.hero) {
                    //FIXME currently no logic here if the throw effect kills the hero
                    Dungeon.observe();
                }
            }
        }), -1);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        new Elastic().proc(staff, attacker, defender, damage);
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        new Elastic().proc(staff, attacker, defender, (int) (damage * 1.05f));
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FORCE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x664422);
        particle.am = 0.6f;
        particle.setLifespan(3f);
        particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
        particle.setSize(1f, 2f);
        particle.radiateXY(2.5f);
    }

    public static class BlastWave extends Image {

        private static final float TIME_TO_FADE = 0.2f;

        private float time;

        public BlastWave() {
            super(Effects.get(Effects.Type.RIPPLE));
            origin.set(width / 2, height / 2);
        }

        public void reset(int pos) {
            revive();

            x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
            y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

            time = TIME_TO_FADE;
        }

        @Override
        public void update() {
            super.update();

            if ((time -= Game.elapsed) <= 0) {
                kill();
            } else {
                float p = time / TIME_TO_FADE;
                alpha(p);
                scale.y = scale.x = (1 - p) * 3;
            }
        }

        public static void blast(int pos) {
            Group parent = Dungeon.hero.sprite.parent;
            BlastWave b = (BlastWave) parent.recycle(BlastWave.class);
            parent.bringToFront(b);
            b.reset(pos);
        }

    }
}
