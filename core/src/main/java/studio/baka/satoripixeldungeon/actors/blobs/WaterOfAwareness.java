package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Awareness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.Identification;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.journal.Notes.Landmark;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WaterOfAwareness extends WellWater {

    @Override
    protected boolean affectHero(Hero hero) {

        Sample.INSTANCE.play(Assets.SND_DRINK);
        emitter.parent.add(new Identification(hero.sprite.center()));

        hero.belongings.observe();

        for (int i = 0; i < Dungeon.level.length(); i++) {

            int terr = Dungeon.level.map[i];
            if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                Dungeon.level.discover(i);

                if (Dungeon.level.heroFOV[i]) {
                    GameScene.discoverTile(i, terr);
                }
            }
        }

        Buff.affect(hero, Awareness.class, Awareness.DURATION);
        Dungeon.observe();

        Dungeon.hero.interrupt();

        GLog.p(Messages.get(this, "procced"));

        return true;
    }

    @Override
    protected Item affectItem(Item item, int pos) {
        if (item.isIdentified()) {
            return null;
        } else {
            item.identify();
            Badges.validateItemLevelAquired(item);

            emitter.parent.add(new Identification(DungeonTilemap.tileCenterToWorld(pos)));

            return item;
        }
    }

    @Override
    protected Landmark record() {
        return Landmark.WELL_OF_AWARENESS;
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(Speck.QUESTION), 0.3f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
