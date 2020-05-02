package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.effects.Enchanting;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;

public class StoneOfEnchantment extends InventoryStone {

    {
        mode = WndBag.Mode.ENCHANTABLE;
        image = ItemSpriteSheet.STONE_ENCHANT;
    }

    @Override
    protected void onItemSelected(Item item) {

        if (item instanceof Weapon) {

            ((Weapon) item).enchant();

        } else {

            ((Armor) item).inscribe();

        }

        curUser.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.1f, 5);
        Enchanting.show(curUser, item);

        if (item instanceof Weapon) {
            GLog.p(Messages.get(this, "weapon"));
        } else {
            GLog.p(Messages.get(this, "armor"));
        }

        useAnimation();

    }

    @Override
    public int price() {
        return 30 * quantity;
    }
}
