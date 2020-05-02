package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;

public class RingRoom extends StandardRoom {

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{9, 3, 1};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        int minDim = Math.min(width(), height());
        int passageWidth = (int) Math.floor(0.25f * (minDim + 1));
        Painter.fill(level, this, passageWidth + 1, Terrain.WALL);

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }
    }
}
