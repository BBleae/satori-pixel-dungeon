package studio.baka.satoripixeldungeon.levels.rooms.standard;

import studio.baka.satoripixeldungeon.actors.mobs.npcs.Blacksmith;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.levels.traps.BurningTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class BlacksmithRoom extends StandardRoom {

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 6);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 6);
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.TRAP);
        Painter.fill(level, this, 2, Terrain.EMPTY_SP);

        for (int i = 0; i < 2; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY_SP);
            level.drop(
                    Generator.random(Random.oneOf(
                            Generator.Category.ARMOR,
                            Generator.Category.WEAPON,
                            Generator.Category.MISSILE
                    )), pos);
        }

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 1, Terrain.EMPTY);
        }

        Blacksmith npc = new Blacksmith();
        do {
            npc.pos = level.pointToCell(random(2));
        } while (level.heaps.get(npc.pos) != null);
        level.mobs.add(npc);

        for (Point p : getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] == Terrain.TRAP) {
                level.setTrap(new BurningTrap().reveal(), cell);
            }
        }
    }
}
