package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.PotionOfStrength;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class ElixirOfMight extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_MIGHT;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        hero.STR++;

        Buff.affect(hero, HTBoost.class).reset();
        HTBoost boost = Buff.affect(hero, HTBoost.class);
        boost.reset();

        hero.updateHT(true);
        hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_1", boost.boost()));
        GLog.p(Messages.get(this, "msg_2"));

        Badges.validateStrengthAttained();
    }

    public String desc() {
        return Messages.get(this, "desc", HTBoost.boost(Dungeon.hero.HT));
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (50 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{PotionOfStrength.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 5;

            output = ElixirOfMight.class;
            outQuantity = 1;
        }

    }

    public static class HTBoost extends Buff {

        {
            type = buffType.POSITIVE;
        }

        private int left;

        public void reset() {
            left = 5;
        }

        public int boost() {
            return Math.round(left * boost(target.HT) / 5f);
        }

        public static int boost(int HT) {
            return Math.round(4 + HT / 20f);
        }

        public void onLevelUp() {
            left--;
            if (left <= 0) {
                detach();
            }
        }

        @Override
        public int icon() {
            return BuffIndicator.HEALING;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", boost(), left);
        }

        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getInt(LEFT);
        }
    }
}
