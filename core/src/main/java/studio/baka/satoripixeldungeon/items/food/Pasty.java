package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

import java.util.Calendar;

public class Pasty extends Food {

    //TODO: implement fun stuff for other holidays
    //TODO: probably should externalize this if I want to add any more festive stuff.
    private enum Holiday {
        NONE,
        EASTER, //TBD
        HWEEN,//2nd week of october though first day of november
        XMAS //3rd week of december through first week of january
    }

    private static Holiday holiday;

    static {

        holiday = Holiday.NONE;

        final Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                if (calendar.get(Calendar.WEEK_OF_MONTH) == 1)
                    holiday = Holiday.XMAS;
                break;
            case Calendar.OCTOBER:
                if (calendar.get(Calendar.WEEK_OF_MONTH) >= 2)
                    holiday = Holiday.HWEEN;
                break;
            case Calendar.NOVEMBER:
                if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
                    holiday = Holiday.HWEEN;
                break;
            case Calendar.DECEMBER:
                if (calendar.get(Calendar.WEEK_OF_MONTH) >= 3)
                    holiday = Holiday.XMAS;
                break;
        }
    }

    {
        reset();

        energy = Hunger.STARVING;

        bones = true;
    }

    @Override
    public void reset() {
        super.reset();
        switch (holiday) {
            case NONE:
                name = Messages.get(this, "pasty");
                image = ItemSpriteSheet.PASTY;
                break;
            case HWEEN:
                name = Messages.get(this, "pie");
                image = ItemSpriteSheet.PUMPKIN_PIE;
                break;
            case XMAS:
                name = Messages.get(this, "cane");
                image = ItemSpriteSheet.CANDY_CANE;
                break;
        }
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);

        switch (holiday) {
            case NONE:
                break; //do nothing extra
            case HWEEN:
                //heals for 10% max hp
                hero.HP = Math.min(hero.HP + hero.HT / 10, hero.HT);
                hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                break;
            case XMAS:
                Buff.affect(hero, Recharging.class, 2f); //half of a charge
                ScrollOfRecharging.charge(hero);
                break;
        }
    }

    @Override
    public String info() {
        switch (holiday) {
            case NONE:
            default:
                return Messages.get(this, "pasty_desc");
            case HWEEN:
                return Messages.get(this, "pie_desc");
            case XMAS:
                return Messages.get(this, "cane_desc");
        }
    }

    @Override
    public int price() {
        return 20 * quantity;
    }
}
