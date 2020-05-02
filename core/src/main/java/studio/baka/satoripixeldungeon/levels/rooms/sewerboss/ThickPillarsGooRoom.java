package studio.baka.satoripixeldungeon.levels.rooms.sewerboss;

import studio.baka.satoripixeldungeon.actors.mobs.Goo;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.connection.PerimeterRoom;

public class ThickPillarsGooRoom extends GooBossRoom {

    @Override
    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.WATER);

        int pillarW = (width() - 8) / 2;
        int pillarH = (height() - 8) / 2;

        Painter.fill(level, left + 2, top + 2, pillarW + 1, pillarH + 1, Terrain.WALL);
        Painter.fill(level, left + 2, bottom - 2 - pillarH, pillarW + 1, pillarH + 1, Terrain.WALL);
        Painter.fill(level, right - 2 - pillarW, top + 2, pillarW + 1, pillarH + 1, Terrain.WALL);
        Painter.fill(level, right - 2 - pillarW, bottom - 2 - pillarH, pillarW + 1, pillarH + 1, Terrain.WALL);

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
