package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Freezing;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfFrost extends Potion {

    {
        initials = 1;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        for (int offset : PathFinder.NEIGHBOURS9) {
            if (!Dungeon.level.solid[cell + offset]) {

                GameScene.add(Blob.seed(cell + offset, 10, Freezing.class));

            }
        }

    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
