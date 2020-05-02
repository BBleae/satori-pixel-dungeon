package studio.baka.satoripixeldungeon.items.quest;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class DwarfToken extends Item {

    {
        image = ItemSpriteSheet.TOKEN;

        stackable = true;
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
}
