package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundle;

public class Fragile extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
    private int hits = 0;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        //degrades from 100% to 25% damage over 150 hits
        damage *= (1f - hits * 0.005f);
        if (hits < 150) hits++;
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

    private static final String HITS = "hits";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        hits = bundle.getInt(HITS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(HITS, hits);
    }

}
