package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Slow extends FlavourBuff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    public static final float DURATION = 10f;

    @Override
    public int icon() {
        return BuffIndicator.SLOW;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

}
