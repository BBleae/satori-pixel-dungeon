package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;

public class WeakeningTrap extends Trap {

    {
        color = GREEN;
        shape = WAVES;
    }

    @Override
    public void activate() {
        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
        }

        Char ch = Actor.findChar(pos);
        if (ch != null) {
            if (ch.properties().contains(Char.Property.BOSS)
                    || ch.properties().contains(Char.Property.MINIBOSS)) {
                Buff.prolong(ch, Weakness.class, Weakness.DURATION / 2f);
            }
            Buff.prolong(ch, Weakness.class, Weakness.DURATION * 2f);
        }
    }
}
