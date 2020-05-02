package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.Icons;

public class WndError extends WndTitledMessage {

	public WndError( String message ) {
		super( Icons.WARNING.get(), Messages.get(WndError.class, "title"), message );
	}

}
