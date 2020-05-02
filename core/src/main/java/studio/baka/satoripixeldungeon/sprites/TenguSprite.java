package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.TextureFilm;

public class TenguSprite extends MobSprite {

    public TenguSprite() {
        super();

        texture(Assets.TENGU);

        TextureFilm frames = new TextureFilm(texture, 14, 16);

        idle = new Animation(2, true);
        idle.frames(frames, 0, 0, 0, 1);

        run = new Animation(15, false);
        run.frames(frames, 2, 3, 4, 5, 0);

        attack = new Animation(15, false);
        attack.frames(frames, 6, 7, 7, 0);

        zap = attack.clone();

        die = new Animation(8, false);
        die.frames(frames, 8, 9, 10, 10, 10, 10, 10, 10);

        play(run.clone());
    }

    @Override
    public void idle() {
        isMoving = false;
        super.idle();
    }

    @Override
    public void move(int from, int to) {

        place(to);

        play(run);
        turnTo(from, to);

        isMoving = true;

        if (Dungeon.level.water[to]) {
            GameScene.ripple(to);
        }

    }

    @Override
    public void attack(int cell) {
        if (!Dungeon.level.adjacent(cell, ch.pos)) {

            ((MissileSprite) parent.recycle(MissileSprite.class)).
                    reset(ch.pos, cell, new TenguShuriken(), () -> ch.onAttackComplete());

            play(zap);
            turnTo(ch.pos, cell);

        } else {

            super.attack(cell);

        }
    }

    @Override
    public void onComplete(Animation anim) {
        if (anim == run) {
            synchronized (this) {
                isMoving = false;
                idle();

                notifyAll();
            }
        } else {
            super.onComplete(anim);
        }
    }

    public static class TenguShuriken extends Item {
        {
            image = ItemSpriteSheet.SHURIKEN;
        }
    }
}
