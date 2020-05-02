package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Belongings;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.artifacts.LloydsBeacon;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import com.watabou.noosa.Game;

public class DistortionTrap extends Trap {

    {
        color = TEAL;
        shape = LARGE_DOT;
    }

    @Override
    public void activate() {
        InterlevelScene.returnDepth = Dungeon.depth;
        Belongings belongings = Dungeon.hero.belongings;

        for (Notes.Record rec : Notes.getRecords()) {
            if (rec.depth() == Dungeon.depth) {
                Notes.remove(rec);
            }
        }

        for (Item i : belongings) {
            if (i instanceof LloydsBeacon && ((LloydsBeacon) i).returnDepth == Dungeon.depth)
                ((LloydsBeacon) i).returnDepth = -1;
        }

        InterlevelScene.mode = InterlevelScene.Mode.RESET;
        Game.switchScene(InterlevelScene.class);
    }
}
