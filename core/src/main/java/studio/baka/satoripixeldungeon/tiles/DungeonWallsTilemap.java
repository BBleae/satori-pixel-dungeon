package studio.baka.satoripixeldungeon.tiles;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.levels.Terrain;

public class DungeonWallsTilemap extends DungeonTilemap {

    public DungeonWallsTilemap() {
        super(Dungeon.level.tilesTex());
        map(Dungeon.level.map, Dungeon.level.width());
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {

        if (flat) return -1;

        if (DungeonTileSheet.wallStitcheable(tile)) {
            if (pos + mapWidth < size && !DungeonTileSheet.wallStitcheable(map[pos + mapWidth])) {

                if (map[pos + mapWidth] == Terrain.DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS;
                } else if (map[pos + mapWidth] == Terrain.LOCKED_DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_LOCKED;
                } else if (map[pos + mapWidth] == Terrain.OPEN_DOOR) {
                    return DungeonTileSheet.NULL_TILE;
                }

            } else {
                return DungeonTileSheet.stitchInternalWallTile(
                        tile,
                        (pos + 1) % mapWidth != 0 ? map[pos + 1] : -1,
                        (pos + 1) % mapWidth != 0 && pos + mapWidth < size ? map[pos + 1 + mapWidth] : -1,
                        pos + mapWidth < size ? map[pos + mapWidth] : -1,
                        pos % mapWidth != 0 && pos + mapWidth < size ? map[pos - 1 + mapWidth] : -1,
                        pos % mapWidth != 0 ? map[pos - 1] : -1
                );
            }

        }

        if (pos + mapWidth < size && DungeonTileSheet.wallStitcheable(map[pos + mapWidth])) {

            return DungeonTileSheet.stitchWallOverhangTile(
                    tile,
                    (pos + 1) % mapWidth != 0 ? map[pos + 1 + mapWidth] : -1,
                    map[pos + mapWidth],
                    pos % mapWidth != 0 ? map[pos - 1 + mapWidth] : -1
            );

        } else if (Dungeon.level.insideMap(pos) && (map[pos + mapWidth] == Terrain.DOOR || map[pos + mapWidth] == Terrain.LOCKED_DOOR)) {
            return DungeonTileSheet.DOOR_OVERHANG;
        } else if (Dungeon.level.insideMap(pos) && map[pos + mapWidth] == Terrain.OPEN_DOOR) {
            return DungeonTileSheet.DOOR_OVERHANG_OPEN;
        } else if (pos + mapWidth < size && (map[pos + mapWidth] == Terrain.STATUE || map[pos + mapWidth] == Terrain.STATUE_SP)) {
            return DungeonTileSheet.STATUE_OVERHANG;
        } else if (pos + mapWidth < size && map[pos + mapWidth] == Terrain.ALCHEMY) {
            return DungeonTileSheet.ALCHEMY_POT_OVERHANG;
        } else if (pos + mapWidth < size && map[pos + mapWidth] == Terrain.BARRICADE) {
            return DungeonTileSheet.BARRICADE_OVERHANG;
        } else if (pos + mapWidth < size && map[pos + mapWidth] == Terrain.HIGH_GRASS) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.HIGH_GRASS_OVERHANG, pos + mapWidth);
        } else if (pos + mapWidth < size && map[pos + mapWidth] == Terrain.FURROWED_GRASS) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.FURROWED_OVERHANG, pos + mapWidth);
        }

        return -1;
    }

    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }

}
