package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.GooWarn;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ArcaneBomb extends Bomb {

    {
        image = ItemSpriteSheet.ARCANE_BOMB;
    }

    @Override
    protected void onThrow(int cell) {
        super.onThrow(cell);
        if (fuse != null) {
            PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE)
                    GameScene.add(Blob.seed(i, 3, GooWarn.class));
            }
        }
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        ArrayList<Char> affected = new ArrayList<>();

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                if (Dungeon.level.heroFOV[i]) {
                    CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10);
                }
                Char ch = Actor.findChar(i);
                if (ch != null) {
                    affected.add(ch);
                }
            }
        }

        for (Char ch : affected) {
            // 100%/83%/67% bomb damage based on distance, but pierces armor.
            int damage = Math.round(Random.NormalIntRange(Dungeon.depth + 5, 10 + Dungeon.depth * 2));
            float multiplier = 1f - (.16667f * Dungeon.level.distance(cell, ch.pos));
            ch.damage(Math.round(damage * multiplier), this);
            if (ch == Dungeon.hero && !ch.isAlive()) {
                Dungeon.fail(Bomb.class);
            }
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 50);
    }
}
