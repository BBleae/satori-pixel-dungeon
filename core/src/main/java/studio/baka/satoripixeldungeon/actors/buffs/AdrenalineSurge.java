package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class AdrenalineSurge extends Buff {

    {
        type = buffType.POSITIVE;
    }

    private int boost;
    private float interval;

    public void reset(int boost, float interval) {
        this.boost = boost;
        this.interval = interval;
        spend(interval - cooldown());
    }

    public int boost() {
        return boost;
    }

    @Override
    public boolean act() {
        boost--;
        if (boost > 0) {
            spend(interval);
        } else {
            detach();
        }
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
    public String desc() {
        return Messages.get(this, "desc", boost, dispTurns(cooldown() + 1));
    }

    private static final String BOOST = "boost";
    private static final String INTERVAL = "interval";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BOOST, boost);
        bundle.put(INTERVAL, interval);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        boost = bundle.getInt(BOOST);
        //pre-0.7.1
        if (bundle.contains(INTERVAL)) {
            interval = bundle.getFloat(INTERVAL);
        } else {
            interval = 800f;
        }
    }
}
