package studio.baka.satoripixeldungeon.items.potions.brews;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Electricity;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.PotionOfParalyticGas;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class ShockingBrew extends Brew {

    {
        image = ItemSpriteSheet.BREW_SHOCKING;
    }

    @Override
    public void shatter(int cell) {
        if (Dungeon.level.heroFOV[cell]) {
            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                GameScene.add(Blob.seed(i, 20, Electricity.class));
            }
        }
        Sample.INSTANCE.play(Assets.SND_LIGHTNING);
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (40 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{PotionOfParalyticGas.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = ShockingBrew.class;
            outQuantity = 1;
        }

    }
}
