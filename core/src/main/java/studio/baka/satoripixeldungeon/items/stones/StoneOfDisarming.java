package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.levels.traps.Trap;
import studio.baka.satoripixeldungeon.mechanics.ShadowCaster;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StoneOfDisarming extends Runestone {

    private static final int DIST = 8;

    {
        image = ItemSpriteSheet.STONE_DISARM;
    }

    @Override
    protected void activate(final int cell) {
        boolean[] FOV = new boolean[Dungeon.level.length()];
        Point c = Dungeon.level.cellToPoint(cell);
        ShadowCaster.castShadow(c.x, c.y, FOV, Dungeon.level.losBlocking, DIST);

        int sX = Math.max(0, c.x - DIST);
        int eX = Math.min(Dungeon.level.width() - 1, c.x + DIST);

        int sY = Math.max(0, c.y - DIST);
        int eY = Math.min(Dungeon.level.height() - 1, c.y + DIST);

        ArrayList<Trap> disarmCandidates = new ArrayList<>();

        for (int y = sY; y <= eY; y++) {
            int curr = y * Dungeon.level.width() + sX;
            for (int x = sX; x <= eX; x++) {

                if (FOV[curr]) {

                    Trap t = Dungeon.level.traps.get(curr);
                    if (t != null && t.active) {
                        disarmCandidates.add(t);
                    }

                }
                curr++;
            }
        }

        disarmCandidates.sort((o1, o2) -> {
            float diff = Dungeon.level.trueDistance(cell, o1.pos) - Dungeon.level.trueDistance(cell, o2.pos);
            if (diff < 0) {
                return -1;
            } else if (diff == 0) {
                return Random.Int(2) == 0 ? -1 : 1;
            } else {
                return 1;
            }
        });

        //disarms at most nine traps
        while (disarmCandidates.size() > 9) {
            disarmCandidates.remove(9);
        }

        for (Trap t : disarmCandidates) {
            t.reveal();
            t.disarm();
            CellEmitter.get(t.pos).burst(Speck.factory(Speck.STEAM), 6);
        }

        Sample.INSTANCE.play(Assets.SND_TELEPORT);
    }
}
