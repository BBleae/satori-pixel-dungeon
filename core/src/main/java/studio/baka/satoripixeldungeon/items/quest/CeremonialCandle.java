package studio.baka.satoripixeldungeon.items.quest;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.NewbornElemental;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;


public class CeremonialCandle extends Item {

    //generated with the wandmaker quest
    public static int ritualPos;

    {
        image = ItemSpriteSheet.CANDLE;

        defaultAction = AC_THROW;

        unique = true;
        stackable = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public void doDrop(Hero hero) {
        super.doDrop(hero);
        checkCandles();
    }

    @Override
    protected void onThrow(int cell) {
        super.onThrow(cell);
        checkCandles();
    }

    private static void checkCandles() {
        Heap heapTop = Dungeon.level.heaps.get(ritualPos - Dungeon.level.width());
        Heap heapRight = Dungeon.level.heaps.get(ritualPos + 1);
        Heap heapBottom = Dungeon.level.heaps.get(ritualPos + Dungeon.level.width());
        Heap heapLeft = Dungeon.level.heaps.get(ritualPos - 1);

        if (heapTop != null &&
                heapRight != null &&
                heapBottom != null &&
                heapLeft != null) {

            if (heapTop.peek() instanceof CeremonialCandle &&
                    heapRight.peek() instanceof CeremonialCandle &&
                    heapBottom.peek() instanceof CeremonialCandle &&
                    heapLeft.peek() instanceof CeremonialCandle) {

                heapTop.pickUp();
                heapRight.pickUp();
                heapBottom.pickUp();
                heapLeft.pickUp();

                NewbornElemental elemental = new NewbornElemental();
                Char ch = Actor.findChar(ritualPos);
                if (ch != null) {
                    ArrayList<Integer> candidates = new ArrayList<>();
                    for (int n : PathFinder.NEIGHBOURS8) {
                        int cell = ritualPos + n;
                        if ((Dungeon.level.passable[cell] || Dungeon.level.avoid[cell]) && Actor.findChar(cell) == null) {
                            candidates.add(cell);
                        }
                    }
                    if (candidates.size() > 0) {
                        elemental.pos = Random.element(candidates);
                    } else {
                        elemental.pos = ritualPos;
                    }
                } else {
                    elemental.pos = ritualPos;
                }
                elemental.state = elemental.HUNTING;
                GameScene.add(elemental, 1);

                for (int i : PathFinder.NEIGHBOURS9) {
                    CellEmitter.get(ritualPos + i).burst(ElmoParticle.FACTORY, 10);
                }
                Sample.INSTANCE.play(Assets.SND_BURNING);
            }
        }

    }
}
