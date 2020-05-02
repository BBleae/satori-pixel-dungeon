package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ImpSprite;

public class ImpShopkeeper extends Shopkeeper {

    {
        spriteClass = ImpSprite.class;
    }

    private boolean seenBefore = false;

    @Override
    protected boolean act() {

        if (!seenBefore && Dungeon.level.heroFOV[pos]) {
            yell(Messages.get(this, "greetings", Dungeon.hero.givenName()));
            seenBefore = true;
        }

        return super.act();
    }

    @Override
    public void flee() {
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type == Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
                heap.destroy();
            }
        }

        destroy();

        sprite.emitter().burst(Speck.factory(Speck.WOOL), 15);
        sprite.killAndErase();
    }
}
