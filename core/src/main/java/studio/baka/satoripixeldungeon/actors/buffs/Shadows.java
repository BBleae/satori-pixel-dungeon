package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Shadows extends Invisibility {

    protected float left;

    private static final String LEFT = "left";

    {
        announced = false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getFloat(LEFT);
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            Sample.INSTANCE.play(Assets.SND_MELD);
            if (Dungeon.level != null)
                Dungeon.observe();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
    }

    @Override
    public boolean act() {
        if (target.isAlive()) {

            spend(TICK * 2);

            if (--left <= 0 || Dungeon.hero.visibleEnemies() > 0) {
                detach();
            }

        } else {

            detach();

        }

        return true;
    }

    public void prolong() {
        left = 2;
    }

    @Override
    public int icon() {
        return BuffIndicator.SHADOWS;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.resetColor();
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
