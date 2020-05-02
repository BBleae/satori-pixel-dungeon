package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class BlazingTrap extends Trap {

    {
        color = ORANGE;
        shape = STARS;
    }


    @Override
    public void activate() {
        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.pit[i] || Dungeon.level.water[i])
                    GameScene.add(Blob.seed(i, 1, Fire.class));
                else
                    GameScene.add(Blob.seed(i, 5, Fire.class));
                CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
            }
        }
        Sample.INSTANCE.play(Assets.SND_BURNING);
    }
}
