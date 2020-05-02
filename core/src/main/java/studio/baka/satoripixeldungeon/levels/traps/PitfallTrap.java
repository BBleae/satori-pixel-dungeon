package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.levels.features.Chasm;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.GLog;

public class PitfallTrap extends Trap {

    {
        color = RED;
        shape = DIAMOND;
    }

    @Override
    public void activate() {

        if (Dungeon.bossLevel() || Dungeon.depth > 25) {
            GLog.w(Messages.get(this, "no_pit"));
            return;
        }

        Heap heap = Dungeon.level.heaps.get(pos);

        if (heap != null) {
            for (Item item : heap.items) {
                Dungeon.dropToChasm(item);
            }
            heap.sprite.kill();
            GameScene.discard(heap);
            Dungeon.level.heaps.remove(pos);
        }

        Char ch = Actor.findChar(pos);

        if (ch != null && !ch.flying) {
            if (ch == Dungeon.hero) {
                Chasm.heroFall(pos);
            } else {
                Chasm.mobFall((Mob) ch);
            }
        }
    }

    //TODO these used to become chasms when disarmed, but the functionality was problematic
    //because it could block routes, perhaps some way to make this work elegantly?
}
