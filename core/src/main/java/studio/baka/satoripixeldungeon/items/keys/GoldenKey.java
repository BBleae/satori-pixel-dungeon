package studio.baka.satoripixeldungeon.items.keys;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class GoldenKey extends Key {

    {
        image = ItemSpriteSheet.GOLDEN_KEY;
    }

    public GoldenKey() {
        this(0);
    }

    public GoldenKey(int depth) {
        super();
        this.depth = depth;
    }

}
