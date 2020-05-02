package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Electricity;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class ShockingTrap extends Trap {

    {
        color = YELLOW;
        shape = DOTS;
    }

    @Override
    public void activate() {

        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play(Assets.SND_LIGHTNING);
        }

        for (int i : PathFinder.NEIGHBOURS9) {
            if (!Dungeon.level.solid[pos + i]) {
                GameScene.add(Blob.seed(pos + i, 10, Electricity.class));
            }
        }
    }

}
