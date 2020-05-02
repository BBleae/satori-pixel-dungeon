package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.levels.traps.Trap;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.tiles.TerrainFeaturesTilemap;

public class WndInfoTrap extends WndTitledMessage {

	public WndInfoTrap(Trap trap) {

		super(TerrainFeaturesTilemap.tile( trap.pos, Dungeon.level.map[trap.pos]),
				Messages.titleCase(trap.name),
				(!trap.active ? Messages.get(WndInfoTrap.class, "inactive") + "\n\n" : "") + trap.desc());

	}

}
