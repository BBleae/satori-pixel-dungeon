package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.effects.Wound;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import com.watabou.noosa.audio.Sample;

public class GrippingTrap extends Trap {

    {
        color = GREY;
        shape = DOTS;
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

        if (c != null && !c.flying) {
            int damage = Math.max(0, (2 + Dungeon.depth) - c.drRoll());
            Buff.affect(c, Bleeding.class).set(damage);
            Buff.prolong(c, Cripple.class, Cripple.DURATION);
            Wound.hit(c);
        } else {
            Wound.hit(pos);
        }

    }
}
