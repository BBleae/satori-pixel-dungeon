package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.LeafParticle;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Blooming extends Weapon.Enchantment {

    private static final ItemSprite.Glowing DARK_GREEN = new ItemSprite.Glowing(0x008800);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        int level = Math.max(0, weapon.level());

        if (Random.Int(level + 3) >= 2) {

            boolean secondPlant = level > Random.Int(10);
            if (plantGrass(defender.pos)) {
                if (secondPlant) secondPlant = false;
                else return damage;
            }

            ArrayList<Integer> positions = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                positions.add(i);
            }
            Random.shuffle(positions);
            for (int i : positions) {
                if (plantGrass(defender.pos + i)) {
                    if (secondPlant) secondPlant = false;
                    else return damage;
                }
            }

        }

        return damage;
    }

    private boolean plantGrass(int cell) {
        int c = Dungeon.level.map[cell];
        if (c == Terrain.EMPTY || c == Terrain.EMPTY_DECO
                || c == Terrain.EMBERS || c == Terrain.GRASS) {
            Level.set(cell, Terrain.HIGH_GRASS);
            GameScene.updateMap(cell);
            CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            return true;
        }
        return false;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return DARK_GREEN;
    }
}
