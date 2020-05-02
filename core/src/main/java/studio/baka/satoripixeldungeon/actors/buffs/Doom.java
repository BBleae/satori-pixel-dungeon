package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;

public class Doom extends Buff {

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.DARKENED);
        else if (target.invisible == 0) target.sprite.remove(CharSprite.State.DARKENED);
    }

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}
