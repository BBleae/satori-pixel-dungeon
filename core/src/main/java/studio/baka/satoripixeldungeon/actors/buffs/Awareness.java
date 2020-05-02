package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class Awareness extends FlavourBuff {

    {
        type = buffType.POSITIVE;
    }

    public static final float DURATION = 2f;

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
        GameScene.updateFog();
    }
}
