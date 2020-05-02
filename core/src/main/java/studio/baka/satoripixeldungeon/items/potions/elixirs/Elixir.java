package studio.baka.satoripixeldungeon.items.potions.elixirs;

import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.potions.Potion;

public abstract class Elixir extends Potion {

    public abstract void apply(Hero hero);

    @Override
    public boolean isKnown() {
        return true;
    }
}
