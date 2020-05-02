package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.MagicalSight;
import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfMagicalSight extends ExoticPotion {

    {
        initials = 7;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect(hero, MagicalSight.class, MagicalSight.DURATION);
        Dungeon.observe();

    }

}
