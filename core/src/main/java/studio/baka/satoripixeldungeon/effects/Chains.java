package studio.baka.satoripixeldungeon.effects;

import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class Chains extends Group {

    private static final double A = 180 / Math.PI;

    private float spent = 0f;
    private final float duration;

    private final Callback callback;

    private final Image[] chains;

    private final PointF from;
    private final PointF to;

    public Chains(int from, int to, Callback callback) {
        this(DungeonTilemap.tileCenterToWorld(from),
                DungeonTilemap.tileCenterToWorld(to),
                callback);
    }

    public Chains(PointF from, PointF to, Callback callback) {
        super();

        this.callback = callback;

        this.from = from;
        this.to = to;

        float dx = to.x - from.x;
        float dy = to.y - from.y;
        float distance = (float) Math.hypot(dx, dy);


        duration = distance / 300f + 0.1f;

        float rotation = (float) (Math.atan2(dy, dx) * A) + 90f;

        int numChains = Math.round(distance / 6f) + 1;

        chains = new Image[numChains];
        for (int i = 0; i < chains.length; i++) {
            chains[i] = new Image(Effects.get(Effects.Type.CHAIN));
            chains[i].angle = rotation;
            chains[i].origin.set(chains[i].width() / 2, chains[i].height());
            add(chains[i]);
        }
    }

    @Override
    public void update() {
        if ((spent += Game.elapsed) > duration) {

            killAndErase();
            if (callback != null) {
                callback.call();
            }

        } else {
            float dx = to.x - from.x;
            float dy = to.y - from.y;
            for (int i = 0; i < chains.length; i++) {
                chains[i].center(new PointF(
                        from.x + ((dx * (i / (float) chains.length)) * (spent / duration)),
                        from.y + ((dy * (i / (float) chains.length)) * (spent / duration))
                ));
            }
        }
    }

}
