package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.items.quest.CeremonialCandle;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;

public class RitualSiteRoom extends StandardRoom {

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 5);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 5);
    }

    public void paint(Level level) {

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        RitualMarker vis = new RitualMarker();
        Point c = center();
        vis.pos(c.x - 1, c.y - 1);

        level.customTiles.add(vis);

        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMPTY_DECO);

        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());

        CeremonialCandle.ritualPos = c.x + (level.width() * c.y);
    }

    public static class RitualMarker extends CustomTilemap {

        {
            texture = Assets.PRISON_QUEST;

            tileW = tileH = 3;
        }

        final int TEX_WIDTH = 64;

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(mapSimpleImage(0, 0, TEX_WIDTH), 3);
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
