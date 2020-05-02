package studio.baka.satoripixeldungeon.items.bags;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class PotionBandolier extends Bag {

    {
        image = ItemSpriteSheet.BANDOLIER;

        size = 20;
    }

    @Override
    public boolean grab(Item item) {
        return item instanceof Potion;
    }

    @Override
    public int price() {
        return 40;
    }

}
