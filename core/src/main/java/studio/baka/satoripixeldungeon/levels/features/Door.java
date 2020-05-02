package studio.baka.satoripixeldungeon.levels.features;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class Door {

    public static void enter(int pos) {
        Level.set(pos, Terrain.OPEN_DOOR);
        GameScene.updateMap(pos);

        if (Dungeon.level.heroFOV[pos]) {
            Dungeon.observe();
            Sample.INSTANCE.play(Assets.SND_OPEN);
        }
    }

    public static void leave(int pos) {
        int chars = 0;

        for (Char ch : Actor.chars()) {
            if (ch.pos == pos) chars++;
        }

        //door does not shut if anything else is also on it
        if (Dungeon.level.heaps.get(pos) == null && chars <= 1) {
            Level.set(pos, Terrain.DOOR);
            GameScene.updateMap(pos);
            if (Dungeon.level.heroFOV[pos])
                Dungeon.observe();
        }
    }
}
