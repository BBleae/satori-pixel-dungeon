package studio.baka.satoripixeldungeon.levels.features;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.LeafParticle;
import studio.baka.satoripixeldungeon.items.Dewdrop;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.armor.glyphs.Camouflage;
import studio.baka.satoripixeldungeon.items.artifacts.SandalsOfNature;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class HighGrass {

    //prevents items dropped from grass, from trampling that same grass.
    //yes this is a bit ugly, oh well.
    private static boolean freezeTrample = false;

    public static void trample(Level level, int pos) {

        if (freezeTrample) return;

        Char ch = Actor.findChar(pos);

        if (level.map[pos] == Terrain.FURROWED_GRASS) {
            if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.HUNTRESS) {
                //Do nothing
                freezeTrample = true;
            } else {
                Level.set(pos, Terrain.GRASS);
            }

        } else {
            if (ch instanceof Hero && ((Hero) ch).heroClass == HeroClass.HUNTRESS) {
                Level.set(pos, Terrain.FURROWED_GRASS);
                freezeTrample = true;
            } else {
                Level.set(pos, Terrain.GRASS);
            }

            int naturalismLevel = 0;

            if (ch != null) {
                SandalsOfNature.Naturalism naturalism = ch.buff(SandalsOfNature.Naturalism.class);
                if (naturalism != null) {
                    if (!naturalism.isCursed()) {
                        naturalismLevel = naturalism.itemLevel() + 1;
                        naturalism.charge();
                    } else {
                        naturalismLevel = -1;
                    }
                }
            }

            if (naturalismLevel >= 0) {
                // Seed, scales from 1/20 to 1/4
                if (Random.Int(20 - (naturalismLevel * 4)) == 0) {
                    level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop();
                }

                // Dew, scales from 1/6 to 1/3
                if (Random.Int(24 - naturalismLevel * 3) <= 3) {
                    level.drop(new Dewdrop(), pos).sprite.drop();
                }
            }

            if (ch instanceof Hero) {
                Hero hero = (Hero) ch;

                //Camouflage
                //FIXME doesn't work with sad ghost
                if (hero.belongings.armor != null && hero.belongings.armor.hasGlyph(Camouflage.class, hero)) {
                    Buff.affect(hero, Camouflage.Camo.class).set(3 + hero.belongings.armor.level());
                }
            }

        }

        freezeTrample = false;

        if (SatoriPixelDungeon.scene() instanceof GameScene) {
            GameScene.updateMap(pos);

            CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            if (Dungeon.level.heroFOV[pos]) Dungeon.observe();
        }
    }
}
