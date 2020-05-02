package studio.baka.satoripixeldungeon.items.weapon.missiles;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Piranha;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class FishingSpear extends MissileWeapon {

    {
        image = ItemSpriteSheet.FISHING_SPEAR;

        tier = 2;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Piranha) {
            damage = Math.max(damage, defender.HP / 2);
        }
        return super.proc(attacker, defender, damage);
    }
}
