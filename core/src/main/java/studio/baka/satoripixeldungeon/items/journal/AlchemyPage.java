package studio.baka.satoripixeldungeon.items.journal;

import studio.baka.satoripixeldungeon.journal.Document;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class AlchemyPage extends DocumentPage {

    {
        image = ItemSpriteSheet.ALCH_PAGE;
    }

    @Override
    public Document document() {
        return Document.ALCHEMY_GUIDE;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", document().pageTitle(page()));
    }
}
