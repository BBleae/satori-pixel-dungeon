package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class MagicalSight extends FlavourBuff {

    public static final float DURATION = 50f;

    public int distance = 8;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.MIND_VISION;
    }

    @Override
    public void tintIcon(Image icon) {
        greyIcon(icon, 5f, cooldown());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
        GameScene.updateFog();
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

}
