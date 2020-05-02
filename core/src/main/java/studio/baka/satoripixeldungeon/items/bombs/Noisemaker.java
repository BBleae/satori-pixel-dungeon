package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.mobs.Mimic;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Noisemaker extends Bomb {

    {
        image = ItemSpriteSheet.NOISEMAKER;
    }

    public void setTrigger(int cell) {

        Buff.affect(Dungeon.hero, Trigger.class).set(cell);

        CellEmitter.center(cell).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        Sample.INSTANCE.play(Assets.SND_ALERT);

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            mob.beckon(cell);
        }

        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type == Heap.Type.MIMIC) {
                Mimic m = Mimic.spawnAt(heap.pos, heap.items);
                if (m != null) {
                    m.beckon(cell);
                    heap.destroy();
                }
            }
        }

    }

    public static class Trigger extends Buff {

        int cell;
        int floor;
        int left;

        public void set(int cell) {
            floor = Dungeon.depth;
            this.cell = cell;
            left = 6;
        }

        @Override
        public boolean act() {

            if (Dungeon.depth != floor) {
                spend(TICK);
                return true;
            }

            Noisemaker bomb = null;
            Heap heap = Dungeon.level.heaps.get(cell);

            if (heap != null) {
                for (Item i : heap.items) {
                    if (i instanceof Noisemaker) {
                        bomb = (Noisemaker) i;
                        break;
                    }
                }
            }

            if (bomb == null) {
                detach();

            } else if (Actor.findChar(cell) != null) {

                heap.items.remove(bomb);
                if (heap.items.isEmpty()) {
                    heap.destroy();
                }

                detach();
                bomb.explode(cell);

            } else {
                spend(TICK);

                left--;

                if (left <= 0) {
                    CellEmitter.center(cell).start(Speck.factory(Speck.SCREAM), 0.3f, 3);
                    Sample.INSTANCE.play(Assets.SND_ALERT);

                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                        mob.beckon(cell);
                    }
                    left = 6;
                }

            }

            return true;
        }

        private static final String CELL = "cell";
        private static final String FLOOR = "floor";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CELL, cell);
            bundle.put(FLOOR, floor);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            cell = bundle.getInt(CELL);
            floor = bundle.getInt(FLOOR);
            left = bundle.getInt(LEFT);
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 40);
    }
}
