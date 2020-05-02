package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.scenes.AlchemyScene;

public class Alchemy extends Blob implements AlchemyScene.AlchemyProvider {

    protected int pos;

    @Override
    protected void evolve() {
        int cell;
        for (int i = area.top - 1; i <= area.bottom; i++) {
            for (int j = area.left - 1; j <= area.right; j++) {
                cell = j + i * Dungeon.level.width();
                if (Dungeon.level.insideMap(cell)) {
                    off[cell] = cur[cell];
                    volume += off[cell];
                    if (off[cell] > 0 && Dungeon.level.heroFOV[cell]) {
                        Notes.add(Notes.Landmark.ALCHEMY);
                    }
                }
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(Speck.BUBBLE), 0.33f, 0);
    }

    public static int alchPos;

    //1 volume is kept in reserve

    @Override
    public int getEnergy() {
        return Math.max(0, cur[alchPos] - 1);
    }

    @Override
    public void spendEnergy(int reduction) {
        cur[alchPos] = Math.max(1, cur[alchPos] - reduction);
    }
}
