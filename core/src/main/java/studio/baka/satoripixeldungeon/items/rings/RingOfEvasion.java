package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfEvasion extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.15f, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(15f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Evasion();
    }

    public static float evasionMultiplier(Char target) {
        return (float) Math.pow(1.15, getBonus(target, Evasion.class));
    }

    public class Evasion extends RingBuff {
    }
}
