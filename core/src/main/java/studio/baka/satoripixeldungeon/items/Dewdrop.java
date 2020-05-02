package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Dewdrop extends Item {

    {
        image = ItemSpriteSheet.DEWDROP;

        stackable = true;
        dropsDownHeap = true;
    }

    @Override
    public boolean doPickUp(Hero hero) {

        DewVial vial = hero.belongings.getItem(DewVial.class);

        if (vial != null && vial.isNotFull()) {

            vial.collectDew(this);

        } else {

            //20 drops for a full heal
            int heal = Math.round(hero.HT * 0.05f * quantity);

            int effect = Math.min(hero.HT - hero.HP, heal);
            if (effect > 0) {
                hero.HP += effect;
                hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "value", effect));
            } else {
                GLog.i(Messages.get(this, "already_full"));
                return false;
            }

        }

        Sample.INSTANCE.play(Assets.SND_DEWDROP);
        hero.spendAndNext(TIME_TO_PICK_UP);

        return true;
    }

    @Override
    //max of one dew in a stack
    public Item quantity(int value) {
        quantity = Math.min(value, 1);
        return this;
    }

}
