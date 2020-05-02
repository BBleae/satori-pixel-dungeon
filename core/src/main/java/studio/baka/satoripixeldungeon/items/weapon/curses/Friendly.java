package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Charm;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Friendly extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        if (Random.Int(10) == 0) {

            int base = Random.IntRange(3, 5);

            Buff.affect(attacker, Charm.class, base + 10).object = defender.id();
            attacker.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);

            //5 turns will be reduced by the attack, so effectively lasts for base turns
            Buff.affect(defender, Charm.class, base + 5).object = attacker.id();
            defender.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);

        }

        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

}
