package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.wands.WandOfLivingEarth;
import studio.baka.satoripixeldungeon.items.wands.WandOfWarding;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.*;
import studio.baka.satoripixeldungeon.ui.Archs;
import studio.baka.satoripixeldungeon.ui.RedButton;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Quad;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.*;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.nio.FloatBuffer;
import java.util.Calendar;

public class SurfaceScene extends PixelScene {

    private static final int FRAME_WIDTH = 88;
    private static final int FRAME_HEIGHT = 125;

    private static final int FRAME_MARGIN_TOP = 9;
    private static final int FRAME_MARGIN_X = 4;

    private static final int BUTTON_HEIGHT = 20;

    private static final int SKY_WIDTH = 80;
    private static final int SKY_HEIGHT = 112;

    private static final int NSTARS = 100;
    private static final int NCLOUDS = 5;

    private Camera viewport;

    @Override
    public void create() {

        super.create();

        Music.INSTANCE.play(Assets.HAPPY, true);

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Archs archs = new Archs();
        archs.reversed = true;
        archs.setSize(w, h);
        add(archs);

        float vx = align((w - SKY_WIDTH) / 2f);
        float vy = align((h - SKY_HEIGHT - BUTTON_HEIGHT) / 2f);

        Point s = Camera.main.cameraToScreen(vx, vy);
        viewport = new Camera(s.x, s.y, SKY_WIDTH, SKY_HEIGHT, defaultZoom);
        Camera.add(viewport);

        Group window = new Group();
        window.camera = viewport;
        add(window);

        boolean dayTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 7;

        Sky sky = new Sky(dayTime);
        sky.scale.set(SKY_WIDTH, SKY_HEIGHT);
        window.add(sky);

        if (!dayTime) {
            for (int i = 0; i < NSTARS; i++) {
                float size = Random.Float();
                ColorBlock star = new ColorBlock(size, size, 0xFFFFFFFF);
                star.x = Random.Float(SKY_WIDTH) - size / 2;
                star.y = Random.Float(SKY_HEIGHT) - size / 2;
                star.am = size * (1 - star.y / SKY_HEIGHT);
                window.add(star);
            }
        }

        float range = SKY_HEIGHT * 2 / 3;
        for (int i = 0; i < NCLOUDS; i++) {
            Cloud cloud = new Cloud((NCLOUDS - 1 - i) * (range / NCLOUDS) + Random.Float(range / NCLOUDS), dayTime);
            window.add(cloud);
        }

        int nPatches = (int) (sky.width() / GrassPatch.WIDTH + 1);

        for (int i = 0; i < nPatches * 4; i++) {
            GrassPatch patch = new GrassPatch((i - 0.75f) * GrassPatch.WIDTH / 4, SKY_HEIGHT + 1, dayTime);
            patch.brightness(dayTime ? 0.7f : 0.4f);
            window.add(patch);
        }

        Avatar a = new Avatar(Dungeon.hero.heroClass);
        // Removing semitransparent contour
        a.am = 2;
        a.aa = -1;
        a.x = (SKY_WIDTH - a.width) / 2;
        a.y = SKY_HEIGHT - a.height;
        align(a);

        final Pet pet = new Pet();
        pet.rm = pet.gm = pet.bm = 1.2f;
        pet.x = SKY_WIDTH / 2 + 2;
        pet.y = SKY_HEIGHT - pet.height;
        align(pet);

        //allies. Attempts to pick highest level, but prefers rose > earth > ward.
        //Rose level is halved because it's easier to upgrade
        CharSprite allySprite = null;

        //picks the highest between ghost's weapon, armor, and rose level/2
        int roseLevel = 0;
        DriedRose rose = Dungeon.hero.belongings.getItem(DriedRose.class);
        if (rose != null) {
            roseLevel = rose.level() / 2;
            if (rose.ghostWeapon() != null) {
                roseLevel = Math.max(roseLevel, rose.ghostWeapon().level());
            }
            if (rose.ghostArmor() != null) {
                roseLevel = Math.max(roseLevel, rose.ghostArmor().level());
            }
        }

        int earthLevel = Dungeon.hero.belongings.getItem(WandOfLivingEarth.class) == null ? 0 : Dungeon.hero.belongings.getItem(WandOfLivingEarth.class).level();
        int wardLevel = Dungeon.hero.belongings.getItem(WandOfWarding.class) == null ? 0 : Dungeon.hero.belongings.getItem(WandOfWarding.class).level();

        MagesStaff staff = Dungeon.hero.belongings.getItem(MagesStaff.class);
        if (staff != null) {
            if (staff.wandClass() == WandOfLivingEarth.class) {
                earthLevel = Math.max(earthLevel, staff.level());
            } else if (staff.wandClass() == WandOfWarding.class) {
                wardLevel = Math.max(wardLevel, staff.level());
            }
        }

        if (roseLevel >= 3 && roseLevel >= earthLevel && roseLevel >= wardLevel) {
            allySprite = new GhostSprite();
            if (dayTime) allySprite.alpha(0.4f);
        } else if (earthLevel >= 3 && earthLevel >= wardLevel) {
            allySprite = new EarthGuardianSprite();
        } else if (wardLevel >= 3) {
            allySprite = new WardSprite();
            ((WardSprite) allySprite).updateTier(Math.min(wardLevel + 2, 6));
        }

        if (allySprite != null) {
            allySprite.add(CharSprite.State.PARALYSED);
            allySprite.scale = new PointF(2, 2);
            allySprite.x = a.x - allySprite.width() * 0.75f;
            allySprite.y = SKY_HEIGHT - allySprite.height();
            align(allySprite);
            window.add(allySprite);
        }

        window.add(a);
        window.add(pet);

        window.add(new PointerArea(sky) {
            protected void onClick(PointerEvent event) {
                pet.jump();
            }
        });

        for (int i = 0; i < nPatches; i++) {
            GrassPatch patch = new GrassPatch((i - 0.5f) * GrassPatch.WIDTH, SKY_HEIGHT, dayTime);
            patch.brightness(dayTime ? 1.0f : 0.8f);
            window.add(patch);
        }

        Image frame = new Image(Assets.SURFACE);

        frame.frame(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        frame.x = vx - FRAME_MARGIN_X;
        frame.y = vy - FRAME_MARGIN_TOP;
        add(frame);

        if (dayTime) {
            a.brightness(1.2f);
            pet.brightness(1.2f);
        } else {
            frame.hardlight(0xDDEEFF);
        }

        RedButton gameOver = new RedButton(Messages.get(this, "exit")) {
            protected void onClick() {
                Game.switchScene(RankingsScene.class);
            }
        };
        gameOver.setSize(SKY_WIDTH - FRAME_MARGIN_X * 2, BUTTON_HEIGHT);
        gameOver.setPos(frame.x + FRAME_MARGIN_X * 2, frame.y + frame.height + 4);
        add(gameOver);

        Badges.validateHappyEnd();

        fadeIn();
    }

    @Override
    public void destroy() {
        Badges.saveGlobal();

        Camera.remove(viewport);
        super.destroy();
    }

    @Override
    protected void onBackPressed() {
    }

    private static class Sky extends Visual {

        private static final int[] day = {0xFF4488FF, 0xFFCCEEFF};
        private static final int[] night = {0xFF001155, 0xFF335980};

        private final SmartTexture texture;
        private final FloatBuffer verticesBuffer;

        public Sky(boolean dayTime) {
            super(0, 0, 1, 1);

            texture = TextureCache.createGradient(dayTime ? day : night);

            float[] vertices = new float[16];
            verticesBuffer = Quad.create();

            vertices[2] = 0.25f;
            vertices[6] = 0.25f;
            vertices[10] = 0.75f;
            vertices[14] = 0.75f;

            vertices[3] = 0;
            vertices[7] = 1;
            vertices[11] = 1;
            vertices[15] = 0;


            vertices[0] = 0;
            vertices[1] = 0;

            vertices[4] = 1;
            vertices[5] = 0;

            vertices[8] = 1;
            vertices[9] = 1;

            vertices[12] = 0;
            vertices[13] = 1;

            verticesBuffer.position(0);
            verticesBuffer.put(vertices);
        }

        @Override
        public void draw() {

            super.draw();

            NoosaScript script = NoosaScript.get();

            texture.bind();

            script.camera(camera());

            script.uModel.valueM4(matrix);
            script.lighting(
                    rm, gm, bm, am,
                    ra, ga, ba, aa);

            script.drawQuad(verticesBuffer);
        }
    }

    private static class Cloud extends Image {

        private static int lastIndex = -1;

        public Cloud(float y, boolean dayTime) {
            super(Assets.SURFACE);

            int index;
            do {
                index = Random.Int(3);
            } while (index == lastIndex);

            switch (index) {
                case 0:
                    frame(88, 0, 49, 20);
                    break;
                case 1:
                    frame(88, 20, 49, 22);
                    break;
                case 2:
                    frame(88, 42, 50, 18);
                    break;
            }

            lastIndex = index;

            this.y = y;

            scale.set(1 - y / SKY_HEIGHT);
            x = Random.Float(SKY_WIDTH + width()) - width();
            speed.x = scale.x * (dayTime ? +8 : -8);

            if (dayTime) {
                tint(0xCCEEFF, 1 - scale.y);
            } else {
                rm = gm = bm = +3.0f;
                ra = ga = ba = -2.1f;
            }
        }

        @Override
        public void update() {
            super.update();
            if (speed.x > 0 && x > SKY_WIDTH) {
                x = -width();
            } else if (speed.x < 0 && x < -width()) {
                x = SKY_WIDTH;
            }
        }
    }

    private static class Avatar extends Image {

        private static final int WIDTH = 24;
        private static final int HEIGHT = 32;

        public Avatar(HeroClass cl) {
            super(Assets.AVATARS);
            frame(new TextureFilm(texture, WIDTH, HEIGHT).get(cl.ordinal()));
        }
    }

    private static class Pet extends RatSprite {

        public void jump() {
            play(run);
        }

        @Override
        public void onComplete(Animation anim) {
            if (anim == run) {
                idle();
            }
        }
    }

    private static class GrassPatch extends Image {

        public static final int WIDTH = 16;
        public static final int HEIGHT = 14;

        private final float tx;
        private final float ty;

        private double a = Random.Float(5);
        private double angle;

        private final boolean forward;

        public GrassPatch(float tx, float ty, boolean forward) {

            super(Assets.SURFACE);

            frame(88 + Random.Int(4) * WIDTH, 60, WIDTH, HEIGHT);

            this.tx = tx;
            this.ty = ty;

            this.forward = forward;
        }

        @Override
        public void update() {
            super.update();
            a += Random.Float(Game.elapsed * 5);
            angle = (2 + Math.cos(a)) * (forward ? +0.2 : -0.2);

            scale.y = (float) Math.cos(angle);

            x = tx + (float) Math.tan(angle) * width;
            y = ty - scale.y * height;
        }

        @Override
        protected void updateMatrix() {
            super.updateMatrix();
            Matrix.skewX(matrix, (float) (angle / Matrix.G2RAD));
        }
    }
}
