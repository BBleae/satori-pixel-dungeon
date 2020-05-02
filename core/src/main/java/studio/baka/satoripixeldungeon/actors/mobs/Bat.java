package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.sprites.BatSprite;
import com.watabou.utils.Random;

public class Bat extends Mob {

    {
        spriteClass = BatSprite.class;

        HP = HT = 30;
        defenseSkill = 15;
        baseSpeed = 2f;

        EXP = 7;
        maxLvl = 15;

        flying = true;

        loot = new PotionOfHealing();
        lootChance = 0.1667f; //by default, see rollToDropLoot()
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 18);
    }

    @Override
    public int attackSkill(Char target) {
        return 16;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        int reg = Math.min(damage, HT - HP);

        if (reg > 0) {
            HP += reg;
            sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
        }

        return damage;
    }

    @Override
    public void rollToDropLoot() {
        lootChance *= ((7f - Dungeon.LimitedDrops.BAT_HP.count) / 7f);
        super.rollToDropLoot();
    }

    @Override
    protected Item createLoot() {
        Dungeon.LimitedDrops.BAT_HP.count++;
        return super.createLoot();
    }

}
