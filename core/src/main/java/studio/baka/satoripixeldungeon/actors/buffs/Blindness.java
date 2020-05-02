package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Blindness extends FlavourBuff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
    }

    @Override
    public int icon() {
        return BuffIndicator.BLINDNESS;
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
