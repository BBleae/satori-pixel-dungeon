package studio.baka.satoripixeldungeon.items.rings;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Electricity;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.actors.mobs.Eye;
import studio.baka.satoripixeldungeon.actors.mobs.Shaman;
import studio.baka.satoripixeldungeon.actors.mobs.Warlock;
import studio.baka.satoripixeldungeon.actors.mobs.Yog;
import studio.baka.satoripixeldungeon.items.wands.*;
import studio.baka.satoripixeldungeon.levels.traps.DisintegrationTrap;
import studio.baka.satoripixeldungeon.levels.traps.GrimTrap;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;
import java.util.HashSet;

public class RingOfElements extends Ring {

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.80f, soloBonus()))));
        } else {
            return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(20f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Resistance();
    }

    public static final HashSet<Class> RESISTS = new HashSet<>();

    static {
        RESISTS.add(Burning.class);
        RESISTS.add(Charm.class);
        RESISTS.add(Chill.class);
        RESISTS.add(Frost.class);
        RESISTS.add(Ooze.class);
        RESISTS.add(Paralysis.class);
        RESISTS.add(Poison.class);
        RESISTS.add(Corrosion.class);
        RESISTS.add(Weakness.class);

        RESISTS.add(DisintegrationTrap.class);
        RESISTS.add(GrimTrap.class);

        RESISTS.add(WandOfBlastWave.class);
        RESISTS.add(WandOfDisintegration.class);
        RESISTS.add(WandOfFireblast.class);
        RESISTS.add(WandOfFrost.class);
        RESISTS.add(WandOfLightning.class);
        RESISTS.add(WandOfLivingEarth.class);
        RESISTS.add(WandOfMagicMissile.class);
        RESISTS.add(WandOfPrismaticLight.class);
        RESISTS.add(WandOfTransfusion.class);
        RESISTS.add(WandOfWarding.Ward.class);

        RESISTS.add(ToxicGas.class);
        RESISTS.add(Electricity.class);

        RESISTS.add(Shaman.LightningBolt.class);
        RESISTS.add(Warlock.DarkBolt.class);
        RESISTS.add(Eye.DeathGaze.class);
        RESISTS.add(Yog.BurningFist.DarkBolt.class);
    }

    public static float resist(Char target, Class effect) {
        if (getBonus(target, Resistance.class) == 0) return 1f;

        for (Class c : RESISTS) {
            if (c.isAssignableFrom(effect)) {
                return (float) Math.pow(0.80, getBonus(target, Resistance.class));
            }
        }

        return 1f;
    }

    public class Resistance extends RingBuff {

    }
}
