package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Roots;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.particles.LeafParticle;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class Regrowth extends Blob {

    @Override
    protected void evolve() {
        super.evolve();

        if (volume > 0) {
            int cell;
            for (int i = area.left; i < area.right; i++) {
                for (int j = area.top; j < area.bottom; j++) {
                    cell = i + j * Dungeon.level.width();
                    if (off[cell] > 0) {
                        int c = Dungeon.level.map[cell];
                        int c1 = c;
                        if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
                            c1 = (cur[cell] > 9 && Actor.findChar(cell) == null)
                                    ? Terrain.HIGH_GRASS : Terrain.GRASS;
                        } else if ((c == Terrain.GRASS || c == Terrain.FURROWED_GRASS)
                                && cur[cell] > 9 && Dungeon.level.plants.get(cell) == null && Actor.findChar(cell) == null) {
                            c1 = Terrain.HIGH_GRASS;
                        }

                        if (c1 != c) {
                            Level.set(cell, c1);
                            GameScene.updateMap(cell);
                        }

                        Char ch = Actor.findChar(cell);
                        if (ch != null
                                && !ch.isImmune(this.getClass())
                                && ch != Dungeon.hero
                                && off[cell] > 1) {
                            Buff.prolong(ch, Roots.class, TICK);
                        } else if (ch == Dungeon.hero && Dungeon.hero.heroClass != HeroClass.HUNTRESS) {
                            Buff.prolong(ch, Roots.class, TICK);
                        }
                    }
                }
            }
            Dungeon.observe();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);

        emitter.start(LeafParticle.LEVEL_SPECIFIC, 0.2f, 0);
    }
}
