package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.actors.buffs.Barkskin;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;

public class PotionOfEarthenArmor extends ExoticPotion {

    {
        initials = 8;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        Buff.affect(hero, Barkskin.class).set(2 + hero.lvl / 3, 50);
    }

}
