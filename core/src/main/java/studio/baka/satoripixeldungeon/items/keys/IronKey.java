package studio.baka.satoripixeldungeon.items.keys;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class IronKey extends Key {

    {
        image = ItemSpriteSheet.IRON_KEY;
    }

    public IronKey() {
        this(0);
    }

    public IronKey(int depth) {
        super();
        this.depth = depth;
    }

}
