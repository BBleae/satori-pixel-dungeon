package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.CorrosiveGas;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class CorrosionTrap extends Trap {

    {
        color = GREY;
        shape = GRILL;
    }

    @Override
    public void activate() {

        CorrosiveGas corrosiveGas = Blob.seed(pos, 80 + 5 * Dungeon.depth, CorrosiveGas.class);

        corrosiveGas.setStrength(1 + Dungeon.depth / 4);

        GameScene.add(corrosiveGas);

    }
}
