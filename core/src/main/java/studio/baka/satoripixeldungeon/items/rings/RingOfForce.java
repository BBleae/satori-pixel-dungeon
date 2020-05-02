package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public class RingOfForce extends Ring {

    @Override
    protected RingBuff buff() {
        return new Force();
    }

    public static int armedDamageBonus(Char ch) {
        return getBonus(ch, Force.class);
    }


    // *** Weapon-like properties ***

    private static float tier(int str) {
        float tier = Math.max(1, (str - 8) / 2f);
        //each str point after 18 is half as effective
        if (tier > 5) {
            tier = 5 + (tier - 5) / 2f;
        }
        return tier;
    }

    public static int damageRoll(Hero hero) {
        if (hero.buff(Force.class) != null) {
            int level = getBonus(hero, Force.class);
            float tier = tier(hero.STR());
            return Random.NormalIntRange(min(level, tier), max(level, tier));
        } else {
            //attack without any ring of force influence
            return Random.NormalIntRange(1, Math.max(hero.STR() - 8, 1));
        }
    }

    //same as equivalent tier weapon
    private static int min(int lvl, float tier) {
        return Math.max(0, Math.round(
                tier +  //base
                        lvl     //level scaling
        ));
    }

    //same as equivalent tier weapon
    private static int max(int lvl, float tier) {
        return Math.max(0, Math.round(
                5 * (tier + 1) +    //base
                        lvl * (tier + 1)    //level scaling
        ));
    }

    @Override
    public String statsInfo() {
        float tier = tier(Dungeon.hero.STR());
        if (isIdentified()) {
            return Messages.get(this, "stats", min(soloBonus(), tier), max(soloBonus(), tier), soloBonus());
        } else {
            return Messages.get(this, "typical_stats", min(1, tier), max(1, tier), 1);
        }
    }

    public class Force extends RingBuff {
    }
}

