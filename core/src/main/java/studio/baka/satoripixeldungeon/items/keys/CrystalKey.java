package studio.baka.satoripixeldungeon.items.keys;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class CrystalKey extends Key {

    {
        image = ItemSpriteSheet.CRYSTAL_KEY;
    }

    public CrystalKey() {
        this(0);
    }

    public CrystalKey(int depth) {
        super();
        this.depth = depth;
    }

}
