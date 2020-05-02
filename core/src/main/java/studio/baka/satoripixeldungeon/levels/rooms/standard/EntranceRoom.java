package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.journal.GuidePage;
import studio.baka.satoripixeldungeon.journal.Document;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class EntranceRoom extends StandardRoom {

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 5);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 5);
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        do {
            level.entrance = level.pointToCell(random(2));
        } while (level.findMob(level.entrance) != null);
        Painter.set(level, level.entrance, Terrain.ENTRANCE);

        if (Dungeon.depth == 1 && !Document.ADVENTURERS_GUIDE.hasPage(Document.GUIDE_INTRO_PAGE)) {
            int pos;
            do {
                //can't be on bottom row of tiles
                pos = level.pointToCell(new Point(Random.IntRange(left + 1, right - 1),
                        Random.IntRange(top + 1, bottom - 2)));
            } while (pos == level.entrance || level.findMob(level.entrance) != null);
            GuidePage p = new GuidePage();
            p.page(Document.GUIDE_INTRO_PAGE);
            level.drop(p, pos);
        }

        if (Dungeon.depth == 2) {
            if (!Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_1)) {
                for (Room.Door door : connected.values()) {
                    door.set(Door.Type.HIDDEN);
                }
            }

            if (!Document.ADVENTURERS_GUIDE.hasPage(Document.GUIDE_SEARCH_PAGE)) {
                int pos;
                do {
                    //can't be on bottom row of tiles
                    pos = level.pointToCell(new Point(Random.IntRange(left + 1, right - 1),
                            Random.IntRange(top + 1, bottom - 2)));
                } while (pos == level.entrance || level.findMob(level.entrance) != null);
                GuidePage p = new GuidePage();
                p.page(Document.GUIDE_SEARCH_PAGE);
                level.drop(p, pos);
            }

        }

    }

}
