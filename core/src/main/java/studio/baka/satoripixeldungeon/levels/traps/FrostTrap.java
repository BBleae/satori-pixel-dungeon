package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Freezing;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class FrostTrap extends Trap {

    {
        color = WHITE;
        shape = STARS;
    }

    @Override
    public void activate() {

        if (Dungeon.level.heroFOV[pos]) {
            Splash.at(pos, 0xFFB2D6FF, 5);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                GameScene.add(Blob.seed(i, 20, Freezing.class));
            }
        }
    }
}
