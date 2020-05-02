package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

abstract public class KindOfWeapon extends EquipableItem {

    protected static final float TIME_TO_EQUIP = 1f;

    @Override
    public boolean isEquipped(Hero hero) {
        return hero.belongings.weapon == this;
    }

    @Override
    public boolean doEquip(Hero hero) {

        detachAll(hero.belongings.backpack);

        if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip(hero, true)) {

            hero.belongings.weapon = this;
            activate(hero);

            updateQuickslot();

            cursedKnown = true;
            if (cursed) {
                equipCursed(hero);
                GLog.n(Messages.get(KindOfWeapon.class, "equip_cursed"));
            }

            hero.spendAndNext(TIME_TO_EQUIP);
            return true;

        } else {

            collect(hero.belongings.backpack);
            return false;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {

            hero.belongings.weapon = null;
            return true;

        } else {

            return false;

        }
    }

    public int min() {
        return min(level());
    }

    public int max() {
        return max(level());
    }

    abstract public int min(int lvl);

    abstract public int max(int lvl);

    public int damageRoll(Char owner) {
        return Random.NormalIntRange(min(), max());
    }

    public float accuracyFactor(Char owner) {
        return 1f;
    }

    public float speedFactor(Char owner) {
        return 1f;
    }

    public int reachFactor(Char owner) {
        return 1;
    }

    public boolean canReach(Char owner, int target) {
        if (Dungeon.level.distance(owner.pos, target) > reachFactor(owner)) {
            return false;
        } else {
            boolean[] passable = BArray.not(Dungeon.level.solid, null);
            for (Char ch : Actor.chars()) {
                if (ch != owner) passable[ch.pos] = false;
            }

            PathFinder.buildDistanceMap(target, passable, reachFactor(owner));

            return PathFinder.distance[owner.pos] <= reachFactor(owner);
        }
    }

    public int defenseFactor(Char owner) {
        return 0;
    }

    public int proc(Char attacker, Char defender, int damage) {
        return damage;
    }

}
