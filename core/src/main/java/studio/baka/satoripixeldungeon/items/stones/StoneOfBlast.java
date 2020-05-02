package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class StoneOfBlast extends Runestone {

    {
        image = ItemSpriteSheet.STONE_BLAST;
    }

    @Override
    protected void activate(int cell) {
        new Bomb().explode(cell);
    }

}
