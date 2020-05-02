package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Mana extends Buff implements Hero.Doom {

    private static final float REGEN_TIME = 10f;

    private float level;

    private static final String LEVEL = "level";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getFloat(LEVEL);
    }

    @Override
    public boolean act() {
        if (target != Dungeon.hero) detach();

        if (Dungeon.level.locked || target.buff(WellFed.class) != null ||
                target.buff(Bless.class) != null ||
                target.buff(Recharging.class) != null) {
            Hero hero = (Hero) target;
            manaregin(hero);
            spend(REGEN_TIME / 2);
            return true;
        }

        if (target.isAlive() && target instanceof Hero) {
            Hero hero = (Hero) target;
            manaregin(hero);

            if (Dungeon.hero.heroClass != HeroClass.HUNTRESS) {
                spend(REGEN_TIME);
            } else {
                spend(REGEN_TIME / 2);
            }
        } else {
            diactivate();
        }

        return true;
    }

    private void manaregin(Hero hero) {
        int regenmana = (int) (hero.getMaxmana() * 0.1f);
        if (regenmana == 0) regenmana = 1;

        float manafactor;
        if (hero.heroClass == HeroClass.HUNTRESS) manafactor = 1.2f;
        else manafactor = 1f;

        if (hero.mana < hero.getMaxmana() && hero.mana + (int) (regenmana * manafactor) >= hero.getMaxmana()) {
            hero.setMana(hero.getMaxmana());
            GLog.w(Messages.get(this, "mana_full"));
        } else if (hero.mana + (int) (regenmana * manafactor) < hero.getMaxmana()) {
            hero.setMana(hero.mana + (regenmana * regenmana));
        }

        if (hero.mana > hero.getMaxmana()) hero.setMana(hero.getMaxmana());
    }

    @Override
    public int icon() {
        return BuffIndicator.NONE;
    }

    @Override
    public String toString() {
        return Messages.get(this, "mana");
    }

    @Override
    public String desc() {
        String result;
        result = Messages.get(this, "current_mana");

        result += Messages.get(this, "desc");

        return result;
    }

    @Override
    public void onDeath() {

    }
}
