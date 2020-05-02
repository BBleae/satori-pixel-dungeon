package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.actors.blobs.Foliage;
import studio.baka.satoripixeldungeon.items.wands.WandOfRegrowth;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Patch;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import studio.baka.satoripixeldungeon.plants.Starflower;
import com.watabou.utils.Random;

public class SecretGardenRoom extends SecretRoom {

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.GRASS);

        boolean[] grass = Patch.generate(width() - 2, height() - 2, 0.5f, 0, true);
        for (int i = top + 1; i < bottom; i++) {
            for (int j = left + 1; j < right; j++) {
                if (grass[xyToPatchCoords(j, i)]) {
                    level.map[i * level.width() + j] = Terrain.HIGH_GRASS;
                }
            }
        }

        entrance().set(Door.Type.HIDDEN);

        level.plant(new Starflower.Seed(), plantPos(level));
        level.plant(new WandOfRegrowth.Seedpod.Seed(), plantPos(level));
        level.plant(new WandOfRegrowth.Dewcatcher.Seed(), plantPos(level));

        if (Random.Int(2) == 0) {
            level.plant(new WandOfRegrowth.Seedpod.Seed(), plantPos(level));
        } else {
            level.plant(new WandOfRegrowth.Dewcatcher.Seed(), plantPos(level));
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

    protected int xyToPatchCoords(int x, int y) {
        return (x - left - 1) + ((y - top - 1) * (width() - 2));
    }
}
