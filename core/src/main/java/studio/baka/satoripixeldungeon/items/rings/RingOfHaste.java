package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfHaste extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.2f, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(20f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Haste();
    }

    public static float speedMultiplier(Char target) {
        return (float) Math.pow(1.2, getBonus(target, Haste.class));
    }

    public class Haste extends RingBuff {
    }
}
