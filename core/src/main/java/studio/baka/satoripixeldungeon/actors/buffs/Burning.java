package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Thief;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.food.ChargrilledMeat;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfUpgrade;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Burning extends Buff implements Hero.Doom {

    private static final float DURATION = 8f;

    private float left;

    //for tracking burning of hero items
    private int burnIncrement = 0;

    private static final String LEFT = "left";
    private static final String BURN = "burnIncrement";

    {
        type = buffType.NEGATIVE;
        announced = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, left);
        bundle.put(BURN, burnIncrement);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        left = bundle.getFloat(LEFT);
        burnIncrement = bundle.getInt(BURN);
    }

    @Override
    public boolean act() {

        if (target.isAlive() && !target.isImmune(getClass())) {

            int damage = Random.NormalIntRange(1, 3 + Dungeon.depth / 4);
            Buff.detach(target, Chill.class);

            if (target instanceof Hero) {

                Hero hero = (Hero) target;

                hero.damage(damage, this);
                burnIncrement++;

                //at 4+ turns, there is a (turns-3)/3 chance an item burns
                if (Random.Int(3) < (burnIncrement - 3)) {
                    burnIncrement = 0;

                    ArrayList<Item> burnable = new ArrayList<>();
                    //does not reach inside of containers
                    for (Item i : hero.belongings.backpack.items) {
                        if ((i instanceof Scroll && !(i instanceof ScrollOfUpgrade))
                                || i instanceof MysteryMeat) {
                            burnable.add(i);
                        }
                    }

                    if (!burnable.isEmpty()) {
                        Item toBurn = Random.element(burnable).detach(hero.belongings.backpack);
                        GLog.w(Messages.get(this, "burnsup", Messages.capitalize(toBurn.toString())));
                        if (toBurn instanceof MysteryMeat) {
                            ChargrilledMeat steak = new ChargrilledMeat();
                            if (!steak.collect(hero.belongings.backpack)) {
                                Dungeon.level.drop(steak, hero.pos).sprite.drop();
                            }
                        }
                        Heap.burnFX(hero.pos);
                    }
                }

            } else {
                target.damage(damage, this);
            }

            if (target instanceof Thief) {

                Item item = ((Thief) target).item;

                if (item instanceof Scroll &&
                        !(item instanceof ScrollOfUpgrade)) {
                    target.sprite.emitter().burst(ElmoParticle.FACTORY, 6);
                    ((Thief) target).item = null;
                } else if (item instanceof MysteryMeat) {
                    target.sprite.emitter().burst(ElmoParticle.FACTORY, 6);
                    ((Thief) target).item = new ChargrilledMeat();
                }

            }

        } else {

            detach();
        }

        if (Dungeon.level.flamable[target.pos] && Blob.volumeAt(target.pos, Fire.class) == 0) {
            GameScene.add(Blob.seed(target.pos, 4, Fire.class));
        }

        spend(TICK);
        left -= TICK;

        if (left <= 0 ||
                (Dungeon.level.water[target.pos] && !target.flying)) {

            detach();
        }

        return true;
    }

    public void reignite(Char ch) {
        reignite(ch, DURATION);
    }

    public void reignite(Char ch, float duration) {
        left = duration;
    }

    @Override
    public int icon() {
        return BuffIndicator.FIRE;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.BURNING);
        else target.sprite.remove(CharSprite.State.BURNING);
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(left));
    }

    @Override
    public void onDeath() {

        Badges.validateDeathFromFire();

        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}
