package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class FlashingTrap extends Trap {

    {
        color = GREY;
        shape = STARS;
    }

    @Override
    public void trigger() {
        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play(Assets.SND_TRAP);
        }
        //this trap is not disarmed by being triggered
        reveal();
        Level.set(pos, Terrain.TRAP);
        activate();
    }

    @Override
    public void activate() {

        Char c = Actor.findChar(pos);

        if (c != null) {
            int damage = Math.max(0, (4 + Dungeon.depth) - c.drRoll());
            Buff.affect(c, Bleeding.class).set(damage);
            Buff.prolong(c, Blindness.class, 10f);
            Buff.prolong(c, Cripple.class, 20f);

            if (c instanceof Mob) {
                if (((Mob) c).state == ((Mob) c).HUNTING) ((Mob) c).state = ((Mob) c).WANDERING;
                ((Mob) c).beckon(Dungeon.level.randomDestination());
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            GameScene.flash(0xFFFFFF);
            Sample.INSTANCE.play(Assets.SND_BLAST);
        }

    }

}
