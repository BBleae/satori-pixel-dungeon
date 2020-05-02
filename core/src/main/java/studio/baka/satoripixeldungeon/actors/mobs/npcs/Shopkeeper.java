package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ShopkeeperSprite;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Game;

public class Shopkeeper extends NPC {

    {
        spriteClass = ShopkeeperSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    protected boolean act() {

        throwItem();

        sprite.turnTo(pos, Dungeon.hero.pos);
        spend(TICK);
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        flee();
    }

    @Override
    public void add(Buff buff) {
        flee();
    }

    public void flee() {
        destroy();

        sprite.killAndErase();
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
    }

    @Override
    public void destroy() {
        super.destroy();
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type == Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
                heap.destroy();
            }
        }
    }

    @Override
    public boolean reset() {
        return true;
    }

    public static WndBag sell() {
        return GameScene.selectItem(itemSelector, WndBag.Mode.FOR_SALE, Messages.get(Shopkeeper.class, "sell"));
    }

    private static final WndBag.Listener itemSelector = item -> {
        if (item != null) {
            WndBag parentWnd = sell();
            GameScene.show(new WndTradeItem(item, parentWnd));
        }
    };

    @Override
    public boolean interact() {
        Game.runOnRenderThread(() -> sell());
        return false;
    }
}
