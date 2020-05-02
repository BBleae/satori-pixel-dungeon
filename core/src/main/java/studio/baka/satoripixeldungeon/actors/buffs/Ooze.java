package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Ooze extends Buff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    private float left;
    private static final String LEFT = "left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        //pre-0.7.0
        if (bundle.contains(LEFT)) {
            left = bundle.getFloat(LEFT);
        } else {
            left = 20;
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.OOZE;
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
        return Messages.get(this, "desc", dispTurns(left));
    }

    public void set(float left) {
        this.left = left;
    }

    @Override
    public boolean act() {
        if (target.isAlive()) {
            if (Dungeon.depth > 4)
                target.damage(Dungeon.depth / 5, this);
            else if (Random.Int(2) == 0)
                target.damage(1, this);
            if (!target.isAlive() && target == Dungeon.hero) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "ondeath"));
            }
            spend(TICK);
            left -= TICK;
            if (left <= 0) {
                detach();
            }
        } else {
            detach();
        }
        if (Dungeon.level.water[target.pos]) {
            detach();
        }
        return true;
    }
}
