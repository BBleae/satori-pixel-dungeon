package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Regrowth;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.effects.Splash;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.items.wands.WandOfRegrowth;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.plants.Starflower;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RegrowthBomb extends Bomb {

    {
        //TODO visuals
        image = ItemSpriteSheet.REGROWTH_BOMB;
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        if (Dungeon.level.heroFOV[cell]) {
            Splash.at(cell, 0x00FF00, 30);
        }

        ArrayList<Integer> plantCandidates = new ArrayList<>();

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char ch = Actor.findChar(i);
                if (ch != null) {
                    if (ch.alignment == Dungeon.hero.alignment) {
                        //same as a healing potion
                        Buff.affect(ch, Healing.class).setHeal((int) (0.8f * ch.HT + 14), 0.25f, 0);
                        PotionOfHealing.cure(ch);
                    }
                } else if (Dungeon.level.map[i] == Terrain.EMPTY ||
                        Dungeon.level.map[i] == Terrain.EMBERS ||
                        Dungeon.level.map[i] == Terrain.EMPTY_DECO ||
                        Dungeon.level.map[i] == Terrain.GRASS ||
                        Dungeon.level.map[i] == Terrain.HIGH_GRASS ||
                        Dungeon.level.map[i] == Terrain.FURROWED_GRASS) {

                    plantCandidates.add(i);
                }
                GameScene.add(Blob.seed(i, 10, Regrowth.class));
            }
        }

        int plants = Random.chances(new float[]{0, 6, 3, 1});

        for (int i = 0; i < plants; i++) {
            Integer plantPos = Random.element(plantCandidates);
            if (plantPos != null) {
                Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), plantPos);
                plantCandidates.remove(plantPos);
            }
        }

        Integer plantPos = Random.element(plantCandidates);
        if (plantPos != null) {
            Plant.Seed plant;
            switch (Random.chances(new float[]{0, 6, 3, 1})) {
                case 1:
                default:
                    plant = new WandOfRegrowth.Dewcatcher.Seed();
                    break;
                case 2:
                    plant = new WandOfRegrowth.Seedpod.Seed();
                    break;
                case 3:
                    plant = new Starflower.Seed();
                    break;
            }
            Dungeon.level.plant(plant, plantPos);
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 30);
    }
}
