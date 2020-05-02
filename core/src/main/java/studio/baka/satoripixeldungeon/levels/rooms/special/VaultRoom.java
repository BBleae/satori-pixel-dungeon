package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.keys.CrystalKey;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class VaultRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);
        Painter.fill(level, this, 2, Terrain.EMPTY);

        int cx = (left + right) / 2;
        int cy = (top + bottom) / 2;
        int c = cx + cy * level.width();

        Random.shuffle(prizeClasses);

        Item i1, i2;
        i1 = prize(level);
        i2 = prize(level);
        level.drop(i1, c).type = Heap.Type.CRYSTAL_CHEST;
        level.drop(i2, c + PathFinder.NEIGHBOURS8[Random.Int(8)]).type = Heap.Type.CRYSTAL_CHEST;
        level.addItemToSpawn(new CrystalKey(Dungeon.depth));

        entrance().set(Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private Item prize(Level level) {
        Generator.Category cat = prizeClasses.remove(0);
        Item prize;
        do {
            prize = Generator.random(cat);
        } while (prize == null || Challenges.isItemBlocked(prize));
        return prize;
    }

    private final ArrayList<Generator.Category> prizeClasses = new ArrayList<>(
            Arrays.asList(Generator.Category.WAND,
                    Generator.Category.RING,
                    Generator.Category.ARTIFACT));
}
