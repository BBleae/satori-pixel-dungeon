package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Light extends FlavourBuff {

    {
        type = buffType.POSITIVE;
    }

    public static final float DURATION = 300f;
    public static final int DISTANCE = 6;

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (Dungeon.level != null) {
                target.viewDistance = Math.max(Dungeon.level.viewDistance, DISTANCE);
                Dungeon.observe();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        target.viewDistance = Dungeon.level.viewDistance;
        Dungeon.observe();
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.LIGHT;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon, 20f, cooldown());
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
        else target.sprite.remove(CharSprite.State.ILLUMINATED);
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
