package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class SpecialRoom extends Room {

    @Override
    public int minWidth() {
        return 5;
    }

    public int maxWidth() {
        return 10;
    }

    @Override
    public int minHeight() {
        return 5;
    }

    public int maxHeight() {
        return 10;
    }

    @Override
    public int maxConnections(int direction) {
        return 1;
    }

    private Door entrance;

    public Door entrance() {
        if (entrance == null) {
            if (connected.isEmpty()) {
                return null;
            } else {
                entrance = connected.values().iterator().next();
            }
        }
        return entrance;
    }

    private static final String ENTRANCE = "entrance";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (entrance() != null) {
            bundle.put(ENTRANCE, entrance());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(ENTRANCE)) {
            entrance = (Door) bundle.get(ENTRANCE);
        }
    }

    private static final ArrayList<Class<? extends SpecialRoom>> ALL_SPEC = new ArrayList<>(Arrays.asList(
            WeakFloorRoom.class, MagicWellRoom.class, CryptRoom.class, PoolRoom.class, GardenRoom.class, LibraryRoom.class, ArmoryRoom.class,
            TreasuryRoom.class, TrapsRoom.class, StorageRoom.class, StatueRoom.class, VaultRoom.class, RunestoneRoom.class
    ));

    public static ArrayList<Class<? extends Room>> runSpecials = new ArrayList<>();
    public static ArrayList<Class<? extends Room>> floorSpecials = new ArrayList<>();

    private static int pitNeededDepth = -1;

    public static void initForRun() {
        runSpecials = (ArrayList<Class<? extends Room>>) ALL_SPEC.clone();

        pitNeededDepth = -1;
        Random.shuffle(runSpecials);
    }

    public static void initForFloor() {
        floorSpecials = (ArrayList<Class<? extends Room>>) runSpecials.clone();

        //laboratory rooms spawn at set intervals every chapter
        if (Dungeon.depth % 5 == (Dungeon.seed % 3 + 2)) {
            floorSpecials.add(0, LaboratoryRoom.class);
        }
    }

    private static void useType(Class<? extends Room> type) {
        floorSpecials.remove(type);
        if (runSpecials.remove(type)) {
            runSpecials.add(type);
        }
    }

    public static void resetPitRoom(int depth) {
        if (pitNeededDepth == depth) pitNeededDepth = -1;
    }

    public static SpecialRoom createRoom() {
        if (Dungeon.depth == pitNeededDepth) {
            pitNeededDepth = -1;

            floorSpecials.remove(ArmoryRoom.class);
            floorSpecials.remove(CryptRoom.class);
            floorSpecials.remove(LibraryRoom.class);
            floorSpecials.remove(RunestoneRoom.class);
            floorSpecials.remove(StatueRoom.class);
            floorSpecials.remove(TreasuryRoom.class);
            floorSpecials.remove(VaultRoom.class);
            floorSpecials.remove(WeakFloorRoom.class);

            return new PitRoom();

        } else if (floorSpecials.contains(LaboratoryRoom.class)) {

            useType(LaboratoryRoom.class);
            return new LaboratoryRoom();

        } else {

            if (Dungeon.bossLevel(Dungeon.depth + 1)) {
                floorSpecials.remove(WeakFloorRoom.class);
            }

            Room r;
            int index = floorSpecials.size();
            for (int i = 0; i < 4; i++) {
                int newidx = Random.Int(floorSpecials.size());
                if (newidx < index) index = newidx;
            }

            r = Reflection.newInstance(floorSpecials.get(index));

            if (r instanceof WeakFloorRoom) {
                pitNeededDepth = Dungeon.depth + 1;
            }

            useType(Objects.requireNonNull(r).getClass());
            return (SpecialRoom) r;

        }
    }

    private static final String ROOMS = "special_rooms";
    private static final String PIT = "pit_needed";

    public static void restoreRoomsFromBundle(Bundle bundle) {
        runSpecials.clear();
        if (bundle.contains(ROOMS)) {
            for (Class<? extends Room> type : bundle.getClassArray(ROOMS)) {
                //pre-0.7.0 saves
                if (type != null && type != LaboratoryRoom.class) {
                    runSpecials.add(type);
                }
            }
        } else {
            initForRun();
            SatoriPixelDungeon.reportException(new Exception("specials array didn't exist!"));
        }
        pitNeededDepth = bundle.getInt(PIT);
    }

    public static void storeRoomsInBundle(Bundle bundle) {
        bundle.put(ROOMS, runSpecials.toArray(new Class[0]));
        bundle.put(PIT, pitNeededDepth);
    }
}
