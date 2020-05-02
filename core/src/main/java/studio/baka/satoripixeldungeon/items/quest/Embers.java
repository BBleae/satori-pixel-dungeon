package studio.baka.satoripixeldungeon.items.quest;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Embers extends Item {

    {
        image = ItemSpriteSheet.EMBER;

        unique = true;
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
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing(0x660000, 3f);
    }
}
