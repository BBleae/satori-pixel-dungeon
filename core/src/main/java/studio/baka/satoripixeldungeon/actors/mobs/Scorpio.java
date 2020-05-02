package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.buffs.Light;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ScorpioSprite;
import com.watabou.utils.Random;

public class Scorpio extends Mob {

    {
        spriteClass = ScorpioSprite.class;

        HP = HT = 95;
        defenseSkill = 24;
        viewDistance = Light.DISTANCE;

        EXP = 14;
        maxLvl = 25;

        loot = new PotionOfHealing();
        lootChance = 0.2f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(26, 36);
    }

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 16);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        Ballistica attack = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
        return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.prolong(enemy, Cripple.class, Cripple.DURATION);
        }

        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        if (state == HUNTING) {
            return enemySeen && getFurther(target);
        } else {
            return super.getCloser(target);
        }
    }

    @Override
    protected Item createLoot() {
        //(9-count) / 9 chance of getting healing, otherwise mystery meat
        if (Random.Float() < ((9f - Dungeon.LimitedDrops.SCORPIO_HP.count) / 9f)) {
            Dungeon.LimitedDrops.SCORPIO_HP.count++;
            return (Item) loot;
        } else {
            return new MysteryMeat();
        }
    }

}
