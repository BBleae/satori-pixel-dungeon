package studio.baka.satoripixeldungeon.items.quest;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

//this one's still hanging around to support quests from old saves
//I may reuse it at some point.
public class RatSkull extends Item {

    {
        image = ItemSpriteSheet.SKULL;

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
