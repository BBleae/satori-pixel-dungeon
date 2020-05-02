package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.tiles.TerrainFeaturesTilemap;

public class WndInfoPlant extends WndTitledMessage {
	
	public WndInfoPlant( Plant plant ) {
		
		super(TerrainFeaturesTilemap.tile( plant.pos, Dungeon.level.map[plant.pos]),
				plant.plantName, plant.desc());

	}
}
