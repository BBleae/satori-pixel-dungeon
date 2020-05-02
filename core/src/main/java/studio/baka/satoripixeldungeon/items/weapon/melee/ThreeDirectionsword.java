package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.MagicImmune;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Projecting;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ThreeDirectionsword extends MeleeWeapon {

    {
        image = ItemSpriteSheet.THREEDIRWPN;

        tier = 5;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (enchantment != null) damage = enchantment.proc(this, attacker, defender, damage);
        int dmg;

        affected.clear();
        if (!(this.enchantment instanceof Projecting)) find3dir(attacker, defender);
        else findalldir(attacker, defender);

        affected.remove(defender); //or defender would hurt twice
        for (Char ch : affected) {
            dmg = damage;
            if (Char.hit(attacker, ch, false)) {
                if (this.enchantment != null && attacker.buff(MagicImmune.class) == null) {
                    dmg = enchantment.proc(this, attacker, ch, damage);
                }
                ch.damage(dmg, this);
            } else {
                ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
            }
        }
        return damage;
    }

    private final ArrayList<Char> affected = new ArrayList<>();

    private void find3dir(Char attacker, Char defender) {
        PathFinder.buildDistanceMap(attacker.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n != null && isnear8(attacker, n) && isnear4(defender, n))
                    affected.add(n);
            }
        }
    }

    private void findalldir(Char attacker, Char defender) {
        PathFinder.buildDistanceMap(attacker.pos, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n != null && isnear8(attacker, n))
                    affected.add(n);
            }
        }
    }

    private boolean isnear8(Char from, Char target) {
        for (int the_dir_pos : PathFinder.NEIGHBOURS8) {
            if (target.pos == from.pos + the_dir_pos)
                return true;
        }
        return false;
    }

    private boolean isnear4(Char from, Char target) {
        for (int the_dir_pos : PathFinder.NEIGHBOURS4) {
            if (target.pos == from.pos + the_dir_pos)
                return true;
        }
        return false;
    }
}
