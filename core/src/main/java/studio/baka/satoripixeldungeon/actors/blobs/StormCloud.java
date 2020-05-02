package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class StormCloud extends Blob {

    @Override
    protected void evolve() {
        super.evolve();

        int cell;

        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (off[cell] > 0) {
                    int terr = Dungeon.level.map[cell];
                    if (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
                            terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
                            terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS
                            || terr == Terrain.EMPTY_DECO) {
                        Level.set(cell, Terrain.WATER);
                        GameScene.updateMap(cell);
                    } else if (terr == Terrain.SECRET_TRAP || terr == Terrain.TRAP || terr == Terrain.INACTIVE_TRAP) {
                        Level.set(cell, Terrain.WATER);
                        Dungeon.level.traps.remove(cell);
                        GameScene.updateMap(cell);
                    }
                }
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(Speck.STORM), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

}
