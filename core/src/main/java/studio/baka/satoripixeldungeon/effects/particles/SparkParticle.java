package studio.baka.satoripixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class SparkParticle extends PixelParticle {

    public static final Emitter.Factory FACTORY = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SparkParticle) emitter.recycle(SparkParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public static final Emitter.Factory STATIC = new Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SparkParticle) emitter.recycle(SparkParticle.class)).resetStatic(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public SparkParticle() {
        super();

        size(2);

        acc.set(0, +50);
    }

    public void reset(float x, float y) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan = Random.Float(0.5f, 1.0f);

        speed.polar(-Random.Float(3.1415926f), Random.Float(20, 40));
    }

    public void resetStatic(float x, float y) {
        reset(x, y);

        left = lifespan = Random.Float(0.25f, 0.5f);

        acc.set(0, 0);
        speed.set(0, 0);
    }

    @Override
    public void update() {
        super.update();
        size(Random.Float(5 * left / lifespan));
    }
}