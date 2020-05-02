package studio.baka.satoripixeldungeon.items.journal;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.journal.Document;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.windows.WndJournal;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public abstract class DocumentPage extends Item {

    {
        image = ItemSpriteSheet.MASTERY;
    }

    public abstract Document document();

    private String page;

    public void page(String page) {
        this.page = page;
    }

    public String page() {
        return page;
    }

    @Override
    public final boolean doPickUp(Hero hero) {
        GameScene.pickUpJournal(this, hero.pos);
        GameScene.flashJournal();
        if (document() == Document.ALCHEMY_GUIDE) {
            WndJournal.last_index = 1;
        } else {
            WndJournal.last_index = 0;
        }
        document().addPage(page);
        Sample.INSTANCE.play(Assets.SND_ITEM);
        hero.spendAndNext(TIME_TO_PICK_UP);
        return true;
    }

    private static final String PAGE = "page";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PAGE, page());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        page = bundle.getString(PAGE);
    }
}
