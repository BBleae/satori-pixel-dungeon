package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Sheep;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class StoneOfFlock extends Runestone {

    {
        image = ItemSpriteSheet.STONE_FLOCK;
    }

    @Override
    protected void activate(int cell) {

        for (int i : PathFinder.NEIGHBOURS9) {

            if (!Dungeon.level.solid[cell + i]
                    && !Dungeon.level.pit[cell + i]
                    && Actor.findChar(cell + i) == null) {

                Sheep sheep = new Sheep();
                sheep.lifespan = Random.IntRange(5, 8);
                sheep.pos = cell + i;
                GameScene.add(sheep);
                Dungeon.level.occupyCell(sheep);

                CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
            }
        }
        CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 4);
        Sample.INSTANCE.play(Assets.SND_PUFF);

    }

}
