package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Vampiric extends Weapon.Enchantment {

    private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0x660022);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        //chance to heal scales from 5%-30% based on missing HP
        float missingPercent = (attacker.HT - attacker.HP) / (float) attacker.HT;
        float healChance = 0.05f + .25f * missingPercent;

        if (Random.Float() < healChance) {

            //heals for 50% of damage dealt
            int healAmt = Math.round(damage * 0.5f);
            healAmt = Math.min(healAmt, attacker.HT - attacker.HP);

            if (healAmt > 0 && attacker.isAlive()) {

                attacker.HP += healAmt;
                attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
                attacker.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmt));

            }
        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return RED;
    }
}
