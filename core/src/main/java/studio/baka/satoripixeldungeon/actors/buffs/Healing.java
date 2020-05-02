package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Healing extends Buff {

    private int healingLeft;

    private float percentHealPerTick;
    private int flatHealPerTick;

    {
        //unlike other buffs, this one acts after the hero and takes priority against other effects
        //healing is much more useful if you get some of it off before taking damage
        actPriority = HERO_PRIO - 1;

        type = buffType.POSITIVE;
    }

    @Override
    public boolean act() {

        target.HP = Math.min(target.HT, target.HP + healingThisTick());

        healingLeft -= healingThisTick();

        if (healingLeft <= 0) {
            detach();
        }

        spend(TICK);

        return true;
    }

    private int healingThisTick() {
        return (int) GameMath.gate(1,
                Math.round(healingLeft * percentHealPerTick) + flatHealPerTick,
                healingLeft);
    }

    public void setHeal(int amount, float percentPerTick, int flatPerTick) {
        healingLeft = amount;
        percentHealPerTick = percentPerTick;
        flatHealPerTick = flatPerTick;
    }

    public void increaseHeal(int amount) {
        healingLeft += amount;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.HEALING);
        else target.sprite.remove(CharSprite.State.HEALING);
    }

    private static final String LEFT = "left";
    private static final String PERCENT = "percent";
    private static final String FLAT = "flat";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, healingLeft);
        bundle.put(PERCENT, percentHealPerTick);
        bundle.put(FLAT, flatHealPerTick);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        healingLeft = bundle.getInt(LEFT);
        percentHealPerTick = bundle.getFloat(PERCENT);
        flatHealPerTick = bundle.getInt(FLAT);
    }

    @Override
    public int icon() {
        return BuffIndicator.HEALING;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", healingThisTick(), healingLeft);
    }
}
