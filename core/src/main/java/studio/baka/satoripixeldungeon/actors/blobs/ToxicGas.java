package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;

public class ToxicGas extends Blob implements Hero.Doom {

    @Override
    protected void evolve() {
        super.evolve();

        int damage = 1 + Dungeon.depth / 5;

        Char ch;
        int cell;

        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (cur[cell] > 0 && (ch = Actor.findChar(cell)) != null) {
                    if (!ch.isImmune(this.getClass())) {

                        ch.damage(damage, this);
                    }
                }
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);

        emitter.pour(Speck.factory(Speck.TOXIC), 0.4f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void onDeath() {

        Badges.validateDeathFromGas();

        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}
