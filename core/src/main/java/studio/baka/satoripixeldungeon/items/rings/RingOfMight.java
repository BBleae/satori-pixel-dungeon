package studio.baka.satoripixeldungeon.items.rings;


import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfMight extends Ring {

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            hero.updateHT(false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            hero.updateHT(false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        updateTargetHT();
        return this;
    }

    @Override
    public void level(int value) {
        super.level(value);
        updateTargetHT();
    }

    private void updateTargetHT() {
        if (buff != null && buff.target instanceof Hero) {
            ((Hero) buff.target).updateHT(false);
        }
    }

    public String statsInfo() {
        if (isIdentified()) {
            return Messages.get(this, "stats", soloBonus(), new DecimalFormat("#.##").format(100f * (Math.pow(1.035, soloBonus()) - 1f)));
        } else {
            return Messages.get(this, "typical_stats", 1, new DecimalFormat("#.##").format(3.5f));
        }
    }

    @Override
    protected RingBuff buff() {
        return new Might();
    }

    public static int strengthBonus(Char target) {
        return getBonus(target, Might.class);
    }

    public static float HTMultiplier(Char target) {
        return (float) Math.pow(1.035, getBonus(target, Might.class));
    }

    public class Might extends RingBuff {
    }
}

