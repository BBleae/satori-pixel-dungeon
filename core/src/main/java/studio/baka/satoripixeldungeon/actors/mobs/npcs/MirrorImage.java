package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.CorrosiveGas;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.MirrorSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class MirrorImage extends NPC {

    {
        spriteClass = MirrorSprite.class;

        HP = HT = 1;
        defenseSkill = 1;

        alignment = Alignment.ALLY;
        state = HUNTING;

        //before other mobs
        actPriority = MOB_PRIORITY + 1;
    }

    private Hero hero;
    private int heroID;
    public int armTier;

    @Override
    protected boolean act() {

        if (hero == null) {
            hero = (Hero) Actor.findById(heroID);
            if (hero == null) {
                die(null);
                sprite.killAndErase();
                return true;
            }
        }

        if (hero.tier() != armTier) {
            armTier = hero.tier();
            ((MirrorSprite) sprite).updateArmor(armTier);
        }

        return super.act();
    }

    private static final String HEROID = "hero_id";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEROID, heroID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        heroID = bundle.getInt(HEROID);
    }

    public void duplicate(Hero hero) {
        this.hero = hero;
        heroID = this.hero.id();
        Buff.affect(this, MirrorInvis.class, Short.MAX_VALUE);
    }

    @Override
    public int damageRoll() {
        int damage;
        if (hero.belongings.weapon != null) {
            damage = hero.belongings.weapon.damageRoll(this);
        } else {
            damage = hero.damageRoll(); //handles ring of force
        }
        return (damage + 1) / 2; //half hero damage, rounded up
    }

    @Override
    public int attackSkill(Char target) {
        return hero.attackSkill(target);
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (hero != null) {
            int baseEvasion = 4 + hero.lvl;
            int heroEvasion = hero.defenseSkill(enemy);

            //if the hero has more/less evasion, 50% of it is applied
            return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
        } else {
            return 0;
        }
    }

    @Override
    protected float attackDelay() {
        return hero.attackDelay(); //handles ring of furor
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || (hero.belongings.weapon != null && hero.belongings.weapon.canReach(this, enemy.pos));
    }

    @Override
    public int drRoll() {
        if (hero != null && hero.belongings.weapon != null) {
            return Random.NormalIntRange(0, hero.belongings.weapon.defenseFactor(this) / 2);
        } else {
            return 0;
        }
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        MirrorInvis buff = buff(MirrorInvis.class);
        if (buff != null) {
            buff.detach();
        }

        if (enemy instanceof Mob) {
            ((Mob) enemy).aggro(this);
        }
        if (hero.belongings.weapon != null) {
            return hero.belongings.weapon.proc(this, enemy, damage);
        } else {
            return damage;
        }
    }

    @Override
    public CharSprite sprite() {
        CharSprite s = super.sprite();

        //pre-0.7.0 saves
        if (heroID == 0) {
            heroID = Dungeon.hero.id();
        }

        hero = (Hero) Actor.findById(heroID);
        if (hero != null) {
            armTier = hero.tier();
        }
        ((MirrorSprite) s).updateArmor(armTier);
        return s;
    }

    {
        immunities.add(ToxicGas.class);
        immunities.add(CorrosiveGas.class);
        immunities.add(Burning.class);
    }

    public static class MirrorInvis extends Invisibility {

        {
            announced = false;
        }

        @Override
        public int icon() {
            return BuffIndicator.NONE;
        }
    }
}