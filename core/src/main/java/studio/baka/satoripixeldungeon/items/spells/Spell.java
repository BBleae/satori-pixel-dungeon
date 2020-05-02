package studio.baka.satoripixeldungeon.items.spells;


import studio.baka.satoripixeldungeon.actors.buffs.MagicImmune;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;

import java.util.ArrayList;

public abstract class Spell extends Item {

    public static final String AC_CAST = "CAST";

    {
        stackable = true;
        defaultAction = AC_CAST;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_CAST);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_CAST)) {

            if (curUser.buff(MagicImmune.class) != null) {
                GLog.w(Messages.get(this, "no_magic"));
                return;
            }

            onCast(hero);

        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    protected abstract void onCast(Hero hero);

}
