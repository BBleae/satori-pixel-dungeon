package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.actors.Char;
import com.watabou.utils.Bundle;

public abstract class ShieldBuff extends Buff {

    private int shielding;

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.needsShieldUpdate = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        target.needsShieldUpdate = true;
        super.detach();
    }

    public int shielding() {
        return shielding;
    }

    public void setShield(int shield) {
        this.shielding = shield;
        target.needsShieldUpdate = true;
    }

    public void incShield() {
        incShield(1);
    }

    public void incShield(int amt) {
        shielding += amt;
        target.needsShieldUpdate = true;
    }

    public void decShield() {
        decShield(1);
    }

    public void decShield(int amt) {
        shielding -= amt;
        target.needsShieldUpdate = true;
    }

    //returns the amount of damage leftover
    public int absorbDamage(int dmg) {
        if (shielding >= dmg) {
            shielding -= dmg;
            dmg = 0;
        } else {
            dmg -= shielding;
            shielding = 0;
        }
        if (shielding == 0) {
            detach();
        }
        target.needsShieldUpdate = true;
        return dmg;
    }

    private static final String SHIELDING = "shielding";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SHIELDING, shielding);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        shielding = bundle.getInt(SHIELDING);
    }

}
