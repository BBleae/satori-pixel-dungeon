package studio.baka.satoripixeldungeon.items.quest;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class MetalShard extends Item {

    {
        image = ItemSpriteSheet.SHARD;
        stackable = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int price() {
        return quantity * 100;
    }
}
