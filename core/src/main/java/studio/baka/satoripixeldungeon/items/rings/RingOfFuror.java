package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfFuror extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.105f, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(10.5f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Furor();
    }

    public static float attackDelayMultiplier(Char target) {
        return 1f / (float) Math.pow(1.105, getBonus(target, Furor.class));
    }

    public class Furor extends RingBuff {
    }
}
