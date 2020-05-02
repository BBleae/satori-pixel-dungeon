package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

public class RunestoneRoom extends SpecialRoom {

    @Override
    public int minWidth() {
        return 6;
    }

    @Override
    public int minHeight() {
        return 6;
    }

    @Override
    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.CHASM);

        Painter.drawInside(level, this, entrance(), 2, Terrain.EMPTY_SP);
        Painter.fill(level, this, 2, Terrain.EMPTY);

        int n = Random.NormalIntRange(2, 3);
        int dropPos;
        for (int i = 0; i < n; i++) {
            do {
                dropPos = level.pointToCell(random());
            } while (level.map[dropPos] != Terrain.EMPTY);
            level.drop(prize(level), dropPos);
        }

        entrance().set(Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {

        Item prize = level.findPrizeItem(Runestone.class);
        if (prize == null)
            prize = Generator.random(Generator.Category.STONE);

        return prize;
    }
}
