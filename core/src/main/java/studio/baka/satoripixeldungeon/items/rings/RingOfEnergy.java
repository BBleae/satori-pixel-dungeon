package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfEnergy extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.30f, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(30f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Energy();
    }

    public static float wandChargeMultiplier(Char target) {
        return (float) Math.pow(1.30, getBonus(target, Energy.class));
    }

    public class Energy extends RingBuff {
    }
}
