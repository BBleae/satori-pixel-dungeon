package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Chill;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.items.potions.PotionOfLiquidFlame;
import studio.baka.satoripixeldungeon.sprites.ElementalSprite;
import com.watabou.utils.Random;

public class Elemental extends Mob {

    {
        spriteClass = ElementalSprite.class;

        HP = HT = 65;
        defenseSkill = 20;

        EXP = 10;
        maxLvl = 20;

        flying = true;

        loot = new PotionOfLiquidFlame();
        lootChance = 0.1f;

        properties.add(Property.FIERY);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 26);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }

        return damage;
    }

    @Override
    public void add(Buff buff) {
        if (buff instanceof Frost || buff instanceof Chill) {
            if (Dungeon.level.water[this.pos])
                damage(Random.NormalIntRange(HT / 2, HT), buff);
            else
                damage(Random.NormalIntRange(1, HT * 2 / 3), buff);
        } else {
            super.add(buff);
        }
    }

}
