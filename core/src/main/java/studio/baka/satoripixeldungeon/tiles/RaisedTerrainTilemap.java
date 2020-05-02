package studio.baka.satoripixeldungeon.tiles;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.levels.Terrain;

public class RaisedTerrainTilemap extends DungeonTilemap {

    public RaisedTerrainTilemap() {
        super(Dungeon.level.tilesTex());
        map(Dungeon.level.map, Dungeon.level.width());
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {

        if (flat) return -1;

        if (tile == Terrain.HIGH_GRASS) {
            return DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.RAISED_HIGH_GRASS,
                    pos) + 2;
        } else if (tile == Terrain.FURROWED_GRASS) {
            return DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.RAISED_FURROWED_GRASS,
                    pos) + 2;
        }


        return -1;
    }
}
