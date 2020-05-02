package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.WarlockSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Warlock extends Mob implements Callback {

    private static final float TIME_TO_ZAP = 1f;

    {
        spriteClass = WarlockSprite.class;

        HP = HT = 70;
        defenseSkill = 18;

        EXP = 11;
        maxLvl = 21;

        loot = Generator.Category.POTION;
        lootChance = 0.83f;

        properties.add(Property.UNDEAD);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 22);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    protected boolean doAttack(Char enemy) {

        if (Dungeon.level.adjacent(pos, enemy.pos)) {

            return super.doAttack(enemy);

        } else {

            if (sprite != null && sprite.visible) {
                sprite.zap(enemy.pos);
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    //used so resistances can differentiate between melee and magical attacks
    public static class DarkBolt {
    }

    private void zap() {
        spend(TIME_TO_ZAP);

        if (hit(this, enemy, true)) {
            if (enemy == Dungeon.hero && Random.Int(2) == 0) {
                Buff.prolong(enemy, Weakness.class, Weakness.DURATION);
            }

            int dmg = Random.Int(12, 18);
            enemy.damage(dmg, new DarkBolt());

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "bolt_kill"));
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }

    @Override
    public Item createLoot() {
        Item loot = super.createLoot();

        if (loot instanceof PotionOfHealing) {

            //count/10 chance of not dropping potion
            if (Random.Float() < ((8f - Dungeon.LimitedDrops.WARLOCK_HP.count) / 8f)) {
                Dungeon.LimitedDrops.WARLOCK_HP.count++;
            } else {
                return null;
            }

        }

        return loot;
    }

    {
        resistances.add(Grim.class);
    }
}
