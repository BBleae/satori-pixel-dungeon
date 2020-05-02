package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Piranha;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.PotionOfInvisibility;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

public class PoolRoom extends SpecialRoom {

    private static final int NPIRANHAS = 3;

    @Override
    public int minWidth() {
        return 6;
    }

    @Override
    public int minHeight() {
        return 6;
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.WATER);

        Door door = entrance();
        door.set(Door.Type.REGULAR);

        int x = -1;
        int y = -1;
        if (door.x == left) {

            x = right - 1;
            y = top + height() / 2;
            Painter.fill(level, left + 1, top + 1, 1, height() - 2, Terrain.EMPTY_SP);

        } else if (door.x == right) {

            x = left + 1;
            y = top + height() / 2;
            Painter.fill(level, right - 1, top + 1, 1, height() - 2, Terrain.EMPTY_SP);

        } else if (door.y == top) {

            x = left + width() / 2;
            y = bottom - 1;
            Painter.fill(level, left + 1, top + 1, width() - 2, 1, Terrain.EMPTY_SP);

        } else if (door.y == bottom) {

            x = left + width() / 2;
            y = top + 1;
            Painter.fill(level, left + 1, bottom - 1, width() - 2, 1, Terrain.EMPTY_SP);

        }

        int pos = x + y * level.width();
        level.drop(prize(level), pos).type =
                Random.Int(3) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;
        Painter.set(level, pos, Terrain.PEDESTAL);

        level.addItemToSpawn(new PotionOfInvisibility());

        for (int i = 0; i < NPIRANHAS; i++) {
            Piranha piranha = new Piranha();
            do {
                piranha.pos = level.pointToCell(random());
            } while (level.map[piranha.pos] != Terrain.WATER || level.findMob(piranha.pos) != null);
            level.mobs.add(piranha);
        }
    }

    private static Item prize(Level level) {

        Item prize;

        if (Random.Int(3) == 0) {
            prize = level.findPrizeItem();
            if (prize != null)
                return prize;
        }

        //1 floor set higher in probability, never cursed
        do {
            if (Random.Int(2) == 0) {
                prize = Generator.randomWeapon((Dungeon.depth / 5) + 1);
            } else {
                prize = Generator.randomArmor((Dungeon.depth / 5) + 1);
            }
        } while (prize.cursed || Challenges.isItemBlocked(prize));
        prize.cursedKnown = true;

        //33% chance for an extra update.
        if (Random.Int(3) == 0) {
            prize.upgrade();
        }

        return prize;
    }
}
