package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.weapon.missiles.ThrowingKnife;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.ActionIndicator;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class SnipersMark extends FlavourBuff implements ActionIndicator.Action {

    public int object = 0;

    private static final String OBJECT = "object";

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OBJECT, object);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        object = bundle.getInt(OBJECT);
    }

    @Override
    public int icon() {
        return BuffIndicator.MARK;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public Image getIcon() {
        return new ItemSprite(ItemSpriteSheet.THROWING_KNIFE, null);
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero;
        if (hero == null) return;

        ThrowingKnife throwingKnife = hero.belongings.getItem(ThrowingKnife.class);
        if (throwingKnife == null) return;

        Char ch = (Char) Actor.findById(object);
        if (ch == null) return;

        int cell = QuickSlotButton.autoAim(ch, throwingKnife);
        if (cell == -1) return;

        throwingKnife.cast(hero, cell);
        detach();
    }
}
