package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Stamina;
import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfStamina extends ExoticPotion {

    {
        initials = 2;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        Buff.affect(hero, Stamina.class, 100f);
    }

}
