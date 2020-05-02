package studio.baka.satoripixeldungeon.actors.buffs;

import com.watabou.noosa.Image;

//buff whose only internal logic is to wait and detach after a time.
public class FlavourBuff extends Buff {

    @Override
    public boolean act() {
        detach();
        return true;
    }

    public static void greyIcon(Image icon, float startGrey, float remaining) {
        if (remaining >= startGrey) {
            icon.resetColor();
        } else {
            icon.tint(0xb3b3b3, 0.6f + 0.3f * (startGrey - remaining) / startGrey);
        }
    }

    //flavour buffs can all just rely on cooldown()
    protected String dispTurns() {
        //add one turn as buffs act last, we want them to end at 1 visually, even if they end at 0 internally.
        return dispTurns(cooldown() + 1f);
    }
}
