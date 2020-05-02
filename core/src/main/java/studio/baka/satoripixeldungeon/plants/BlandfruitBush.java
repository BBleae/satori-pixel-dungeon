package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.food.Blandfruit;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class BlandfruitBush extends Plant {

    {
        image = 12;
    }

    @Override
    public void activate(Char ch) {
        Dungeon.level.drop(new Blandfruit(), pos).sprite.drop();
    }

    //This seed no longer drops, but has a sprite as it did drop prior to 0.7.0
    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_FADELEAF;

            plantClass = BlandfruitBush.class;
        }

    }
}
