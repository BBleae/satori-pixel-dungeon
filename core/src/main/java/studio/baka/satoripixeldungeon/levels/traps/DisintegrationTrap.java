package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Beam;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.bags.Bag;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DisintegrationTrap extends Trap {

    {
        color = VIOLET;
        shape = CROSSHAIR;

        canBeHidden = false;
    }

    @Override
    public void activate() {
        Char target = Actor.findChar(pos);

        //find the closest char that can be aimed at
        if (target == null) {
            for (Char ch : Actor.chars()) {
                Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                if (bolt.collisionPos == ch.pos &&
                        (target == null || Dungeon.level.trueDistance(pos, ch.pos) < Dungeon.level.trueDistance(pos, target.pos))) {
                    target = ch;
                }
            }
        }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) heap.explode();

        if (target != null) {
            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[target.pos]) {
                Sample.INSTANCE.play(Assets.SND_RAY);
                SatoriPixelDungeon.scene().add(new Beam.DeathRay(DungeonTilemap.tileCenterToWorld(pos), target.sprite.center()));
            }
            target.damage(Random.NormalIntRange(30, 50) + Dungeon.depth, this);
            if (target == Dungeon.hero) {
                Hero hero = (Hero) target;
                if (!hero.isAlive()) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                } else {
                    Item item = hero.belongings.randomUnequipped();
                    Bag bag = hero.belongings.backpack;
                    //bags do not protect against this trap
                    if (item instanceof Bag) {
                        bag = (Bag) item;
                        item = Random.element(bag.items);
                    }
                    if (item == null || item.level() > 0 || item.unique) return;
                    if (!item.stackable) {
                        item.detachAll(bag);
                        GLog.w(Messages.get(this, "one", item.name()));
                    } else {
                        int n = Random.NormalIntRange(1, (item.quantity() + 1) / 2);
                        for (int i = 1; i <= n; i++)
                            item.detach(bag);
                        GLog.w(Messages.get(this, "some", item.name()));
                    }
                }
            }
        }

    }
}
