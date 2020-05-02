package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.items.armor.glyphs.AntiMagic;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class MagicImmune extends FlavourBuff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    {
        immunities.addAll(AntiMagic.RESISTS);
    }

    //FIXME what about active buffs/debuffs?, what about rings? what about artifacts?

    @Override
    public int icon() {
        return BuffIndicator.COMBO;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0, 1, 0);
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
