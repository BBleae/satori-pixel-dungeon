package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.utils.GLog;

public class PotionOfStrength extends Potion {

    {
        initials = 10;
    }

    @Override
    public void apply(Hero hero) {
        setKnown();

        hero.STR++;
        hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "msg_1"));
        GLog.p(Messages.get(this, "msg_2"));

        Badges.validateStrengthAttained();
    }

    @Override
    public int price() {
        return isKnown() ? 50 * quantity : super.price();
    }
}
