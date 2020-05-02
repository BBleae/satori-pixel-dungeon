package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfExperience extends Potion {

    {
        initials = 0;

        bones = true;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        hero.earnExp(hero.maxExp(), getClass());
    }

    @Override
    public int price() {
        return isKnown() ? 50 * quantity : super.price();
    }
}
