package studio.baka.satoripixeldungeon.tiles;

import studio.baka.satoripixeldungeon.Dungeon;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

public abstract class DungeonTilemap extends Tilemap {

    public static final int SIZE = 16;

    protected int[] map;

    public DungeonTilemap(String tex) {
        super(tex, new TextureFilm(tex, SIZE, SIZE));
    }

    @Override
    //we need to retain two arrays, map is the dungeon tilemap which we can reference.
    // Data is our own internal image representation of the tiles, which may differ.
    public void map(int[] data, int cols) {
        map = data;
        super.map(new int[data.length], cols);
    }

    @Override
    public synchronized void updateMap() {
        super.updateMap();
        for (int i = 0; i < data.length; i++)
            data[i] = getTileVisual(i, map[i], false);
    }

    @Override
    public synchronized void updateMapCell(int cell) {
        //update in a 3x3 grid to account for neighbours which might also be affected
        if (Dungeon.level.insideMap(cell)) {
            super.updateMapCell(cell - mapWidth - 1);
            super.updateMapCell(cell + mapWidth + 1);
            for (int i : PathFinder.NEIGHBOURS9)
                data[cell + i] = getTileVisual(cell + i, map[cell + i], false);

            //unless we're at the level's edge, then just do the one tile.
        } else {
            super.updateMapCell(cell);
            data[cell] = getTileVisual(cell, map[cell], false);
        }
    }

    protected abstract int getTileVisual(int pos, int tile, boolean flat);

    public int screenToTile(int x, int y) {
        return screenToTile(x, y, false);
    }

    //wall assist is used to make raised perspective tapping a bit easier.
    // If the pressed tile is a wall tile, the tap can be 'bumped' down into a none-wall tile.
    // currently this happens if the bottom 1/4 of the wall tile is pressed.
    public int screenToTile(int x, int y, boolean wallAssist) {
        PointF p = camera().screenToCamera(x, y).
                offset(this.point().negate()).
                invScale(SIZE);

        //snap to the edges of the tilemap
        p.x = GameMath.gate(0, p.x, Dungeon.level.width() - 0.001f);
        p.y = GameMath.gate(0, p.y, Dungeon.level.height() - 0.001f);

        int cell = (int) p.x + (int) p.y * Dungeon.level.width();

        if (wallAssist
                && map != null
                && DungeonTileSheet.wallStitcheable(map[cell])) {

            if (cell + mapWidth < size
                    && p.y % 1 >= 0.75f
                    && !DungeonTileSheet.wallStitcheable(map[cell + mapWidth])) {
                cell += mapWidth;
            }

        }

        return cell;
    }

    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    public void discover(int pos, int oldValue) {

        int visual = getTileVisual(pos, oldValue, false);
        if (visual < 0) return;

        final Image tile = new Image(texture);
        tile.frame(tileset.get(getTileVisual(pos, oldValue, false)));
        tile.point(tileToWorld(pos));

        parent.add(tile);

        parent.add(new AlphaTweener(tile, 0, 0.6f) {
            protected void onComplete() {
                tile.killAndErase();
                killAndErase();
            }
        });
    }

    public static PointF tileToWorld(int pos) {
        return new PointF(pos % Dungeon.level.width(), pos / Dungeon.level.width()).scale(SIZE);
    }

    public static PointF tileCenterToWorld(int pos) {
        return new PointF(
                (pos % Dungeon.level.width() + 0.5f) * SIZE,
                (pos / Dungeon.level.width() + 0.5f) * SIZE);
    }

    public static PointF raisedTileCenterToWorld(int pos) {
        return new PointF(
                (pos % Dungeon.level.width() + 0.5f) * SIZE,
                (pos / Dungeon.level.width() + 0.1f) * SIZE);
    }

    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }

}
