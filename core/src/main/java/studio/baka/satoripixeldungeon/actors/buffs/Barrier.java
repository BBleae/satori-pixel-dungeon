package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Barrier extends ShieldBuff {

    {
        type = buffType.POSITIVE;
    }

    @Override
    public boolean act() {

        absorbDamage(1);

        if (shielding() <= 0) {
            detach();
        }

        spend(TICK);

        return true;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.SHIELDED);
        else target.sprite.remove(CharSprite.State.SHIELDED);
    }

    @Override
    public int icon() {
        return BuffIndicator.ARMOR;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.tint(0, 0.5f, 1, 0.5f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", shielding());
    }
}
