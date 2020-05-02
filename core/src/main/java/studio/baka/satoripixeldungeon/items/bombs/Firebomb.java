package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Firebomb extends Bomb {

    {
        image = ItemSpriteSheet.FIRE_BOMB;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.pit[i])
                    GameScene.add(Blob.seed(i, 2, Fire.class));
                else
                    GameScene.add(Blob.seed(i, 10, Fire.class));
                CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
            }
        }
        Sample.INSTANCE.play(Assets.SND_BURNING);
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 30);
    }
}
