package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;

public class BurningTrap extends Trap {

    {
        color = ORANGE;
        shape = DOTS;
    }

    @Override
    public void activate() {

        for (int i : PathFinder.NEIGHBOURS9) {
            if (!Dungeon.level.solid[pos + i]) {
                GameScene.add(Blob.seed(pos + i, 2, Fire.class));
                CellEmitter.get(pos + i).burst(FlameParticle.FACTORY, 5);
            }
        }

    }
}
