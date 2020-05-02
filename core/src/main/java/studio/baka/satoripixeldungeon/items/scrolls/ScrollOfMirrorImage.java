package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.MirrorImage;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfMirrorImage extends Scroll {

    {
        initials = 3;
    }

    private static final int NIMAGES = 2;

    @Override
    public void doRead() {
        int spawnedImages = spawnImages(curUser, NIMAGES);

        if (spawnedImages > 0) {
            setKnown();
        }

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        readAnimation();
    }

    @Override
    public void empoweredRead() {
        //spawns 2 images right away, delays 3 of them, 5 total.
        new DelayedImageSpawner(5 - spawnImages(curUser, 2), 1, 2).attachTo(curUser);

        setKnown();

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        readAnimation();
    }

    //returns the number of images spawned
    public static int spawnImages(Hero hero, int nImages) {

        ArrayList<Integer> respawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                respawnPoints.add(p);
            }
        }

        int spawned = 0;
        while (nImages > 0 && respawnPoints.size() > 0) {
            int index = Random.index(respawnPoints);

            MirrorImage mob = new MirrorImage();
            mob.duplicate(hero);
            GameScene.add(mob);
            ScrollOfTeleportation.appear(mob, respawnPoints.get(index));

            respawnPoints.remove(index);
            nImages--;
            spawned++;
        }

        return spawned;
    }

    public static class DelayedImageSpawner extends Buff {

        public DelayedImageSpawner() {
            this(NIMAGES, NIMAGES, 1);
        }

        public DelayedImageSpawner(int total, int perRound, float delay) {
            super();
            totImages = total;
            imPerRound = perRound;
            this.delay = delay;
        }

        private int totImages;
        private int imPerRound;
        private float delay;

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                spend(delay);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean act() {

            int spawned = spawnImages((Hero) target, Math.min(totImages, imPerRound));

            totImages -= spawned;

            if (totImages < 0) {
                detach();
            } else {
                spend(delay);
            }

            return true;
        }

        private static final String TOTAL = "images";
        private static final String PER_ROUND = "per_round";
        private static final String DELAY = "delay";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TOTAL, totImages);
            bundle.put(PER_ROUND, imPerRound);
            bundle.put(DELAY, delay);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            totImages = bundle.getInt(TOTAL);
            imPerRound = bundle.getInt(PER_ROUND);
            delay = bundle.getFloat(DELAY);
        }
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
