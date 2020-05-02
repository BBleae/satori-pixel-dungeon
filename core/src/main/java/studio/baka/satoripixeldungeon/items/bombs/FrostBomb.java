package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Freezing;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

public class FrostBomb extends Bomb {

    {
        image = ItemSpriteSheet.FROST_BOMB;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                GameScene.add(Blob.seed(i, 10, Freezing.class));
                Char ch = Actor.findChar(i);
                if (ch != null) {
                    Buff.affect(ch, Frost.class, 2f);
                }
            }
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 30);
    }
}
