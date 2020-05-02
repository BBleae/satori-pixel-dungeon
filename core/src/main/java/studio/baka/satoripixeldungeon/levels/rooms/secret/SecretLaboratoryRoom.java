package studio.baka.satoripixeldungeon.levels.rooms.secret;

import studio.baka.satoripixeldungeon.actors.blobs.Alchemy;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.items.potions.*;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashMap;

public class SecretLaboratoryRoom extends SecretRoom {

    private static final HashMap<Class<? extends Potion>, Float> potionChances = new HashMap<>();

    static {
        potionChances.put(PotionOfHealing.class, 1f);
        potionChances.put(PotionOfMindVision.class, 2f);
        potionChances.put(PotionOfFrost.class, 3f);
        potionChances.put(PotionOfLiquidFlame.class, 3f);
        potionChances.put(PotionOfToxicGas.class, 3f);
        potionChances.put(PotionOfHaste.class, 4f);
        potionChances.put(PotionOfInvisibility.class, 4f);
        potionChances.put(PotionOfLevitation.class, 4f);
        potionChances.put(PotionOfParalyticGas.class, 4f);
        potionChances.put(PotionOfPurity.class, 4f);
        potionChances.put(PotionOfExperience.class, 6f);
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY_SP);

        entrance().set(Door.Type.HIDDEN);

        Point pot = center();
        Painter.set(level, pot, Terrain.ALCHEMY);

        Blob.seed(pot.x + level.width() * pot.y, 1 + Random.NormalIntRange(20, 30), Alchemy.class, level);

        int n = Random.IntRange(2, 3);
        HashMap<Class<? extends Potion>, Float> chances = new HashMap<>(potionChances);
        for (int i = 0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get(pos) != null);

            Class<? extends Potion> potionCls = Random.chances(chances);
            chances.put(potionCls, 0f);
            level.drop(Reflection.newInstance(potionCls), pos);
        }

    }

}
