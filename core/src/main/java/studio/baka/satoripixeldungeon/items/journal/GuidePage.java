package studio.baka.satoripixeldungeon.items.journal;

import studio.baka.satoripixeldungeon.journal.Document;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class GuidePage extends DocumentPage {

    {
        image = ItemSpriteSheet.GUIDE_PAGE;
    }

    @Override
    public Document document() {
        return Document.ADVENTURERS_GUIDE;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", document().pageTitle(page()));
    }
}
