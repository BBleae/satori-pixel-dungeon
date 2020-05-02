package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class WellWater extends Blob {

    @Override
    protected void evolve() {
        int cell;
        boolean seen = false;
        for (int i = area.top - 1; i <= area.bottom; i++) {
            for (int j = area.left - 1; j <= area.right; j++) {
                cell = j + i * Dungeon.level.width();
                if (Dungeon.level.insideMap(cell)) {
                    off[cell] = cur[cell];
                    volume += off[cell];
                    if (off[cell] > 0 && Dungeon.level.visited[cell]) {
                        seen = true;
                    }
                }
            }
        }
        if (seen) {
            Notes.add(record());
        } else {
            Notes.remove(record());
        }
    }

    protected boolean affect(int pos) {

        Heap heap;

        if (pos == Dungeon.hero.pos && affectHero(Dungeon.hero)) {

            cur[pos] = 0;
            return true;

        } else if ((heap = Dungeon.level.heaps.get(pos)) != null) {

            Item oldItem = heap.peek();
            Item newItem = affectItem(oldItem, pos);

            if (newItem != null) {

                if (newItem == oldItem) {

                } else if (oldItem.quantity() > 1) {

                    oldItem.quantity(oldItem.quantity() - 1);
                    heap.drop(newItem);

                } else {
                    heap.replace(oldItem, newItem);
                }

                heap.sprite.link();
                cur[pos] = 0;

                return true;

            } else {

                int newPlace;
                do {
                    newPlace = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                } while (!Dungeon.level.passable[newPlace] && !Dungeon.level.avoid[newPlace]);
                Dungeon.level.drop(heap.pickUp(), newPlace).sprite.drop(pos);

                return false;

            }

        } else {

            return false;

        }
    }

    protected abstract boolean affectHero(Hero hero);

    protected abstract Item affectItem(Item item, int pos);

    protected abstract Notes.Landmark record();

    public static void affectCell(int cell) {

        Class<?>[] waters = {WaterOfHealth.class, WaterOfAwareness.class, WaterOfTransmutation.class};

        for (Class<?> waterClass : waters) {
            WellWater water = (WellWater) Dungeon.level.blobs.get(waterClass);
            if (water != null &&
                    water.volume > 0 &&
                    water.cur[cell] > 0 &&
                    water.affect(cell)) {

                Level.set(cell, Terrain.EMPTY_WELL);
                GameScene.updateMap(cell);

                return;
            }
        }
    }
}
