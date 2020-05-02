package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;

public class PotionOfHealing extends Potion {

    {
        initials = 3;

        bones = true;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        //starts out healing 30 hp, equalizes with hero health total at level 11
        Buff.affect(hero, Healing.class).setHeal((int) (0.8f * hero.HT + 14), 0.25f, 0);
        cure(hero);
        GLog.p(Messages.get(this, "heal"));
    }

    public static void cure(Char ch) {
        Buff.detach(ch, Poison.class);
        Buff.detach(ch, Cripple.class);
        Buff.detach(ch, Weakness.class);
        Buff.detach(ch, Bleeding.class);

    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
