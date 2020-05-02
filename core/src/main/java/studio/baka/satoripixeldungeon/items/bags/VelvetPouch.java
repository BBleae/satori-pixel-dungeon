package studio.baka.satoripixeldungeon.items.bags;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class VelvetPouch extends Bag {

    {
        image = ItemSpriteSheet.POUCH;

        size = 20;
    }

    @Override
    public boolean grab(Item item) {
        return item instanceof Plant.Seed || item instanceof Runestone;
    }

    @Override
    public int price() {
        return 30;
    }

}
