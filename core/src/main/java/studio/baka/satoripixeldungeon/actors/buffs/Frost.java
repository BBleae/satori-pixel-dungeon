package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Thief;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.food.FrozenCarpaccio;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.PotionOfStrength;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Frost extends FlavourBuff {

    private static final float DURATION = 5f;

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {

            target.paralysed++;
            Buff.detach(target, Burning.class);
            Buff.detach(target, Chill.class);

            if (target instanceof Hero) {

                Hero hero = (Hero) target;
                ArrayList<Item> freezable = new ArrayList<>();
                //does not reach inside of containers
                for (Item i : hero.belongings.backpack.items) {
                    if ((i instanceof Potion && !(i instanceof PotionOfStrength))
                            || i instanceof MysteryMeat) {
                        freezable.add(i);
                    }
                }

                if (!freezable.isEmpty()) {
                    Item toFreeze = Random.element(freezable).detach(hero.belongings.backpack);
                    GLog.w(Messages.get(this, "freezes", toFreeze.toString()));
                    if (toFreeze instanceof Potion) {
                        ((Potion) toFreeze).shatter(hero.pos);
                    } else if (toFreeze instanceof MysteryMeat) {
                        FrozenCarpaccio carpaccio = new FrozenCarpaccio();
                        if (!carpaccio.collect(hero.belongings.backpack)) {
                            Dungeon.level.drop(carpaccio, target.pos).sprite.drop();
                        }
                    }
                }

            } else if (target instanceof Thief) {

                Item item = ((Thief) target).item;

                if (item instanceof Potion && !(item instanceof PotionOfStrength)) {
                    ((Potion) ((Thief) target).item).shatter(target.pos);
                    ((Thief) target).item = null;
                } else if (item instanceof MysteryMeat) {
                    ((Thief) target).item = new FrozenCarpaccio();
                }

            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (target.paralysed > 0)
            target.paralysed--;
        if (Dungeon.level.water[target.pos])
            Buff.prolong(target, Chill.class, 4f);
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.FROZEN);
        else target.sprite.remove(CharSprite.State.FROZEN);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static float duration(Char ch) {
        return DURATION;
    }
}
