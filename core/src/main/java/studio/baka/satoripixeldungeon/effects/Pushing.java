package studio.baka.satoripixeldungeon.effects;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class Pushing extends Actor {

    private final CharSprite sprite;
    private final int from;
    private final int to;

    private Effect effect;

    private Callback callback;

    {
        actPriority = VFX_PRIO;
    }

    public Pushing(Char ch, int from, int to) {
        sprite = ch.sprite;
        this.from = from;
        this.to = to;
        this.callback = null;
    }

    public Pushing(Char ch, int from, int to, Callback callback) {
        this(ch, from, to);
        this.callback = callback;
    }

    @Override
    protected boolean act() {
        if (sprite != null) {

            if (effect == null) {
                new Effect();
            }
        }

        Actor.remove(Pushing.this);

        //so that all pushing effects at the same time go simultaneously
        for (Actor actor : Actor.all()) {
            if (actor instanceof Pushing && actor.cooldown() == 0)
                return true;
        }
        return false;

    }

    public class Effect extends Visual {

        private static final float DELAY = 0.15f;

        private final PointF end;

        private float delay;

        public Effect() {
            super(0, 0, 0, 0);

            point(sprite.worldToCamera(from));
            end = sprite.worldToCamera(to);

            speed.set(2 * (end.x - x) / DELAY, 2 * (end.y - y) / DELAY);
            acc.set(-speed.x / DELAY, -speed.y / DELAY);

            delay = 0;

            if (sprite.parent != null)
                sprite.parent.add(this);
        }

        @Override
        public void update() {
            super.update();

            if ((delay += Game.elapsed) < DELAY) {

                sprite.x = x;
                sprite.y = y;

            } else {

                sprite.point(end);

                killAndErase();
                Actor.remove(Pushing.this);
                if (callback != null) callback.call();

                next();
            }
        }
    }

}
