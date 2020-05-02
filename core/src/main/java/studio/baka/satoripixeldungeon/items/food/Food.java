package studio.baka.satoripixeldungeon.items.food;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Food extends Item {

    public static final float TIME_TO_EAT = 3f;

    public static final String AC_EAT = "EAT";

    public float energy = Hunger.HUNGRY;
    public String message = Messages.get(this, "eat_msg");

    {
        stackable = true;
        image = ItemSpriteSheet.RATION;

        bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_EAT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_EAT)) {

            detach(hero.belongings.backpack);

            satisfy(hero);
            GLog.i(message);

            foodProc(hero);

            hero.sprite.operate(hero.pos);
            hero.busy();
            SpellSprite.show(hero, SpellSprite.FOOD);
            Sample.INSTANCE.play(Assets.SND_EAT);

            hero.spend(TIME_TO_EAT);

            Statistics.foodEaten++;
            Badges.validateFoodEaten();

        }
    }

    protected void satisfy(Hero hero) {
        Buff.affect(hero, Hunger.class).satisfy(energy);
    }

    public static void foodProc(Hero hero) {
        switch (hero.heroClass) {
            case WARRIOR:
                if (hero.HP < hero.HT) {
                    hero.HP = Math.min(hero.HP + 5, hero.HT);
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                }
                break;
            case MAGE:
                //1 charge
                Buff.affect(hero, Recharging.class, 4f);
                ScrollOfRecharging.charge(hero);
                break;
            case ROGUE:
            case HUNTRESS:
                break;
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }
}
