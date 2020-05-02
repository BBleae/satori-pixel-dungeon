package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfAccuracy extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.3f, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(30f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Accuracy();
    }

    public static float accuracyMultiplier(Char target) {
        return (float) Math.pow(1.3f, getBonus(target, Accuracy.class));
    }

    public class Accuracy extends RingBuff {
    }
}
