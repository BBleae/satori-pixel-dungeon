package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.AcidicSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Acidic extends Scorpio {

    {
        spriteClass = AcidicSprite.class;

        properties.add(Property.ACIDIC);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        int dmg = Random.IntRange(0, damage);
        if (dmg > 0) {
            enemy.damage(dmg, this);
            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(getClass());
                GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name)));
            }
        }

        return super.defenseProc(enemy, damage);
    }

}
