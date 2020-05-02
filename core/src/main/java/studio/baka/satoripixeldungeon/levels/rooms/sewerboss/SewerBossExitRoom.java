package studio.baka.satoripixeldungeon.levels.rooms.sewerboss;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.standard.ExitRoom;
import studio.baka.satoripixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;

public class SewerBossExitRoom extends ExitRoom {

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 8);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 8);
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        Point c = center();

        Painter.fill(level, c.x - 1, c.y - 1, 3, 2, Terrain.WALL);
        Painter.fill(level, c.x - 1, c.y + 1, 3, 1, Terrain.EMPTY_SP);

        level.exit = level.pointToCell(c);
        Painter.set(level, level.exit, Terrain.LOCKED_EXIT);

        CustomTilemap vis = new SewerExit();
        vis.pos(c.x - 1, c.y);
        level.customTiles.add(vis);

        vis = new SewerExitOverhang();
        vis.pos(c.x - 1, c.y - 2);
        level.customWalls.add(vis);

    }

    public static class SewerExit extends CustomTilemap {

        {
            texture = Assets.SEWER_BOSS;

            tileW = 3;
            tileH = 3;
        }

        private static final int[] layout = new int[]{
                21, -1, 22,
                23, 23, 23,
                24, 24, 24
        };

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(layout, 3);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            if ((tileX == 1 && tileY == 0) || tileY == 2) {
                return null;
            }
            return super.image(tileX, tileY);
        }
    }

    public static class SewerExitOverhang extends CustomTilemap {

        {
            texture = Assets.SEWER_BOSS;

            tileW = 3;
            tileH = 2;
        }

        private static final int[] layout = new int[]{
                16, 17, 18,
                19, -1, 20
        };

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(layout, 3);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }
}
