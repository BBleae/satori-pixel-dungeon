package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.actors.buffs.Bless;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfHolyFuror extends ExoticPotion {

    {
        initials = 0;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.prolong(hero, Bless.class, 100f);
    }

}
