package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

public class ScrollOfPassage extends ExoticScroll {

    {
        initials = 8;
    }

    @Override
    public void doRead() {

        setKnown();

        if (Dungeon.bossLevel()) {

            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
            return;

        }

        Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (buff != null) buff.detach();
        buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (buff != null) buff.detach();

        InterlevelScene.mode = InterlevelScene.Mode.RETURN;
        InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1 - (Dungeon.depth - 2) % 5));
        InterlevelScene.returnPos = -1;
        Game.switchScene(InterlevelScene.class);
    }
}
