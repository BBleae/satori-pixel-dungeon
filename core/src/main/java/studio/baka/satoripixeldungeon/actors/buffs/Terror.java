package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Terror extends FlavourBuff {

    public int object = 0;

    private static final String OBJECT = "object";

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OBJECT, object);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        object = bundle.getInt(OBJECT);
    }

    @Override
    public int icon() {
        return BuffIndicator.TERROR;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public void recover() {
        spend(-5f);
        if (cooldown() <= 0) {
            detach();
        }
    }
}
