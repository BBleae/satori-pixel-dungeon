package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Sheep;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WoollyBomb extends Bomb {

    {
        image = ItemSpriteSheet.WOOLY_BOMB;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.insideMap(i)
                        && Actor.findChar(i) == null
                        && !(Dungeon.level.pit[i])) {
                    Sheep sheep = new Sheep();
                    sheep.lifespan = Random.NormalIntRange(8, 16);
                    sheep.pos = i;
                    Dungeon.level.occupyCell(sheep);
                    GameScene.add(sheep);
                    CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4);
                }
            }
        }

        Sample.INSTANCE.play(Assets.SND_PUFF);


    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 30);
    }
}
