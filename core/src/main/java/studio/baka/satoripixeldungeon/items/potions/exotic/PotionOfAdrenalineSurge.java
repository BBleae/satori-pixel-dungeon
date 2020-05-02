package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.actors.buffs.AdrenalineSurge;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfAdrenalineSurge extends ExoticPotion {

    {
        initials = 10;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect(hero, AdrenalineSurge.class).reset(2, 800f);
    }

}
