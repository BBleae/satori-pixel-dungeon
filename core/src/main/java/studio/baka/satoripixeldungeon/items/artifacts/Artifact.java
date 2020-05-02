package studio.baka.satoripixeldungeon.items.artifacts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.KindofMisc;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Artifact extends KindofMisc {

    protected Buff passiveBuff;
    protected Buff activeBuff;

    //level is used internally to track upgrades to artifacts, size/logic varies per artifact.
    //already inherited from item superclass
    //exp is used to count progress towards levels for some artifacts
    protected int exp = 0;
    //levelCap is the artifact's maximum level
    protected int levelCap = 0;

    //the current artifact charge
    protected int charge = 0;
    //the build towards next charge, usually rolls over at 1.
    //better to keep charge as an int and use a separate float than casting.
    protected float partialCharge = 0;
    //the maximum charge, varies per artifact, not all artifacts use this.
    protected int chargeCap = 0;

    //used by some artifacts to keep track of duration of effects or cooldowns to use.
    protected int cooldown = 0;

    @Override
    public boolean doEquip(final Hero hero) {

        if ((hero.belongings.misc1 != null && hero.belongings.misc1.getClass() == this.getClass())
                || (hero.belongings.misc2 != null && hero.belongings.misc2.getClass() == this.getClass())
                || (hero.belongings.misc3 != null && hero.belongings.misc3.getClass() == this.getClass())) {

            GLog.w(Messages.get(Artifact.class, "cannot_wear_two"));
            return false;

        } else {

            if (super.doEquip(hero)) {

                identify();
                return true;

            } else {

                return false;

            }

        }

    }

    public void activate(Char ch) {
        passiveBuff = passiveBuff();
        passiveBuff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {

            passiveBuff.detach();
            passiveBuff = null;

            if (activeBuff != null) {
                activeBuff.detach();
                activeBuff = null;
            }

            return true;

        } else {

            return false;

        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int visiblyUpgraded() {
        return levelKnown ? Math.round((level() * 10) / (float) levelCap) : 0;
    }

    //transfers upgrades from another artifact, transfer level will equal the displayed level
    public void transferUpgrade(int transferLvl) {
        upgrade(Math.round((float) (transferLvl * levelCap) / 10));
    }

    @Override
    public String info() {
        if (cursed && cursedKnown && !isEquipped(Dungeon.hero)) {
            return desc() + "\n\n" + Messages.get(Artifact.class, "curse_known");

        } else if (!isIdentified() && cursedKnown && !isEquipped(Dungeon.hero)) {
            return desc() + "\n\n" + Messages.get(Artifact.class, "not_cursed");

        } else {
            return desc();

        }
    }

    @Override
    public String status() {

        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified() || cursed) {
            return null;
        }

        //display the current cooldown
        if (cooldown != 0)
            return Messages.format("%d", cooldown);

        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);

        //display as #/#
        if (chargeCap > 0)
            return Messages.format("%d/%d", charge, chargeCap);

        //if there's no cap -
        //- but there is charge anyway, display that charge
        if (charge != 0)
            return Messages.format("%d", charge);

        //otherwise, if there's no charge, return null.
        return null;
    }

    @Override
    public Item random() {
        //always +0

        //30% chance to be cursed
        if (Random.Float() < 0.3f) {
            cursed = true;
        }
        return this;
    }

    @Override
    public int price() {
        int price = 100;
        if (level() > 0)
            price += 20 * visiblyUpgraded();
        if (cursed && cursedKnown) {
            price /= 2;
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }


    protected ArtifactBuff passiveBuff() {
        return null;
    }

    protected ArtifactBuff activeBuff() {
        return null;
    }

    public void charge(Hero target) {
        //do nothing by default;
    }

    public class ArtifactBuff extends Buff {

        public int itemLevel() {
            return level();
        }

        public boolean isCursed() {
            return cursed;
        }

    }

    private static final String EXP = "exp";
    private static final String CHARGE = "charge";
    private static final String PARTIALCHARGE = "partialcharge";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(EXP, exp);
        bundle.put(CHARGE, charge);
        bundle.put(PARTIALCHARGE, partialCharge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        exp = bundle.getInt(EXP);
        if (chargeCap > 0) charge = Math.min(chargeCap, bundle.getInt(CHARGE));
        else charge = bundle.getInt(CHARGE);
        partialCharge = bundle.getFloat(PARTIALCHARGE);
    }
}
