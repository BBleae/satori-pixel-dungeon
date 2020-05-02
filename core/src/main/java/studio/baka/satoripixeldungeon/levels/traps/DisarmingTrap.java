package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.KindOfWeapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class DisarmingTrap extends Trap {

    {
        color = RED;
        shape = LARGE_DOT;
    }

    @Override
    public void activate() {
        Heap heap = Dungeon.level.heaps.get(pos);

        if (heap != null) {
            int cell = Dungeon.level.randomRespawnCell();

            if (cell != -1) {
                Item item = heap.pickUp();
                Dungeon.level.drop(item, cell).seen = true;
                for (int i : PathFinder.NEIGHBOURS9)
                    Dungeon.level.visited[cell + i] = true;
                GameScene.updateFog();

                Sample.INSTANCE.play(Assets.SND_TELEPORT);
                CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
            }
        }

        if (Dungeon.hero.pos == pos && !Dungeon.hero.flying) {
            Hero hero = Dungeon.hero;
            KindOfWeapon weapon = hero.belongings.weapon;

            if (weapon != null && !weapon.cursed) {

                int cell = Dungeon.level.randomRespawnCell();
                if (cell != -1) {
                    hero.belongings.weapon = null;
                    Dungeon.quickslot.clearItem(weapon);
                    Item.updateQuickslot();

                    Dungeon.level.drop(weapon, cell).seen = true;
                    for (int i : PathFinder.NEIGHBOURS9)
                        Dungeon.level.visited[cell + i] = true;
                    GameScene.updateFog();

                    GLog.w(Messages.get(this, "disarm"));

                    Sample.INSTANCE.play(Assets.SND_TELEPORT);
                    CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);

                }

            }
        }
    }
}
