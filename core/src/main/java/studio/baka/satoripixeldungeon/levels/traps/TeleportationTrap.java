package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class TeleportationTrap extends Trap {

    {
        color = TEAL;
        shape = DOTS;
    }

    @Override
    public void activate() {

        CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        Sample.INSTANCE.play(Assets.SND_TELEPORT);

        Char ch = Actor.findChar(pos);
        if (ch != null && !ch.flying) {
            if (ch instanceof Hero) {
                ScrollOfTeleportation.teleportHero((Hero) ch);
            } else {
                int count = 10;
                int pos;
                do {
                    pos = Dungeon.level.randomRespawnCell();
                    if (count-- <= 0) {
                        break;
                    }
                } while (pos == -1);

                if (pos == -1 || Dungeon.bossLevel()) {

                    GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));

                } else {

                    ch.pos = pos;
                    if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).HUNTING) {
                        ((Mob) ch).state = ((Mob) ch).WANDERING;
                    }
                    ch.sprite.place(ch.pos);
                    ch.sprite.visible = Dungeon.level.heroFOV[pos];

                }
            }
        }

        Heap heap = Dungeon.level.heaps.get(pos);

        if (heap != null) {
            int cell = Dungeon.level.randomRespawnCell();

            Item item = heap.pickUp();

            if (cell != -1) {
                Dungeon.level.drop(item, cell);
            }
        }
    }
}
