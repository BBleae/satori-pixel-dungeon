package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Blazing extends Weapon.Enchantment {

    private static final ItemSprite.Glowing ORANGE = new ItemSprite.Glowing(0xFF4400);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        int level = Math.max(0, weapon.level());

        if (Random.Int(level + 3) >= 2) {

            if (defender.buff(Burning.class) != null) {
                Buff.affect(defender, Burning.class).reignite(defender, 8f);
                int burnDamage = Random.NormalIntRange(1, 3 + Dungeon.depth / 4);
                defender.damage(Math.round(burnDamage * 0.67f), this);
            } else {
                Buff.affect(defender, Burning.class).reignite(defender, 8f);
            }

            defender.sprite.emitter().burst(FlameParticle.FACTORY, level + 1);

        }

        return damage;

    }

    @Override
    public Glowing glowing() {
        return ORANGE;
    }
}
