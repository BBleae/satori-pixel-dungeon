package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Corrosion;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;


public class RotDart extends TippedDart {

    {
        image = ItemSpriteSheet.ROT_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        if (defender.properties().contains(Char.Property.BOSS)
                || defender.properties().contains(Char.Property.MINIBOSS)) {
            Buff.affect(defender, Corrosion.class).set(5f, Dungeon.depth / 3);
        } else {
            Buff.affect(defender, Corrosion.class).set(10f, Dungeon.depth);
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    protected float durabilityPerUse() {
        return 100f;
    }
}
