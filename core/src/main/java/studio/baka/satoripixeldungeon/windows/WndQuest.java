package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.actors.mobs.npcs.NPC;
import studio.baka.satoripixeldungeon.messages.Messages;

public class WndQuest extends WndTitledMessage {

	public WndQuest( NPC questgiver, String text ) {
		super( questgiver.sprite(), Messages.titleCase( questgiver.name ), text );
	}
}
