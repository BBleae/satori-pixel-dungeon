package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.Item;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class CupSprite extends MobSprite {

    private final Animation cast;

    public CupSprite() {
        super();

        texture(Assets.CUP);

        TextureFilm frames = new TextureFilm(texture, 12, 15);

        idle = new MovieClip.Animation(2, true);
        idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);

        run = new MovieClip.Animation(12, true);
        run.frames(frames, 4, 5, 6, 7);

        attack = new MovieClip.Animation(12, false);
        attack.frames(frames, 2, 3, 0);

        cast = attack.clone();

        die = new MovieClip.Animation(12, false);
        die.frames(frames, 8, 9, 10);

        play(idle);
    }

    @Override
    public void die() {
        super.die();
        emitter().burst(Speck.factory(Speck.STEAM), 6);
        emitter().burst(ShadowParticle.UP, 8);
    }

    @Override
    public void attack(int cell) {
        if (!Dungeon.level.adjacent(cell, ch.pos)) {

            ((MissileSprite) parent.recycle(MissileSprite.class)).
                    reset(ch.pos, cell, new CupShot(), () -> ch.onAttackComplete());

            play(cast);
            turnTo(ch.pos, cell);

        } else {

            super.attack(cell);

        }
    }

    public static class CupShot extends Item {
        {
            image = ItemSpriteSheet.FISHING_SPEAR;
        }
    }
}
