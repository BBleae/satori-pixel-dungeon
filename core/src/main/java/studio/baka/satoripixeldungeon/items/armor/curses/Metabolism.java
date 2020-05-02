package studio.baka.satoripixeldungeon.items.armor.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.Armor.Glyph;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;

public class Metabolism extends Glyph {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (Random.Int(6) == 0 && defender instanceof Hero) {

            //assumes using up 10% of starving, and healing of 1 hp per 10 turns;
            int healing = Math.min((int) Hunger.STARVING / 100, defender.HT - defender.HP);

            if (healing > 0) {

                Hunger hunger = Buff.affect(defender, Hunger.class);

                if (hunger != null && !hunger.isStarving()) {

                    hunger.reduceHunger(healing * -10);
                    BuffIndicator.refreshHero();

                    defender.HP += healing;
                    defender.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                    defender.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healing));
                }
            }

        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}
