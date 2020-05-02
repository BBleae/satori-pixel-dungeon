package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.BrokenSeal;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

abstract public class ClassArmor extends Armor {

    private static final String AC_SPECIAL = "SPECIAL";

    {
        levelKnown = true;
        cursedKnown = true;
        defaultAction = AC_SPECIAL;

        bones = false;
    }

    private int armorTier;

    public ClassArmor() {
        super(6);
    }

    public static ClassArmor upgrade(Hero owner, Armor armor) {

        ClassArmor classArmor = null;

        switch (owner.heroClass) {
            case WARRIOR:
            default:
                classArmor = new WarriorArmor();
                BrokenSeal seal = armor.checkSeal();
                if (seal != null) {
                    classArmor.affixSeal(seal);
                }
                break;
            case ROGUE:
                classArmor = new RogueArmor();
                break;
            case MAGE:
                classArmor = new MageArmor();
                break;
            case HUNTRESS:
                classArmor = new HuntressArmor();
                break;
            case MAHOU_SHOUJO:
                classArmor = new MahoArmor();
        }

        classArmor.level(armor.level() - (armor.curseInfusionBonus ? 1 : 0));
        classArmor.armorTier = armor.tier;
        classArmor.augment = armor.augment;
        classArmor.inscribe(armor.glyph);
        classArmor.cursed = armor.cursed;
        classArmor.curseInfusionBonus = armor.curseInfusionBonus;
        classArmor.identify();

        return classArmor;
    }

    private static final String ARMOR_TIER = "armortier";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ARMOR_TIER, armorTier);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        armorTier = bundle.getInt(ARMOR_TIER);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.HP >= 3 && isEquipped(hero)) {
            actions.add(AC_SPECIAL);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SPECIAL)) {

            if (hero.HP < 3) {
                GLog.w(Messages.get(this, "low_hp"));
            } else if (!isEquipped(hero)) {
                GLog.w(Messages.get(this, "not_equipped"));
            } else {
                curUser = hero;
                Invisibility.dispel();
                doSpecial();
            }

        }
    }

    abstract public void doSpecial();

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);

        //strength req decreases at +1,+3,+6,+10,etc.
        return (8 + Math.round(armorTier * 2)) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public int DRMax(int lvl) {
        int max = armorTier * (2 + lvl) + augment.defenseFactor(lvl);
        if (lvl > max) {
            return ((lvl - max) + 1) / 2;
        } else {
            return max;
        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int price() {
        return 0;
    }

}
