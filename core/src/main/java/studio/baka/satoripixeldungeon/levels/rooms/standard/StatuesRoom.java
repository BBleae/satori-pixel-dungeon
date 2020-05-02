package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;

public class StatuesRoom extends StandardRoom {

    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{9, 3, 1};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }


        int rows = (width() + 1) / 6;
        int cols = (height() + 1) / 6;

        int w = (width() - 4 - (rows - 1)) / rows;
        int h = (height() - 4 - (cols - 1)) / cols;

        int Wspacing = rows % 2 == width() % 2 ? 2 : 1;
        int Hspacing = cols % 2 == height() % 2 ? 2 : 1;

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int left = this.left + 2 + (x * (w + Wspacing));
                int top = this.top + 2 + (y * (h + Hspacing));

                Painter.fill(level, left, top, w, h, Terrain.EMPTY_SP);

                Painter.set(level, left, top, Terrain.STATUE_SP);
                Painter.set(level, left + w - 1, top, Terrain.STATUE_SP);
                Painter.set(level, left, top + h - 1, Terrain.STATUE_SP);
                Painter.set(level, left + w - 1, top + h - 1, Terrain.STATUE_SP);
            }
        }

    }
}
