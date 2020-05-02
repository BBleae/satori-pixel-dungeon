package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Levitation extends FlavourBuff {

    {
        type = buffType.POSITIVE;
    }

    public static final float DURATION = 20f;

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.flying = true;
            Roots.detach(target, Roots.class);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        target.flying = false;
        Dungeon.level.occupyCell(target);
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.LEVITATION;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon, 5f, cooldown());
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.LEVITATING);
        else target.sprite.remove(CharSprite.State.LEVITATING);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }
}
