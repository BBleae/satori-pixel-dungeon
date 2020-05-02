package studio.baka.satoripixeldungeon.levels.rooms.special;

import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Foliage;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.plants.BlandfruitBush;
import studio.baka.satoripixeldungeon.plants.Sungrass;
import com.watabou.utils.Random;

public class GardenRoom extends SpecialRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.HIGH_GRASS);
        Painter.fill(level, this, 2, Terrain.GRASS);

        entrance().set(Door.Type.REGULAR);

        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            if (Random.Int(2) == 0) {
                level.plant(new Sungrass.Seed(), plantPos(level));
            }
        } else {
            int bushes = Random.Int(3);
            if (bushes == 0) {
                level.plant(new Sungrass.Seed(), plantPos(level));
            } else if (bushes == 1) {
                level.plant(new BlandfruitBush.Seed(), plantPos(level));
            } else if (Random.Int(5) == 0) {
                level.plant(new Sungrass.Seed(), plantPos(level));
                level.plant(new BlandfruitBush.Seed(), plantPos(level));
            }
        }

        Foliage light = (Foliage) level.blobs.get(Foliage.class);
        if (light == null) {
            light = new Foliage();
        }
        for (int i = top + 1; i < bottom; i++) {
            for (int j = left + 1; j < right; j++) {
                light.seed(level, j + level.width() * i, 1);
            }
        }
        level.blobs.put(Foliage.class, light);
    }

    private int plantPos(Level level) {
        int pos;
        do {
            pos = level.pointToCell(random());
        } while (level.plants.get(pos) != null);
        return pos;
    }
}
