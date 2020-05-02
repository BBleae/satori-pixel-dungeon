package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;

public class SkullsRoom extends StandardRoom {

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

        int minDim = Math.min(width(), height());

        Painter.fill(level, this, Terrain.WALL);

        if (minDim >= 9) {
            Painter.fillEllipse(level, this, 2, Terrain.EMPTY);
        } else {
            Painter.fill(level, this, 2, Terrain.EMPTY);
        }

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            if (door.x == left || door.x == right) {
                Painter.drawInside(level, this, door, (width() - 3) / 2, Terrain.EMPTY);
            } else {
                Painter.drawInside(level, this, door, (height() - 3) / 2, Terrain.EMPTY);
            }
        }

        boolean oddWidth = width() % 2 == 1;
        boolean oddHeight = height() % 2 == 1;

        if (minDim >= 12) {

            Painter.fillEllipse(level, this, 5, Terrain.STATUE);
            Painter.fillEllipse(level, this, 6, Terrain.WALL);

        } else {

            Painter.fill(level,
                    left + width() / 2 + (oddWidth ? 0 : -1),
                    top + height() / 2 + (oddHeight ? 0 : -1),
                    oddWidth ? 1 : 2,
                    oddHeight ? 1 : 2,
                    Terrain.STATUE);

        }

    }
}
