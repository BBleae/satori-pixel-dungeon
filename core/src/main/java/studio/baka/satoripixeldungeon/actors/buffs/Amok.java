package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Amok extends FlavourBuff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.AMOK;
    }

    @Override
    public void detach() {
        super.detach();
        if (target instanceof Mob)
            ((Mob) target).aggro(null);
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
