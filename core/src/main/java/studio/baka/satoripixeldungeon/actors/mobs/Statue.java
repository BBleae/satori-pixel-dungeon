package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.items.weapon.Weapon.Enchantment;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Statue extends Mob {

    {
        spriteClass = StatueSprite.class;

        EXP = 0;
        state = PASSIVE;

        properties.add(Property.INORGANIC);
    }

    protected Weapon weapon;

    public Statue() {
        super();

        do {
            weapon = (MeleeWeapon) Generator.random(Generator.Category.WEAPON);
        } while (weapon.cursed);

        //noinspection unchecked
        weapon.enchant(Enchantment.random());

        HP = HT = 15 + Dungeon.depth * 5;
        defenseSkill = 4 + Dungeon.depth;
    }

    private static final String WEAPON = "weapon";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WEAPON, weapon);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        weapon = (Weapon) bundle.get(WEAPON);
    }

    @Override
    protected boolean act() {
        if (Dungeon.level.heroFOV[pos]) {
            Notes.add(Notes.Landmark.STATUE);
        }
        return super.act();
    }

    @Override
    public int damageRoll() {
        return weapon.damageRoll(this);
    }

    @Override
    public int attackSkill(Char target) {
        return (int) ((9 + Dungeon.depth) * weapon.accuracyFactor(this));
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay() * weapon.speedFactor(this);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || weapon.canReach(this, enemy.pos);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, Dungeon.depth + weapon.defenseFactor(this));
    }

    @Override
    public void damage(int dmg, Object src) {

        if (state == PASSIVE) {
            state = HUNTING;
        }

        super.damage(dmg, src);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        return weapon.proc(this, enemy, damage);
    }

    @Override
    public void beckon(int cell) {
        // Do nothing
    }

    @Override
    public void die(Object cause) {
        weapon.identify();
        Dungeon.level.drop(weapon, pos).sprite.drop();
        super.die(cause);
    }

    @Override
    public void destroy() {
        Notes.remove(Notes.Landmark.STATUE);
        super.destroy();
    }

    @Override
    public boolean reset() {
        state = PASSIVE;
        return true;
    }

    @Override
    public String description() {
        return Messages.get(this, "desc", weapon.name());
    }

    {
        resistances.add(Grim.class);
    }

}
