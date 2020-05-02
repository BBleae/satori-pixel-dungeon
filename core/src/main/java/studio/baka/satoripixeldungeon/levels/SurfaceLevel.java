package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Bones;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.noosa.Group;

public class SurfaceLevel extends Level {

    {
        color1 = 0x6a723d;
        color2 = 0x88924c;
    }

    //keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
    //private ArrayList<Item> storedItems = new ArrayList<>();

    @Override
    public String tilesTex() {
        return Assets.TILES_SURFACE;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_SEWERS;
    }

    @Override
    protected boolean build() {

        setSize(10, 9);

        map = MAP_START.clone();

        buildFlagMaps();
        cleanWalls();

        entrance = 7 + 7 * width;
        exit = 4 + 2 * width;

        return true;
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    public Actor respawner() {
        return null;
    }

    public int randomRespawnCell() {
        return entrance;
    }

    @Override
    protected void createItems() {
        Item item = Bones.get();
        if (item != null) {
            drop(item, exit - 1).type = Heap.Type.REMAINS;
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(this, "water_name");
            case Terrain.BOOKSHELF:
                return Messages.get(this, "bookshelf_name");
            case Terrain.EXIT:
            case Terrain.PEDESTAL:
                return Messages.get(this, "exit_name");
            case Terrain.STATUE_SP:
            case Terrain.STATUE:
                return Messages.get(this, "a");
            case Terrain.EMPTY_WELL:
                return Messages.get(this, "c");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(this, "empty_deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(this, "empty_sp_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(this, "bookshelf_desc");
            case Terrain.EXIT:
            case Terrain.PEDESTAL:
                return Messages.get(this, "exit_desc");
            case Terrain.STATUE_SP:
            case Terrain.STATUE:
                return Messages.get(this, "b");
            case Terrain.EMPTY_WELL:
                return Messages.get(this, "d");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CityLevel.addCityVisuals(this, visuals);
        return visuals;
    }

    private static final int W = Terrain.WALL;
    private static final int D = Terrain.DOOR;
    private static final int m = Terrain.EMPTY_SP;
    private static final int B = Terrain.BOOKSHELF;
    private static final int S = Terrain.STATUE_SP;
    private static final int s = Terrain.STATUE;
    private static final int E = Terrain.ENTRANCE;
    private static final int X = Terrain.EXIT;
    private static final int w = Terrain.EMPTY_WELL;
    private static final int P = Terrain.PEDESTAL;

    //我最喜欢字符画了，笨蛋大Evan。
    private static final int[] MAP_START =
            {W, W, W, W, W, W, W, W, W, W,
                    W, B, B, B, B, B, B, B, B, W,
                    W, w, m, P, X, m, m, m, w, W,
                    W, m, m, s, s, s, m, m, m, W,
                    W, m, m, S, S, S, m, m, m, W,
                    W, m, m, m, m, m, m, m, m, W,
                    W, m, m, m, m, m, m, m, m, W,
                    W, w, m, m, m, m, m, m, w, W,
                    W, W, W, W, W, W, W, D, W, W,
            };
}
