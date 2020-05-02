package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.actors.buffs.Barrier;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfShielding extends ExoticPotion {

    {
        initials = 3;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        //~75% of a potion of healing
        Buff.affect(hero, Barrier.class).setShield((int) (0.6f * hero.HT + 10));
    }
}
