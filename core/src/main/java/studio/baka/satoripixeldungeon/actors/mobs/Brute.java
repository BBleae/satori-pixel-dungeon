package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.BruteSprite;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Brute extends Mob {

    {
        spriteClass = BruteSprite.class;

        HP = HT = 40;
        defenseSkill = 15;

        EXP = 8;
        maxLvl = 16;

        loot = Gold.class;
        lootChance = 0.5f;
    }

    private boolean enraged = false;

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        enraged = HP < HT / 4;
    }

    @Override
    public int damageRoll() {
        return enraged ?
                Random.NormalIntRange(15, 45) :
                Random.NormalIntRange(6, 26);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);

        if (isAlive() && !enraged && HP < HT / 4) {
            enraged = true;
            spend(TICK);
            if (Dungeon.level.heroFOV[pos]) {
                sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
            }
        }
    }

    {
        immunities.add(Terror.class);
    }
}
