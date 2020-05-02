package studio.baka.satoripixeldungeon.items.keys;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.windows.WndJournal;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public abstract class Key extends Item {

    public static final float TIME_TO_UNLOCK = 1f;

    {
        stackable = true;
        unique = true;
    }

    public int depth;

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && ((Key) item).depth == depth;
    }

    @Override
    public boolean doPickUp(Hero hero) {
        GameScene.pickUpJournal(this, hero.pos);
        WndJournal.last_index = 2;
        Notes.add(this);
        Sample.INSTANCE.play(Assets.SND_ITEM);
        hero.spendAndNext(TIME_TO_PICK_UP);
        GameScene.updateKeyDisplay();
        return true;
    }

    private static final String DEPTH = "depth";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, depth);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        depth = bundle.getInt(DEPTH);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

}
