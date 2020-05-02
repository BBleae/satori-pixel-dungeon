package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;

public class HeroSprite extends CharSprite {

    private static final int FRAME_WIDTH = 12;
    private static final int FRAME_HEIGHT = 15;

    private static final int RUN_FRAMERATE = 20;

    private static TextureFilm tiers;

    private Animation fly;
    private Animation read;

    public HeroSprite() {
        super();

        texture(Dungeon.hero.heroClass.spritesheet());
        updateArmor();

        link(Dungeon.hero);

        if (ch.isAlive())
            idle();
        else
            die();
    }

    public void updateArmor() {

        TextureFilm film = new TextureFilm(tiers(), Dungeon.hero.tier(), FRAME_WIDTH, FRAME_HEIGHT);

        idle = new Animation(1, true);
        idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);

        run = new Animation(RUN_FRAMERATE, true);
        run.frames(film, 2, 3, 4, 5, 6, 7);

        die = new Animation(20, false);
        die.frames(film, 8, 9, 10, 11, 12, 11);

        attack = new Animation(15, false);
        attack.frames(film, 13, 14, 15, 0);

        zap = attack.clone();

        operate = new Animation(8, false);
        operate.frames(film, 16, 17, 16, 17);

        fly = new Animation(1, true);
        fly.frames(film, 18);

        read = new Animation(20, false);
        read.frames(film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19);

        if (Dungeon.hero.isAlive())
            idle();
        else
            die();
    }

    @Override
    public void place(int p) {
        super.place(p);
        Camera.main.panTo(center(), 5f);
    }

    @Override
    public void move(int from, int to) {
        super.move(from, to);
        if (ch.flying) {
            play(fly);
        }
        Camera.main.panFollow(this, 20f);
    }

    @Override
    public void jump(int from, int to, Callback callback) {
        super.jump(from, to, callback);
        play(fly);
    }

    public void read() {
        animCallback = () -> {
            idle();
            ch.onOperateComplete();
        };
        play(read);
    }

    @Override
    public void bloodBurstA(PointF from, int damage) {
        //Does nothing.

        /*
         * This is both for visual clarity, and also for content ratings regarding violence
         * towards human characters. The heroes are the only human or human-like characters which
         * participate in combat, so removing all blood associated with them is a simple way to
         * reduce the violence rating of the game.
         */
    }

    @Override
    public void update() {
        sleeping = ch.isAlive() && ((Hero) ch).resting;

        super.update();
    }

    public void sprint(float speed) {
        run.delay = 1f / speed / RUN_FRAMERATE;
    }

    public static TextureFilm tiers() {
        if (tiers == null) {
            SmartTexture texture = TextureCache.get(Assets.ROGUE);
            tiers = new TextureFilm(texture, texture.width, FRAME_HEIGHT);
        }

        return tiers;
    }

    public static Image avatar(HeroClass cl, int armorTier) {

        RectF patch = tiers().get(armorTier);
        Image avatar = new Image(cl.spritesheet());
        RectF frame = avatar.texture.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT);
        frame.shift(patch.left, patch.top);
        avatar.frame(frame);

        return avatar;
    }
}
