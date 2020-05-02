package studio.baka.satoripixeldungeon.items.potions.brews;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Inferno;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.PotionOfLiquidFlame;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class InfernalBrew extends Brew {

    {
        image = ItemSpriteSheet.BREW_INFERNAL;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        GameScene.add(Blob.seed(cell, 1000, Inferno.class));
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (30 + 40);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{PotionOfLiquidFlame.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = InfernalBrew.class;
            outQuantity = 1;
        }

    }
}
