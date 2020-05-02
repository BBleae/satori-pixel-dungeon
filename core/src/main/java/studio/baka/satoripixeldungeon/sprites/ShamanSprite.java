package studio.baka.satoripixeldungeon.sprites;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Shaman;
import studio.baka.satoripixeldungeon.effects.Lightning;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;

public class ShamanSprite extends MobSprite {

    public ShamanSprite() {
        super();

        texture(Assets.SHAMAN);

        TextureFilm frames = new TextureFilm(texture, 12, 15);

        idle = new Animation(2, true);
        idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);

        run = new Animation(12, true);
        run.frames(frames, 4, 5, 6, 7);

        attack = new Animation(12, false);
        attack.frames(frames, 2, 3, 0);

        zap = attack.clone();

        die = new Animation(12, false);
        die.frames(frames, 8, 9, 10);

        play(idle);
    }

    public void zap(int pos) {

        Char enemy = Actor.findChar(pos);

        if (enemy != null) {
            parent.add(new Lightning(center(), enemy.sprite.destinationCenter(), (Shaman) ch));
        } else {
            parent.add(new Lightning(center(), pos, (Shaman) ch));
        }
        Sample.INSTANCE.play(Assets.SND_LIGHTNING);

        turnTo(ch.pos, pos);
        play(zap);
    }
}
