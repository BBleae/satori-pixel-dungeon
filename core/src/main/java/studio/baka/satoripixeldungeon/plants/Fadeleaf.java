package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

public class Fadeleaf extends Plant {

    {
        image = 10;
    }

    @Override
    public void activate(final Char ch) {

        if (ch instanceof Hero) {

            ((Hero) ch).curAction = null;

            if (((Hero) ch).subClass == HeroSubClass.WARDEN) {

                if (Dungeon.bossLevel()) {
                    GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
                    return;

                }

                Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
                if (buff != null) buff.detach();
                buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
                if (buff != null) buff.detach();

                InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
                InterlevelScene.returnPos = -2;
                Game.switchScene(InterlevelScene.class);

            } else {
                ScrollOfTeleportation.teleportHero((Hero) ch);
            }

        } else if (ch instanceof Mob && !ch.properties().contains(Char.Property.IMMOVABLE)) {

            int count = 10;
            int newPos;
            do {
                newPos = Dungeon.level.randomRespawnCell();
                if (count-- <= 0) {
                    break;
                }
            } while (newPos == -1);

            if (newPos != -1 && !Dungeon.bossLevel()) {

                ch.pos = newPos;
                if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
                ch.sprite.place(ch.pos);
                ch.sprite.visible = Dungeon.level.heroFOV[ch.pos];

            }

        }

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_FADELEAF;

            plantClass = Fadeleaf.class;
        }
    }
}
