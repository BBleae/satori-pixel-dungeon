package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.AmuletFake;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;

public class LastLevel extends Level {

    {
        color1 = 0x801500;
        color2 = 0xa68521;
    }

    private int pedestal;

    @Override
    public String tilesTex() {
        return Assets.TILES_HALLS;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_HALLS;
    }

    @Override
    public void create() {
        super.create();
        for (int i = 0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            if ((flags & Terrain.PIT) != 0) {
                passable[i] = avoid[i] = false;
                solid[i] = true;
            }
        }
    }

    @Override
    protected boolean build() {

        setSize(16, 64);
        Arrays.fill(map, Terrain.CHASM);

        int mid = width / 2;

        Painter.fill(this, 0, height - 1, width, 1, Terrain.WALL);
        Painter.fill(this, mid - 1, 10, 3, (height - 11), Terrain.EMPTY);
        Painter.fill(this, mid - 2, height - 3, 5, 1, Terrain.EMPTY);
        Painter.fill(this, mid - 3, height - 2, 7, 1, Terrain.EMPTY);

        Painter.fill(this, mid - 2, 9, 5, 7, Terrain.EMPTY);
        Painter.fill(this, mid - 3, 10, 7, 5, Terrain.EMPTY);

        //entrance = (height-2) * width() + mid;
        entrance = getPos(mid, height - 2);

        //Painter.fill( this, mid - 1,17,5,1,Terrain.CHASM);
        map[entrance] = Terrain.ENTRANCE;

        pedestal = 12 * (width()) + mid;
        map[pedestal] = Terrain.PEDESTAL;
        map[pedestal - 1 - width()] = map[pedestal + 1 - width()] = map[pedestal - 1 + width()] = map[pedestal + 1 + width()] = Terrain.STATUE_SP;

        exit = pedestal;
        //map[exit] = Terrain.EXIT;

        int pos = pedestal;

        map[pos - width()] = map[pos - 1] = map[pos + 1] = map[pos - 2] = map[pos + 2] = Terrain.WATER;
        pos += width();
        map[pos] = map[pos - 2] = map[pos + 2] = map[pos - 3] = map[pos + 3] = Terrain.WATER;
        pos += width();
        map[pos - 3] = map[pos - 2] = map[pos - 1] = map[pos] = map[pos + 1] = map[pos + 2] = map[pos + 3] = Terrain.WATER;
        pos += width();
        map[pos - 2] = map[pos + 2] = Terrain.WATER;

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO;
            }
        }

        feeling = Feeling.NONE;

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

    @Override
    protected void createItems() {
        drop(new AmuletFake(), pedestal);
    }

    @Override
    public int randomRespawnCell() {
        int cell;
        do {
            cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
        } while (!passable[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallsLevel.class, "water_name");
            case Terrain.GRASS:
                return Messages.get(HallsLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(HallsLevel.class, "high_grass_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallsLevel.class, "statue_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallsLevel.class, "water_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallsLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(HallsLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        HallsLevel.addHallsVisuals(this, visuals);
        return visuals;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        for (int i = 0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            if ((flags & Terrain.PIT) != 0) {
                passable[i] = avoid[i] = false;
                solid[i] = true;
            }
        }
    }
}
