package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class MysteryMeat extends Food {

    {
        image = ItemSpriteSheet.MEAT;
        energy = Hunger.HUNGRY / 2f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        effect(hero);
    }

    public int price() {
        return 5 * quantity;
    }

    public static void effect(Hero hero) {
        switch (Random.Int(5)) {
            case 0:
                GLog.w(Messages.get(MysteryMeat.class, "hot"));
                Buff.affect(hero, Burning.class).reignite(hero);
                break;
            case 1:
                GLog.w(Messages.get(MysteryMeat.class, "legs"));
                Buff.prolong(hero, Roots.class, Paralysis.DURATION);
                break;
            case 2:
                GLog.w(Messages.get(MysteryMeat.class, "not_well"));
                Buff.affect(hero, Poison.class).set(hero.HT / 5);
                break;
            case 3:
                GLog.w(Messages.get(MysteryMeat.class, "stuffed"));
                Buff.prolong(hero, Slow.class, Slow.DURATION);
                break;
        }
    }

    public static class PlaceHolder extends MysteryMeat {

        {
            image = ItemSpriteSheet.FOOD_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof MysteryMeat || item instanceof StewedMeat
                    || item instanceof ChargrilledMeat || item instanceof FrozenCarpaccio;
        }

        @Override
        public String info() {
            return "";
        }
    }
}
