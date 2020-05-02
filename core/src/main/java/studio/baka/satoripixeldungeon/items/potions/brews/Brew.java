package studio.baka.satoripixeldungeon.items.potions.brews;

import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.scenes.GameScene;

import java.util.ArrayList;

public abstract class Brew extends Potion {

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_DRINK);
        return actions;
    }

    @Override
    public void setAction() {
        defaultAction = AC_THROW;
    }


    @Override
    public void doThrow(Hero hero) {
        GameScene.selectCell(thrower);
    }

    @Override
    public abstract void shatter(int cell);

    @Override
    public boolean isKnown() {
        return true;
    }

}
