package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FireImbue;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Firebloom extends Plant {

    {
        image = 1;
    }

    @Override
    public void activate(Char ch) {

        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, FireImbue.class).set(15f);
        }

        GameScene.add(Blob.seed(pos, 2, Fire.class));

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(FlameParticle.FACTORY, 5);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_FIREBLOOM;

            plantClass = Firebloom.class;
        }
    }
}
