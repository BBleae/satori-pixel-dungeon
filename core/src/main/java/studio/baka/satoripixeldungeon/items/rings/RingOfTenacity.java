package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfTenacity extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.85f, soloBonus()))));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(15f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Tenacity();
    }

    public static float damageMultiplier(Char t) {
        //(HT - HP)/HT = heroes current % missing health.
        return (float) Math.pow(0.85, getBonus(t, Tenacity.class) * ((float) (t.HT - t.HP) / t.HT));
    }

    public class Tenacity extends RingBuff {
    }
}

