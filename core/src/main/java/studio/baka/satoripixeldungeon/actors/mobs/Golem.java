package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Amok;
import studio.baka.satoripixeldungeon.actors.buffs.Sleep;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Imp;
import studio.baka.satoripixeldungeon.sprites.GolemSprite;
import com.watabou.utils.Random;

public class Golem extends Mob {

    {
        spriteClass = GolemSprite.class;

        HP = HT = 85;
        defenseSkill = 18;

        EXP = 12;
        maxLvl = 22;

        properties.add(Property.INORGANIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(25, 40);
    }

    @Override
    public int attackSkill(Char target) {
        return 28;
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay() * 1.5f;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 12);
    }

    @Override
    public void rollToDropLoot() {
        Imp.Quest.process(this);

        super.rollToDropLoot();
    }

    {
        immunities.add(Amok.class);
        immunities.add(Terror.class);
        immunities.add(Sleep.class);
    }
}
