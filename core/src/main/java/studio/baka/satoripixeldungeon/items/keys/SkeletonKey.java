package studio.baka.satoripixeldungeon.items.keys;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class SkeletonKey extends Key {

    {
        image = ItemSpriteSheet.SKELETON_KEY;
    }

    public SkeletonKey() {
        this(0);
    }

    public SkeletonKey(int depth) {
        super();
        this.depth = depth;
    }

}
