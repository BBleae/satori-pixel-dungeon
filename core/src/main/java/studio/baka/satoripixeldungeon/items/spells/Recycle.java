package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.brews.Brew;
import studio.baka.satoripixeldungeon.items.potions.elixirs.Elixir;
import studio.baka.satoripixeldungeon.items.potions.exotic.ExoticPotion;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTransmutation;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.utils.Reflection;

public class Recycle extends InventorySpell {

    {
        image = ItemSpriteSheet.RECYCLE;
        mode = WndBag.Mode.RECYCLABLE;
    }

    @Override
    protected void onItemSelected(Item item) {
        Item result;
        do {
            if (item instanceof Potion) {
                result = Generator.random(Generator.Category.POTION);
                if (item instanceof ExoticPotion) {
                    result = Reflection.newInstance(ExoticPotion.regToExo.get(result.getClass()));
                }
            } else if (item instanceof Scroll) {
                result = Generator.random(Generator.Category.SCROLL);
                if (item instanceof ExoticScroll) {
                    result = Reflection.newInstance(ExoticScroll.regToExo.get(result.getClass()));
                }
            } else if (item instanceof Plant.Seed) {
                result = Generator.random(Generator.Category.SEED);
            } else {
                result = Generator.random(Generator.Category.STONE);
            }
        } while (result.getClass() == item.getClass() || Challenges.isItemBlocked(result));

        item.detach(curUser.belongings.backpack);
        GLog.p(Messages.get(this, "recycled", result.name()));
        if (!result.collect()) {
            Dungeon.level.drop(result, curUser.pos).sprite.drop();
        }
        //TODO visuals
    }

    public static boolean isRecyclable(Item item) {
        return (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew)) ||
                item instanceof Scroll ||
                item instanceof Plant.Seed ||
                item instanceof Runestone;
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((50 + 40) / 8f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfTransmutation.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = Recycle.class;
            outQuantity = 8;
        }

    }
}
