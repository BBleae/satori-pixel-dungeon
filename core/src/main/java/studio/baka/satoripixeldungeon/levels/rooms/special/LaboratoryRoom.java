package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Alchemy;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.journal.AlchemyPage;
import studio.baka.satoripixeldungeon.items.keys.IronKey;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.journal.Document;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.RegularLevel;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class LaboratoryRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);

        Door entrance = entrance();

        Point pot = null;
        if (entrance.x == left) {
            pot = new Point(right - 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
        } else if (entrance.x == right) {
            pot = new Point(left + 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
        } else if (entrance.y == top) {
            pot = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
        } else if (entrance.y == bottom) {
            pot = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 1);
        }
        Painter.set(level, Objects.requireNonNull(pot), Terrain.ALCHEMY);

        int chapter = 1 + Dungeon.depth / 5;
        Blob.seed(pot.x + level.width() * pot.y, 1 + chapter * 10 + Random.NormalIntRange(0, 10), Alchemy.class, level);

        int n = Random.NormalIntRange(1, 3);
        for (int i = 0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (
                    level.map[pos] != Terrain.EMPTY_SP ||
                            level.heaps.get(pos) != null);
            level.drop(prize(level), pos);
        }

        //guide pages
        Collection<String> allPages = Document.ALCHEMY_GUIDE.pages();
        ArrayList<String> missingPages = new ArrayList<>();
        for (String page : allPages) {
            if (!Document.ALCHEMY_GUIDE.hasPage(page)) {
                missingPages.add(page);
            }
        }

        //4 pages in sewers, 6 in prison, 9 in caves+
        int chapterTarget;
        if (missingPages.size() <= 3) {
            chapterTarget = 3;
        } else if (missingPages.size() <= 5) {
            chapterTarget = 2;
        } else {
            chapterTarget = 1;
        }

        if (!missingPages.isEmpty() && chapter >= chapterTarget) {

            //for each chapter ahead of the target chapter, drop 1 additional page
            int pagesToDrop = Math.min(missingPages.size(), (chapter - chapterTarget) + 1);

            for (int i = 0; i < pagesToDrop; i++) {
                AlchemyPage p = new AlchemyPage();
                p.page(missingPages.remove(0));
                int pos;
                do {
                    pos = level.pointToCell(random());
                } while (
                        level.map[pos] != Terrain.EMPTY_SP ||
                                level.heaps.get(pos) != null);
                level.drop(p, pos);
            }
        }

        if (level instanceof RegularLevel && ((RegularLevel) level).hasPitRoom()) {
            entrance.set(Door.Type.REGULAR);
        } else {
            entrance.set(Door.Type.LOCKED);
            level.addItemToSpawn(new IronKey(Dungeon.depth));
        }

    }

    private static Item prize(Level level) {

        Item prize = level.findPrizeItem(Potion.class);
        if (prize == null)
            prize = Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.STONE));

        return prize;
    }
}
