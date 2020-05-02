package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfSharpshooting extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", soloBonus(), new DecimalFormat("#.##").format(100f * (Math.pow(1.2, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", 1, new DecimalFormat("#.##").format(20f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Aim();
    }

    public static int levelDamageBonus(Char target) {
        return getBonus(target, RingOfSharpshooting.Aim.class);
    }

    public static float durabilityMultiplier(Char target) {
        return (float) (Math.pow(1.2, getBonus(target, Aim.class)));
    }

    public class Aim extends RingBuff {
    }
}
