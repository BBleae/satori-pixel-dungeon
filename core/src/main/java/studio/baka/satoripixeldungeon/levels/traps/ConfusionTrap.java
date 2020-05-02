package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ConfusionGas;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class ConfusionTrap extends Trap {

    {
        color = TEAL;
        shape = GRILL;
    }

    @Override
    public void activate() {

        GameScene.add(Blob.seed(pos, 300 + 20 * Dungeon.depth, ConfusionGas.class));

    }
}
