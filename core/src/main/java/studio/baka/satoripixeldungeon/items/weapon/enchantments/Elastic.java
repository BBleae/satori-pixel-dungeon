package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.wands.WandOfBlastWave;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Elastic extends Weapon.Enchantment {

    private static final ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xFF00FF);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        // lvl 0 - 20%
        // lvl 1 - 33%
        // lvl 2 - 43%
        int level = Math.max(0, weapon.level());

        if (Random.Int(level + 5) >= 4) {
            //trace a ballistica to our target (which will also extend past them
            Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
            //trim it to just be the part that goes past them
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
            //knock them back along that ballistica
            WandOfBlastWave.throwChar(defender, trajectory, 2);
        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PINK;
    }

}
