package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;

public class SecretArtilleryRoom extends SecretRoom {

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);

        Painter.set(level, center(), Terrain.STATUE_SP);

        for (int i = 0; i < 3; i++) {
            int itemPos;
            do {
                itemPos = level.pointToCell(random());
            } while (level.map[itemPos] != Terrain.EMPTY_SP
                    || level.heaps.get(itemPos) != null);

            if (i == 0) {
                level.drop(new Bomb.DoubleBomb(), itemPos);
            } else {
                level.drop(Generator.randomMissile(), itemPos);
            }
        }

        entrance().set(Door.Type.HIDDEN);
    }
}
