package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Shadows;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ShaftParticle;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;

public class Foliage extends Blob {

    @Override
    protected void evolve() {

        int[] map = Dungeon.level.map;

        boolean visible = false;

        int cell;
        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (cur[cell] > 0) {

                    off[cell] = cur[cell];
                    volume += off[cell];

                    if (map[cell] == Terrain.EMBERS) {
                        map[cell] = Terrain.GRASS;
                        GameScene.updateMap(cell);
                    }

                    visible = visible || Dungeon.level.heroFOV[cell];

                } else {
                    off[cell] = 0;
                }
            }
        }

        Hero hero = Dungeon.hero;
        if (hero.isAlive() && hero.visibleEnemies() == 0 && cur[hero.pos] > 0) {
            Buff.affect(hero, Shadows.class).prolong();
        }

        if (visible) {
            Notes.add(Notes.Landmark.GARDEN);
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(ShaftParticle.FACTORY, 0.9f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
