package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Amok;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Imp;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.KindOfWeapon;
import studio.baka.satoripixeldungeon.items.food.Food;
import studio.baka.satoripixeldungeon.items.weapon.melee.Gauntlet;
import studio.baka.satoripixeldungeon.items.weapon.melee.Gloves;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.MonkSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Monk extends Mob {

    {
        spriteClass = MonkSprite.class;

        HP = HT = 70;
        defenseSkill = 30;

        EXP = 11;
        maxLvl = 21;

        loot = new Food();
        lootChance = 0.083f;

        properties.add(Property.UNDEAD);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(12, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay() * 0.5f;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public void rollToDropLoot() {
        Imp.Quest.process(this);

        super.rollToDropLoot();
    }

    private int hitsToDisarm = 0;

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        if (enemy == Dungeon.hero) {

            Hero hero = Dungeon.hero;
            KindOfWeapon weapon = hero.belongings.weapon;

            if (weapon != null
                    && !(weapon instanceof Gloves)
                    && !(weapon instanceof Gauntlet)
                    && !weapon.cursed) {
                if (hitsToDisarm == 0) hitsToDisarm = Random.NormalIntRange(4, 8);

                if (--hitsToDisarm == 0) {
                    hero.belongings.weapon = null;
                    Dungeon.quickslot.convertToPlaceholder(weapon);
                    Item.updateQuickslot();
                    Dungeon.level.drop(weapon, hero.pos).sprite.drop();
                    GLog.w(Messages.get(this, "disarm", weapon.name()));
                }
            }
        }

        return damage;
    }

    {
        immunities.add(Amok.class);
        immunities.add(Terror.class);
    }

    private static final String DISARMHITS = "hitsToDisarm";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DISARMHITS, hitsToDisarm);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        hitsToDisarm = bundle.getInt(DISARMHITS);
    }
}
