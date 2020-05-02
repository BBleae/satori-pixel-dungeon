package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Roots extends FlavourBuff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (!target.flying && super.attachTo(target)) {
            target.rooted = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        target.rooted = false;
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.ROOTS;
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
