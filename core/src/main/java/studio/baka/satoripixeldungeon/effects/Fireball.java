package studio.baka.satoripixeldungeon.effects;

import studio.baka.satoripixeldungeon.Assets;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

public class Fireball extends Component {

    private static final RectF BLIGHT = new RectF(0, 0, 0.25f, 1);
    private static final RectF FLIGHT = new RectF(0.25f, 0, 0.5f, 1);
    private static final RectF FLAME1 = new RectF(0.50f, 0, 0.75f, 1);
    private static final RectF FLAME2 = new RectF(0.75f, 0, 1.00f, 1);

    private static final int COLOR = 0xFF66FF;

    private Image bLight;
    private Image fLight;
    private Emitter emitter;
    private Group sparks;

    @Override
    protected void createChildren() {

        sparks = new Group();
        add(sparks);

        bLight = new Image(Assets.FIREBALL);
        bLight.frame(BLIGHT);
        bLight.origin.set(bLight.width / 2);
        bLight.angularSpeed = -90;
        add(bLight);

        emitter = new Emitter();
        emitter.pour(new Emitter.Factory() {
            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                Flame p = (Flame) emitter.recycle(Flame.class);
                p.reset();
                p.heightLimit(Fireball.this.y - 30);
                p.x = x - p.width / 2;
                p.y = y - p.height / 2;
            }
        }, 0.1f);
        add(emitter);

        fLight = new Image(Assets.FIREBALL);
        fLight.frame(FLIGHT);
        fLight.origin.set(fLight.width / 2);
        fLight.angularSpeed = 360;
        add(fLight);

        bLight.texture.filter(Texture.LINEAR, Texture.LINEAR);
    }

    @Override
    protected void layout() {

        bLight.x = x - bLight.width / 2;
        bLight.y = y - bLight.height / 2;

        emitter.pos(
                x - bLight.width / 4,
                y - bLight.height / 4,
                bLight.width / 2,
                bLight.height / 2);

        fLight.x = x - fLight.width / 2;
        fLight.y = y - fLight.height / 2;
    }

    @Override
    public void update() {

        super.update();

        if (Random.Float() < Game.elapsed) {
            PixelParticle spark = (PixelParticle) sparks.recycle(PixelParticle.Shrinking.class);
            spark.reset(x, y, ColorMath.random(COLOR, 0x66FF66), 2, Random.Float(0.5f, 1.0f));
            spark.speed.set(
                    Random.Float(-40, +40),
                    Random.Float(-60, +20));
            spark.acc.set(0, +80);
            sparks.add(spark);
        }
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public static class Flame extends Image {

        private static final float LIFESPAN = 1f;

        private float timeLeft;
        private float heightLimit;

        public Flame() {

            super(Assets.FIREBALL);

            frame(Random.Int(2) == 0 ? FLAME1 : FLAME2);
            origin.set(width / 2, height / 2);
            float ACC = -20f;
            acc.set(0, ACC);
        }

        public void reset() {
            revive();
            timeLeft = LIFESPAN;
            float SPEED = -40f;
            speed.set(0, SPEED);
        }

        public void heightLimit(float limit) {
            heightLimit = limit;
        }

        @Override
        public void update() {

            super.update();

            if (y < heightLimit) {
                y = heightLimit;
                speed.set(Random.Float(-20, 20), 0);
                acc.set(0, 0);
            }

            if ((timeLeft -= Game.elapsed) <= 0) {

                kill();

            } else {

                float p = timeLeft / LIFESPAN;
                scale.set(p);
                alpha(p > 0.8f ? (1 - p) * 5f : p * 1.25f);

            }
        }
    }
}
