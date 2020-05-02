package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ParalyticGas;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class PotionOfParalyticGas extends Potion {

    {
        initials = 8;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        GameScene.add(Blob.seed(cell, 1000, ParalyticGas.class));
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
