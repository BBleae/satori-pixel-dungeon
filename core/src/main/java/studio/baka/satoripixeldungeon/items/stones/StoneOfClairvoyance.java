package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.CheckedCell;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMagicMapping;
import studio.baka.satoripixeldungeon.mechanics.ShadowCaster;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;

public class StoneOfClairvoyance extends Runestone {

    private static final int DIST = 12;

    {
        image = ItemSpriteSheet.STONE_CLAIRVOYANCE;
    }

    @Override
    protected void activate(final int cell) {
        Point c = Dungeon.level.cellToPoint(cell);

        int[] rounding = ShadowCaster.rounding[DIST];

        int left, right;
        int curr;
        boolean noticed = false;
        for (int y = Math.max(0, c.y - DIST); y <= Math.min(Dungeon.level.height() - 1, c.y + DIST); y++) {
            if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
                left = c.x - rounding[Math.abs(c.y - y)];
            } else {
                left = DIST;
                while (rounding[left] < rounding[Math.abs(c.y - y)]) {
                    left--;
                }
                left = c.x - left;
            }
            right = Math.min(Dungeon.level.width() - 1, c.x + c.x - left);
            left = Math.max(0, left);
            for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++) {

                curUser.sprite.parent.addToBack(new CheckedCell(curr));
                Dungeon.level.mapped[curr] = true;

                if (Dungeon.level.secret[curr]) {
                    Dungeon.level.discover(curr);

                    if (Dungeon.level.heroFOV[curr]) {
                        GameScene.discoverTile(curr, Dungeon.level.map[curr]);
                        ScrollOfMagicMapping.discover(curr);
                        noticed = true;
                    }
                }

            }
        }

        if (noticed) {
            Sample.INSTANCE.play(Assets.SND_SECRET);
        }

        Sample.INSTANCE.play(Assets.SND_TELEPORT);
        GameScene.updateFog();


    }

}
