package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Grim extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        int level = Math.max(0, weapon.level());

        int enemyHealth = defender.HP - damage;
        if (enemyHealth <= 0) return damage; //no point in proccing if they're already dead.

        //scales from 0 - 50% based on how low hp the enemy is, plus 5% per level
        float maxChance = 0.5f + .05f * level;
        float chanceMulti = (float) Math.pow(((defender.HT - enemyHealth) / (float) defender.HT), 2);
        float chance = maxChance * chanceMulti;

        if (Random.Float() < chance) {

            defender.damage(defender.HP, this);
            defender.sprite.emitter().burst(ShadowParticle.UP, 5);

            if (!defender.isAlive() && attacker instanceof Hero
                    //this prevents unstable from triggering grim achievement
                    && weapon.hasEnchant(Grim.class, attacker)) {
                Badges.validateGrimWeapon();
            }

        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return BLACK;
    }

}
