package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Eye;
import studio.baka.satoripixeldungeon.effects.Beam;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

import java.util.Objects;

public class EyeSprite extends MobSprite {

    private int zapPos;

    private final Animation charging;
    private final Emitter chargeParticles;

    public EyeSprite() {
        super();

        texture(Assets.EYE);

        TextureFilm frames = new TextureFilm(texture, 16, 18);

        idle = new Animation(8, true);
        idle.frames(frames, 0, 1, 2);

        charging = new Animation(12, true);
        charging.frames(frames, 3, 4);

        chargeParticles = centerEmitter();
        chargeParticles.autoKill = false;
        chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
        chargeParticles.on = false;

        run = new Animation(12, true);
        run.frames(frames, 5, 6);

        attack = new Animation(8, false);
        attack.frames(frames, 4, 3);
        zap = attack.clone();

        die = new Animation(8, false);
        die.frames(frames, 7, 8, 9);

        play(idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (((Eye) ch).beamCharged) play(charging);
    }

    @Override
    public void update() {
        super.update();
        chargeParticles.pos(center());
        chargeParticles.visible = visible;
    }

    public void charge(int pos) {
        turnTo(ch.pos, pos);
        play(charging);
    }

    @Override
    public void play(Animation anim) {
        chargeParticles.on = anim == charging;
        super.play(anim);
    }

    @Override
    public void zap(int pos) {
        zapPos = pos;
        super.zap(pos);
    }

    @Override
    public void onComplete(Animation anim) {
        super.onComplete(anim);

        if (anim == zap) {
            idle();
            if (Actor.findChar(zapPos) != null) {
                parent.add(new Beam.DeathRay(center(), Objects.requireNonNull(Actor.findChar(zapPos)).sprite.center()));
            } else {
                parent.add(new Beam.DeathRay(center(), DungeonTilemap.raisedTileCenterToWorld(zapPos)));
            }
            ((Eye) ch).deathGaze();
            ch.next();
        } else if (anim == die) {
            chargeParticles.killAndErase();
        }
    }
}
