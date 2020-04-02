/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewTengu;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.MazeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SurfaceLevel extends Level {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

	//keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
	//private ArrayList<Item> storedItems = new ArrayList<>();
	
	@Override
	public String tilesTex() {
		return Assets.TILES_SURFACE;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_SEWERS;
	}
	
	@Override
	protected boolean build() {
		
		setSize(10, 9);
		
		map = MAP_START.clone();

		buildFlagMaps();
		cleanWalls();

		entrance = 7+7*width;
		exit = 4+2*width;

		return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
	}
	
	public Actor respawner() {
		return null;
	}

	public int randomRespawnCell() {
		return entrance;
	}

	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			drop( item, exit - 1 ).type = Heap.Type.REMAINS;
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(this, "water_name");
			case Terrain.BOOKSHELF:
				return Messages.get(this, "bookshelf_name");
			case Terrain.EXIT:
			case Terrain.PEDESTAL:
				return Messages.get( this, "exit_name");
			case Terrain.STATUE_SP:
			case Terrain.STATUE:
				return Messages.get( this, "a");
			case Terrain.EMPTY_WELL:
				return Messages.get( this, "c");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(this, "empty_deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get( this, "empty_sp_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(this, "bookshelf_desc");
			case Terrain.EXIT:
			case Terrain.PEDESTAL:
				return Messages.get( this, "exit_desc");
			case Terrain.STATUE_SP:
			case Terrain.STATUE:
				return Messages.get( this, "b");
			case Terrain.EMPTY_WELL:
				return Messages.get( this, "d");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}

	private static final int W = Terrain.WALL;
	private static final int D = Terrain.DOOR;
	private static final int L = Terrain.LOCKED_DOOR;
	private static final int e = Terrain.EMPTY;
	private static final int A = Terrain.WATER;
	private static final int m = Terrain.EMPTY_SP;
	private static final int g = Terrain.GRASS;
	private static final int B = Terrain.BOOKSHELF;

	private static final int S = Terrain.STATUE_SP;
	private static final int s = Terrain.STATUE;

	private static final int E = Terrain.ENTRANCE;
	private static final int X = Terrain.EXIT;

	private static final int M = Terrain.WALL_DECO;
	private static final int w = Terrain.EMPTY_WELL;
	private static final int P = Terrain.PEDESTAL;
	private static final int T = Terrain.SIGN;

	//Especially as I means I won't be limited to legal identifiers
	private static final int[] MAP_START =
			{       W, W, W, W, W, W, W, W, W, W,
					W, B, B, B, B, B, B, B, B, W,
					W, w, m, P, X, m, m, m, w, W,
					W, m, m, s, s, s, m, m, m, W,
					W, m, m, S, S, S, m, m, m, W,
					W, m, m, m, m, m, m, m, m, W,
					W, m, m, m, m, m, m, m, m, W,
					W, w, m, m, m, m, m, m, w, W,
					W, W, W, W, W, W, W, D, W, W,
			};
	}
