package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.StormCloud;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class PotionOfStormClouds extends ExoticPotion {

    {
        initials = 5;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        GameScene.add(Blob.seed(cell, 1000, StormCloud.class));
    }
}
