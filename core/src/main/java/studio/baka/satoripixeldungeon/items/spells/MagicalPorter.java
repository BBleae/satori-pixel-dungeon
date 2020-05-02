package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.MerchantsBeacon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;

import java.util.ArrayList;

public class MagicalPorter extends InventorySpell {

    {
        image = ItemSpriteSheet.MAGIC_PORTER;
        mode = WndBag.Mode.NOT_EQUIPPED;
    }

    @Override
    protected void onCast(Hero hero) {
        if (Dungeon.depth >= 25) {
            GLog.w(Messages.get(this, "nowhere"));
        } else {
            super.onCast(hero);
        }
    }

    @Override
    protected void onItemSelected(Item item) {

        Item result = item.detachAll(curUser.belongings.backpack);
        int portDepth = 5 * (1 + Dungeon.depth / 5);
        ArrayList<Item> ported = Dungeon.portedItems.get(portDepth);
        if (ported == null) {
            Dungeon.portedItems.put(portDepth, ported = new ArrayList<>());
        }
        ported.add(result);

    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((5 + 40) / 8f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{MerchantsBeacon.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 4;

            output = MagicalPorter.class;
            outQuantity = 8;
        }

    }
}
