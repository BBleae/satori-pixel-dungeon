package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Weakness extends FlavourBuff {

    public static final float DURATION = 40f;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.WEAKNESS;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }
}
