package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Statue;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class StatueRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        Point c = center();
        int cx = c.x;
        int cy = c.y;

        Door door = entrance();

        door.set(Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));

        if (door.x == left) {

            Painter.fill(level, right - 1, top + 1, 1, height() - 2, Terrain.STATUE);
            cx = right - 2;

        } else if (door.x == right) {

            Painter.fill(level, left + 1, top + 1, 1, height() - 2, Terrain.STATUE);
            cx = left + 2;

        } else if (door.y == top) {

            Painter.fill(level, left + 1, bottom - 1, width() - 2, 1, Terrain.STATUE);
            cy = bottom - 2;

        } else if (door.y == bottom) {

            Painter.fill(level, left + 1, top + 1, width() - 2, 1, Terrain.STATUE);
            cy = top + 2;

        }

        Statue statue = new Statue();
        statue.pos = cx + cy * level.width();
        level.mobs.add(statue);
    }
}
