package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Momentum extends Buff {

    {
        type = buffType.POSITIVE;
    }

    private int stacks = 0;
    private int turnsSinceMove = 0;

    @Override
    public boolean act() {
        turnsSinceMove++;
        if (turnsSinceMove > 0) {
            stacks = Math.max(0, stacks - turnsSinceMove);
            BuffIndicator.refreshHero();
            if (stacks == 0) detach();
        }
        spend(TICK);
        return true;
    }

    public void gainStack() {
        stacks = Math.min(stacks + 1, 10);
        turnsSinceMove = -1;
        BuffIndicator.refreshHero();
    }

    public int stacks() {
        return stacks;
    }

    public float speedMultiplier() {
        //1.33x speed at max stacks
        return 1f + (stacks / 30f);
    }

    public int evasionBonus(int excessArmorStr) {
        //8 evasion, +2 evasion per excess str, at max stacks
        return Math.round((0.8f + 0.2f * excessArmorStr) * stacks);
    }

    @Override
    public int icon() {
        return BuffIndicator.MOMENTUM;
    }

    @Override
    public void tintIcon(Image icon) {
        if (stacks <= 5) {
            icon.hardlight(0.2f * (stacks - 1), 1f, 0f);
        } else {
            icon.hardlight(1f, 1f - 0.2f * (stacks - 6), 0f);
        }
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", stacks * 10);
    }

    private static final String STACKS = "stacks";
    private static final String TURNS_SINCE = "turnsSinceMove";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STACKS, stacks);
        bundle.put(TURNS_SINCE, turnsSinceMove);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stacks = bundle.getInt(STACKS);
        turnsSinceMove = bundle.getInt(TURNS_SINCE);
    }
}
