package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Freezing;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class ChillingTrap extends Trap {

    {
        color = WHITE;
        shape = DOTS;
    }

    @Override
    public void activate() {
        if (Dungeon.level.heroFOV[pos]) {
            Splash.at(pos, 0xFFB2D6FF, 5);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        for (int i : PathFinder.NEIGHBOURS9) {
            if (!Dungeon.level.solid[pos + i]) {
                GameScene.add(Blob.seed(pos + i, 10, Freezing.class));
            }
        }
    }
}
