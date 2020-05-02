package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.actors.mobs.npcs.RatKing;
import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.sewerboss.SewerBossEntranceRoom;
import com.watabou.utils.Random;

public class RatKingRoom extends SecretRoom {

    @Override
    public boolean canConnect(Room r) {
        //never connects at the entrance
        return !(r instanceof SewerBossEntranceRoom) && super.canConnect(r);
    }

    //reduced max size to limit chest numbers.
    // normally would gen with 8-28, this limits it to 8-16
    @Override
    public int maxHeight() {
        return 7;
    }

    public int maxWidth() {
        return 7;
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);

        Door entrance = entrance();
        entrance.set(Door.Type.HIDDEN);
        int door = entrance.x + entrance.y * level.width();

        for (int i = left + 1; i < right; i++) {
            addChest(level, (top + 1) * level.width() + i, door);
            addChest(level, (bottom - 1) * level.width() + i, door);
        }

        for (int i = top + 2; i < bottom - 1; i++) {
            addChest(level, i * level.width() + left + 1, door);
            addChest(level, i * level.width() + right - 1, door);
        }

        RatKing king = new RatKing();
        king.pos = level.pointToCell(random(2));
        level.mobs.add(king);
    }

    private static void addChest(Level level, int pos, int door) {

        if (pos == door - 1 ||
                pos == door + 1 ||
                pos == door - level.width() ||
                pos == door + level.width()) {
            return;
        }

        Item prize = new Gold(Random.IntRange(10, 25));

        level.drop(prize, pos).type = Heap.Type.CHEST;
    }
}
