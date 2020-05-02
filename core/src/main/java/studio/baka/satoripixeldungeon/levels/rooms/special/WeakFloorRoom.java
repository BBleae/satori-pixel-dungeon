package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.Objects;

public class WeakFloorRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.CHASM);

        Door door = entrance();
        door.set(Door.Type.REGULAR);

        Point well = null;

        if (door.x == left) {
            for (int i = top + 1; i < bottom; i++) {
                Painter.drawInside(level, this, new Point(left, i), Random.IntRange(1, width() - 4), Terrain.EMPTY_SP);
            }
            well = new Point(right - 1, Random.Int(2) == 0 ? top + 2 : bottom - 1);
        } else if (door.x == right) {
            for (int i = top + 1; i < bottom; i++) {
                Painter.drawInside(level, this, new Point(right, i), Random.IntRange(1, width() - 4), Terrain.EMPTY_SP);
            }
            well = new Point(left + 1, Random.Int(2) == 0 ? top + 2 : bottom - 1);
        } else if (door.y == top) {
            for (int i = left + 1; i < right; i++) {
                Painter.drawInside(level, this, new Point(i, top), Random.IntRange(1, height() - 4), Terrain.EMPTY_SP);
            }
            well = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
        } else if (door.y == bottom) {
            for (int i = left + 1; i < right; i++) {
                Painter.drawInside(level, this, new Point(i, bottom), Random.IntRange(1, height() - 4), Terrain.EMPTY_SP);
            }
            well = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 2);
        }

        Painter.set(level, Objects.requireNonNull(well), Terrain.CHASM);
        CustomTilemap vis = new HiddenWell();
        vis.pos(well.x, well.y);
        level.customTiles.add(vis);
    }

    public static class HiddenWell extends CustomTilemap {

        {
            texture = Assets.WEAK_FLOOR;
            tileW = tileH = 1;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(new int[]{Dungeon.depth / 5}, 1);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name");
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc");
        }

    }
}
