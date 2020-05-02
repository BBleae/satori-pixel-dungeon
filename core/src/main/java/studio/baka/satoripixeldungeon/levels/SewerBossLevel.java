package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Bones;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.levels.builders.Builder;
import studio.baka.satoripixeldungeon.levels.builders.FigureEightBuilder;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.painters.SewerPainter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.secret.RatKingRoom;
import studio.baka.satoripixeldungeon.levels.rooms.sewerboss.GooBossRoom;
import studio.baka.satoripixeldungeon.levels.rooms.sewerboss.SewerBossEntranceRoom;
import studio.baka.satoripixeldungeon.levels.rooms.sewerboss.SewerBossExitRoom;
import studio.baka.satoripixeldungeon.levels.rooms.standard.StandardRoom;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SewerBossLevel extends SewerLevel {

    {
        color1 = 0x48763c;
        color2 = 0x59994a;
    }

    private int stairs = 0;

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();

        initRooms.add(roomEntrance = new SewerBossEntranceRoom());
        initRooms.add(roomExit = new SewerBossExitRoom());

        int standards = standardRooms();
        for (int i = 0; i < standards; i++) {
            StandardRoom s = StandardRoom.createRoom();
            //force to normal size
            s.setSizeCat(0, 0);
            initRooms.add(s);
        }

        GooBossRoom gooRoom = GooBossRoom.randomGooRoom();
        initRooms.add(gooRoom);
        ((FigureEightBuilder) builder).setLandmarkRoom(gooRoom);
        initRooms.add(new RatKingRoom());
        return initRooms;
    }

    @Override
    protected int standardRooms() {
        //2 to 3, average 2.5
        return 2 + Random.chances(new float[]{1, 1});
    }

    protected Builder builder() {
        return new FigureEightBuilder()
                .setLoopShape(2, Random.Float(0.4f, 0.7f), Random.Float(0f, 0.5f))
                .setPathLength(1f, new float[]{1})
                .setTunnelLength(new float[]{1, 2}, new float[]{1});
    }

    @Override
    protected Painter painter() {
        return new SewerPainter()
                .setWater(0.50f, 5)
                .setGrass(0.20f, 4)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    protected int nTraps() {
        return 0;
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
            } while (pos == entrance || solid[pos]);
            drop(item, pos).setHauntedIfCursed(1f).type = Heap.Type.REMAINS;
        }
    }

    @Override
    public int randomRespawnCell() {
		/*
		int pos;
		do {
			pos = pointToCell(roomEntrance.random());
		} while (pos == entrance || !passable[pos] || Actor.findChar(pos) != null);
		return pos;
		 */
        int cell;
        if (bossisalive()) {
            do {
                cell = Random.Int(0, (width() - 1) * (height() - 1));
            } while (!passable[cell] || Actor.findChar(cell) != null || !heroFOV[cell]);
            return cell;
        } else {
            do {
                cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!passable[cell] || Actor.findChar(cell) != null);
            return cell;
        }
    }


    public void seal() {
        if (entrance != 0) {

            super.seal();

            set(entrance, Terrain.WATER);
            GameScene.updateMap(entrance);
            GameScene.ripple(entrance);

            stairs = entrance;
            entrance = 0;
        }
    }

    public void unseal() {
        if (stairs != 0) {

            super.unseal();

            entrance = stairs;
            stairs = 0;

            set(entrance, Terrain.ENTRANCE);
            GameScene.updateMap(entrance);

        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        if (map[exit - 1] != Terrain.WALL_DECO) visuals.add(new PrisonLevel.Torch(exit - 1));
        if (map[exit + 1] != Terrain.WALL_DECO) visuals.add(new PrisonLevel.Torch(exit + 1));
        return visuals;
    }

    private static final String STAIRS = "stairs";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STAIRS, stairs);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stairs = bundle.getInt(STAIRS);
        roomExit = roomEntrance;
    }
}
