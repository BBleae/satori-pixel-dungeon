package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class StewedMeat extends Food {

    {
        image = ItemSpriteSheet.STEWED;
        energy = Hunger.HUNGRY / 2f;
    }

    @Override
    public int price() {
        return 8 * quantity;
    }

    public static class oneMeat extends Recipe.SimpleRecipe {
        {
            //noinspection unchecked
            inputs = new Class[]{MysteryMeat.class};
            inQuantity = new int[]{1};

            cost = 2;

            output = StewedMeat.class;
            outQuantity = 1;
        }
    }

    public static class twoMeat extends Recipe.SimpleRecipe {
        {
            //noinspection unchecked
            inputs = new Class[]{MysteryMeat.class};
            inQuantity = new int[]{2};

            cost = 3;

            output = StewedMeat.class;
            outQuantity = 2;
        }
    }

    //red meat
    //blue meat

    public static class threeMeat extends Recipe.SimpleRecipe {
        {
            //noinspection unchecked
            inputs = new Class[]{MysteryMeat.class};
            inQuantity = new int[]{3};

            cost = 4;

            output = StewedMeat.class;
            outQuantity = 3;
        }
    }

}
