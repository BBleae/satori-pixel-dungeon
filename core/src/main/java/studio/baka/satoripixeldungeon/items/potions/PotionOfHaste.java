package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Haste;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;

public class PotionOfHaste extends Potion {

    {
        initials = 2;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        GLog.w(Messages.get(this, "energetic"));
        Buff.prolong(hero, Haste.class, Haste.DURATION);
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
