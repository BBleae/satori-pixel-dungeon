package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;

public class SmokeScreen extends Blob {

    @Override
    protected void evolve() {
        super.evolve();

        int cell;

        Level l = Dungeon.level;
        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * l.width();
                l.losBlocking[cell] = off[cell] > 0 || (Terrain.flags[l.map[cell]] & Terrain.LOS_BLOCKING) != 0;
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(Speck.SMOKE), 0.1f);
    }

    @Override
    public void clear(int cell) {
        super.clear(cell);
        Level l = Dungeon.level;
        l.losBlocking[cell] = cur[cell] > 0 || (Terrain.flags[l.map[cell]] & Terrain.LOS_BLOCKING) != 0;
    }

    @Override
    public void fullyClear() {
        super.fullyClear();
        Dungeon.level.buildFlagMaps();
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

}
