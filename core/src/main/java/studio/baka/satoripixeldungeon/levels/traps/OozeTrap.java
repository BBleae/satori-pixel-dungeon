package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Ooze;
import studio.baka.satoripixeldungeon.effects.Splash;

public class OozeTrap extends Trap {

    {
        color = GREEN;
        shape = DOTS;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null && !ch.flying) {
            Buff.affect(ch, Ooze.class).set(20f);
            Splash.at(pos, 0x000000, 5);
        }
    }
}
