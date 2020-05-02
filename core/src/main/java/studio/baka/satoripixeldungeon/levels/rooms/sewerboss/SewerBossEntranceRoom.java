package studio.baka.satoripixeldungeon.levels.rooms.sewerboss;

import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.standard.EntranceRoom;

public class SewerBossEntranceRoom extends EntranceRoom {

    @Override
    public int minHeight() {
        return 6;
    }

    @Override
    public int minWidth() {
        return 8;
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        Painter.fill(level, left + 1, top + 1, width() - 2, 1, Terrain.WALL_DECO);
        Painter.fill(level, left + 1, top + 2, width() - 2, 1, Terrain.WATER);

        do {
            level.entrance = level.pointToCell(random(3));
        } while (level.findMob(level.entrance) != null);
        Painter.set(level, level.entrance, Terrain.ENTRANCE);

        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.REGULAR);

            if (door.y == top || door.y == top + 1) {
                Painter.drawInside(level, this, door, 1, Terrain.WATER);
            }
        }

    }

}
