package studio.baka.satoripixeldungeon.levels.rooms.sewerboss;

import studio.baka.satoripixeldungeon.actors.mobs.Goo;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class DiamondGooRoom extends GooBossRoom {

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);

        //we want the end width to be width()-2, and the width will grow by a total of (height()-4 - height()%2)
        int diamondWidth = width() - 2 - (height() - 4 - height() % 2);
        //but starting width cannot be smaller than 2 on even width, 3 on odd width.
        diamondWidth = Math.max(diamondWidth, width() % 2 == 0 ? 2 : 3);

        for (int i = 1; i < height(); i++) {
            Painter.fill(level, left + (width() - diamondWidth) / 2, top + i, diamondWidth, height() - 2 * i, Terrain.EMPTY);
            diamondWidth += 2;
            if (diamondWidth >= width()) break;
        }

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            Point dir;
            if (door.x == left) {
                dir = new Point(1, 0);
            } else if (door.y == top) {
                dir = new Point(0, 1);
            } else if (door.x == right) {
                dir = new Point(-1, 0);
            } else {
                dir = new Point(0, -1);
            }

            Point curr = new Point(door);
            do {
                Painter.set(level, curr, Terrain.EMPTY_SP);
                curr.x += dir.x;
                curr.y += dir.y;
            } while (level.map[level.pointToCell(curr)] == Terrain.WALL);
        }

        Painter.fill(level, left + width() / 2 - 1, top + height() / 2 - 2, 2 + width() % 2, 4 + height() % 2, Terrain.WATER);
        Painter.fill(level, left + width() / 2 - 2, top + height() / 2 - 1, 4 + width() % 2, 2 + height() % 2, Terrain.WATER);

        setupGooNest(level);

        Goo boss = new Goo();
        boss.pos = level.pointToCell(center());
        level.mobs.add(boss);
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }
}
