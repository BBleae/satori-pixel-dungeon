package studio.baka.satoripixeldungeon.levels.rooms.sewerboss;

import studio.baka.satoripixeldungeon.actors.mobs.Goo;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.connection.PerimeterRoom;

public class ThinPillarsGooRoom extends GooBossRoom {

    @Override
    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.WATER);

        int pillarW = (width() == 14 ? 4 : 2) + width() % 2;
        int pillarH = (height() == 14 ? 4 : 2) + height() % 2;

        if (height() < 12) {
            Painter.fill(level, left + (width() - pillarW) / 2, top + 2, pillarW, 1, Terrain.WALL);
            Painter.fill(level, left + (width() - pillarW) / 2, bottom - 2, pillarW, 1, Terrain.WALL);
        } else {
            Painter.fill(level, left + (width() - pillarW) / 2, top + 3, pillarW, 1, Terrain.WALL);
            Painter.fill(level, left + (width() - pillarW) / 2, bottom - 3, pillarW, 1, Terrain.WALL);
        }

        if (width() < 12) {
            Painter.fill(level, left + 2, top + (height() - pillarH) / 2, 1, pillarH, Terrain.WALL);
            Painter.fill(level, right - 2, top + (height() - pillarH) / 2, 1, pillarH, Terrain.WALL);
        } else {
            Painter.fill(level, left + 3, top + (height() - pillarH) / 2, 1, pillarH, Terrain.WALL);
            Painter.fill(level, right - 3, top + (height() - pillarH) / 2, 1, pillarH, Terrain.WALL);
        }

        PerimeterRoom.fillPerimiterPaths(level, this, Terrain.EMPTY_SP);

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }

        setupGooNest(level);

        Goo boss = new Goo();
        boss.pos = level.pointToCell(center());
        level.mobs.add(boss);

    }

}
