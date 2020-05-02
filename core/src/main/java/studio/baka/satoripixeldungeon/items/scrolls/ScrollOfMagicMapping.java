package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Awareness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.MindVision;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfMagicMapping extends Scroll {

    {
        initials = 2;
    }

    @Override
    public void doRead() {

        int length = Dungeon.level.length();
        int[] map = Dungeon.level.map;
        boolean[] mapped = Dungeon.level.mapped;
        boolean[] discoverable = Dungeon.level.discoverable;

        boolean noticed = false;

        for (int i = 0; i < length; i++) {

            int terr = map[i];

            if (discoverable[i]) {

                mapped[i] = true;
                if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                    Dungeon.level.discover(i);

                    if (Dungeon.level.heroFOV[i]) {
                        GameScene.discoverTile(i, terr);
                        discover(i);

                        noticed = true;
                    }
                }
            }
        }
        GameScene.updateFog();

        GLog.i(Messages.get(this, "layout"));
        if (noticed) {
            Sample.INSTANCE.play(Assets.SND_SECRET);
        }

        SpellSprite.show(curUser, SpellSprite.MAP);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        setKnown();

        readAnimation();
    }

    @Override
    public void empoweredRead() {
        doRead();
        Buff.affect(curUser, MindVision.class, MindVision.DURATION);
        Buff.affect(curUser, Awareness.class, Awareness.DURATION);
        Dungeon.observe();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }

    public static void discover(int cell) {
        CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.1f, 4);
    }
}
