package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ChargrilledMeat extends Food {

    {
        image = ItemSpriteSheet.STEAK;
        energy = Hunger.HUNGRY / 2f;
    }

    @Override
    public int price() {
        return 8 * quantity;
    }

    public static Food cook(MysteryMeat ingredient) {
        ChargrilledMeat result = new ChargrilledMeat();
        result.quantity = ingredient.quantity();
        return result;
    }
}
