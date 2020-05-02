package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfUpgrade;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;

public class MagicalInfusion extends InventorySpell {

    {
        mode = WndBag.Mode.UPGRADEABLE;
        image = ItemSpriteSheet.MAGIC_INFUSE;
    }

    @Override
    protected void onItemSelected(Item item) {

        if (item instanceof Weapon && ((Weapon) item).enchantment != null && !((Weapon) item).hasCurseEnchant()) {
            ((Weapon) item).upgrade(true);
        } else if (item instanceof Armor && ((Armor) item).glyph != null && !((Armor) item).hasCurseGlyph()) {
            ((Armor) item).upgrade(true);
        } else {
            item.upgrade();
        }

        GLog.p(Messages.get(this, "infuse", item.name()));

        Badges.validateItemLevelAquired(item);

        curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
        Statistics.upgradesUsed++;
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((50 + 40) / 1f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{ScrollOfUpgrade.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 4;

            output = MagicalInfusion.class;
            outQuantity = 1;
        }

    }
}
