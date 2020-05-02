package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.sprites.RotLasherSprite;
import com.watabou.utils.Random;

public class RotLasher extends Mob {

    {
        spriteClass = RotLasherSprite.class;

        HP = HT = 40;
        defenseSkill = 0;

        EXP = 1;

        loot = Generator.Category.SEED;
        lootChance = 1f;

        state = WANDERING = new Waiting();

        properties.add(Property.IMMOVABLE);
        properties.add(Property.MINIBOSS);
    }

    @Override
    protected boolean act() {
        if (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)) {
            HP = Math.min(HT, HP + 3);
        }
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        if (src instanceof Burning) {
            destroy();
            sprite.die();
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        Buff.affect(enemy, Cripple.class, 2f);
        return super.attackProc(enemy, damage);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected boolean getCloser(int target) {
        return true;
    }

    @Override
    protected boolean getFurther(int target) {
        return true;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(8, 15);
    }

    @Override
    public int attackSkill(Char target) {
        return 15;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    {
        immunities.add(ToxicGas.class);
    }

    private class Waiting extends Mob.Wandering {
    }
}
