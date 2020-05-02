package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class SmallRation extends Food {

    {
        image = ItemSpriteSheet.OVERPRICED;
        energy = Hunger.HUNGRY / 2f;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }
}
