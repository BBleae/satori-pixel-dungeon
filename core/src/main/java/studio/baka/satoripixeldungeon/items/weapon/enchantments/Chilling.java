package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Chill;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Chilling extends Weapon.Enchantment {

    private static final ItemSprite.Glowing TEAL = new ItemSprite.Glowing(0x00FFFF);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        int level = Math.max(0, weapon.level());

        if (Random.Int(level + 3) >= 2) {

            //adds 3 turns of chill per proc, with a cap of 6 turns
            float durationToAdd = 3f;
            Chill existing = defender.buff(Chill.class);
            if (existing != null) {
                durationToAdd = Math.min(durationToAdd, 6f - existing.cooldown());
            }

            Buff.affect(defender, Chill.class, durationToAdd);
            Splash.at(defender.sprite.center(), 0xFFB2D6FF, 5);

        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return TEAL;
    }

}
