package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class ToxicTrap extends Trap {

    {
        color = GREEN;
        shape = GRILL;
    }

    @Override
    public void activate() {

        GameScene.add(Blob.seed(pos, 300 + 20 * Dungeon.depth, ToxicGas.class));

    }
}
