package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class LockedFloor extends Buff {

    //the amount of turns remaining before beneficial passive effects turn off
    private float left = 50; //starts at 50 turns

    @Override
    public boolean act() {
        spend(TICK);

        if (!Dungeon.level.locked)
            detach();

        if (left >= 1)
            left--;

        return true;
    }

    public void addTime(float time) {
        left += time;
    }

    public boolean regenOn() {
        return left >= 1;
    }

    private final String LEFT = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getFloat(LEFT);
    }

    @Override
    public int icon() {
        return BuffIndicator.LOCKED_FLOOR;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}
