package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.buffs.BlobImmunity;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class PotionOfPurity extends Potion {

    private static final int DISTANCE = 3;

    private static ArrayList<Class> affectedBlobs;

    {
        initials = 9;

        affectedBlobs = new ArrayList<>(new BlobImmunity().immunities());
    }

    @Override
    public void shatter(int cell) {

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), DISTANCE);

        ArrayList<Blob> blobs = new ArrayList<>();
        for (Class c : affectedBlobs) {
            Blob b = Dungeon.level.blobs.get(c);
            if (b != null && b.volume > 0) {
                blobs.add(b);
            }
        }

        for (int i = 0; i < Dungeon.level.length(); i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {

                for (Blob blob : blobs) {

                    int value = blob.cur[i];
                    if (value > 0) {

                        blob.clear(i);
                        blob.cur[i] = 0;
                        blob.volume -= value;

                    }

                }

                if (Dungeon.level.heroFOV[i]) {
                    CellEmitter.get(i).burst(Speck.factory(Speck.DISCOVER), 2);
                }

            }
        }


        if (Dungeon.level.heroFOV[cell]) {
            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);

            setKnown();
            GLog.i(Messages.get(this, "freshness"));
        }

    }

    @Override
    public void apply(Hero hero) {
        GLog.w(Messages.get(this, "protected"));
        Buff.prolong(hero, BlobImmunity.class, BlobImmunity.DURATION);
        setKnown();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
