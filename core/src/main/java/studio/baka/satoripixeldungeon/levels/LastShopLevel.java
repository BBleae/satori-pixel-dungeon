package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Bones;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.levels.builders.Builder;
import studio.baka.satoripixeldungeon.levels.builders.LineBuilder;
import studio.baka.satoripixeldungeon.levels.painters.CityPainter;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.standard.EntranceRoom;
import studio.baka.satoripixeldungeon.levels.rooms.standard.ExitRoom;
import studio.baka.satoripixeldungeon.levels.rooms.standard.ImpShopRoom;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.noosa.Group;

import java.util.ArrayList;

public class LastShopLevel extends RegularLevel {

    {
        color1 = 0x4b6636;
        color2 = 0xf2f2f2;
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_CITY;
    }

    @Override
    protected boolean build() {
        feeling = Feeling.CHASM;
        if (super.build()) {

            for (int i = 0; i < length(); i++) {
                if (map[i] == Terrain.SECRET_DOOR) {
                    map[i] = Terrain.DOOR;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> rooms = new ArrayList<>();

        rooms.add(roomEntrance = new EntranceRoom());
        rooms.add(new ImpShopRoom());
        rooms.add(roomExit = new ExitRoom());

        return rooms;
    }

    @Override
    protected Builder builder() {
        return new LineBuilder()
                .setPathVariance(0f)
                .setPathLength(1f, new float[]{1})
                .setTunnelLength(new float[]{0, 0, 1}, new float[]{1});
    }

    @Override
    protected Painter painter() {
        return new CityPainter()
                .setWater(0.10f, 4)
                .setGrass(0.10f, 3);
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
        Item item = Bones.get();
        if (item != null) {
            int pos;
            do {
                pos = pointToCell(roomEntrance.random());
            } while (pos == entrance);
            drop(item, pos).setHauntedIfCursed(1f).type = Heap.Type.REMAINS;
        }
    }

    @Override
    public int randomRespawnCell() {
        int cell;
        do {
            cell = pointToCell(roomEntrance.random());
        } while (!passable[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(CityLevel.class, "water_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CityLevel.class, "high_grass_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CityLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CityLevel.class, "exit_desc");
            case Terrain.WALL_DECO:
            case Terrain.EMPTY_DECO:
                return Messages.get(CityLevel.class, "deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(CityLevel.class, "sp_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(CityLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CityLevel.class, "bookshelf_desc");
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
}
