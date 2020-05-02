package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Fury extends Buff {

    public static float LEVEL = 0.5f;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public boolean act() {
        if (target.HP > target.HT * LEVEL) {
            detach();
        }

        spend(TICK);

        return true;
    }

    @Override
    public int icon() {
        return BuffIndicator.FURY;
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
        return Messages.get(this, "desc");
    }
}
