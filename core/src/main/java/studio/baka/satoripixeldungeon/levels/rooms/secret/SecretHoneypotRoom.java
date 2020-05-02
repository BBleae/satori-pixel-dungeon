package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.Bee;
import studio.baka.satoripixeldungeon.items.Honeypot;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class SecretHoneypotRoom extends SecretRoom {

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        Point brokenPotPos = center();

        brokenPotPos.x = (brokenPotPos.x + entrance().x) / 2;
        brokenPotPos.y = (brokenPotPos.y + entrance().y) / 2;

        Honeypot.ShatteredPot pot = new Honeypot.ShatteredPot();
        level.drop(pot, level.pointToCell(brokenPotPos));

        Bee bee = new Bee();
        bee.spawn(Dungeon.depth);
        bee.HP = bee.HT;
        bee.pos = level.pointToCell(brokenPotPos);
        level.mobs.add(bee);

        bee.setPotInfo(level.pointToCell(brokenPotPos), null);

        placeItem(new Honeypot(), level);

        placeItem(new Bomb().random(), level);

        entrance().set(Door.Type.HIDDEN);
    }

    private void placeItem(Item item, Level level) {
        int itemPos;
        do {
            itemPos = level.pointToCell(random());
        } while (level.heaps.get(itemPos) != null);

        level.drop(item, itemPos);
    }
}
