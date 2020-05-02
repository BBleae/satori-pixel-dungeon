package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Charm;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.actors.mobs.Eye;
import studio.baka.satoripixeldungeon.actors.mobs.Shaman;
import studio.baka.satoripixeldungeon.actors.mobs.Warlock;
import studio.baka.satoripixeldungeon.actors.mobs.Yog;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.wands.*;
import studio.baka.satoripixeldungeon.levels.traps.DisintegrationTrap;
import studio.baka.satoripixeldungeon.levels.traps.GrimTrap;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class AntiMagic extends Armor.Glyph {

    private static final ItemSprite.Glowing TEAL = new ItemSprite.Glowing(0x88EEFF);

    @SuppressWarnings("rawtypes")
    public static final HashSet<Class> RESISTS = new HashSet<>();

    static {
        RESISTS.add(Charm.class);
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

        RESISTS.add(Shaman.LightningBolt.class);
        RESISTS.add(Warlock.DarkBolt.class);
        RESISTS.add(Eye.DeathGaze.class);
        RESISTS.add(Yog.BurningFist.DarkBolt.class);
    }

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        //no proc effect, see Hero.damage
        return damage;
    }

    public static int drRoll(int level) {
        return Random.NormalIntRange(level, 4 + (level * 2));
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return TEAL;
    }

}