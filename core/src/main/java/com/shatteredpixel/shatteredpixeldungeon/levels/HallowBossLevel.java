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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yog;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Author;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TestItem;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
//import com.shatteredpixel.shatteredpixeldungeon.levels.features.MazeBalanca;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
//import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.MazeBalancaRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.MazeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.Arrays;

public class HallowBossLevel extends Level {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

	//keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
	//private ArrayList<Item> storedItems = new ArrayList<>();

	private int stairs = -1;
	private boolean enteredArena = false;
	private boolean keyDropped = false;

	private static final int WIDTH = 33;
	private static final int HEIGHT = 33;

	private enum stat{
		not_started,maze1,maze2,maze3,ended
	}
	private stat levelstat = stat.not_started;
	
	@Override
	public String tilesTex() {
		return Assets.TILES_HALLOW;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLOW;
	}

	private static final String STAIRS	= "stairs";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	private static final String STAT	= "stat";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STAIRS, stairs );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
		bundle.put( STAT, levelstat );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stairs = bundle.getInt( STAIRS );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
		levelstat = bundle.getEnum( STAT, stat.class );
	}
	
	@Override
	protected boolean build() {
		
		setSize(WIDTH, HEIGHT);

		Arrays.fill( map, Terrain.WALL );
		Painter.fill(this,1,1,WIDTH-2,HEIGHT-2,Terrain.EMPTY_SP);

		buildFlagMaps();
		cleanWalls();

		entrance = WIDTH/2 + (HEIGHT/2) *width ;
		map[entrance] = Terrain.ENTRANCE;
		exit = 1+2*width;

		levelstat = stat.not_started;

		return true;
	}
	/*
	@Override
	public Mob createMob() {
		return null;
	}
	*/
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
		//drop(new TestItem(), 30+30*width);
	}

	private void doMagic( int cell ) {
		set( cell, Terrain.EMPTY_SP );
		CellEmitter.get( cell ).start( FlameParticle.FACTORY, 0.1f, 3 );
	}

	public void process(){
		switch (levelstat){
			case not_started:
				generatemaze();
				levelstat = stat.maze1;
				break;
			case maze1:

				break;
			case maze2:

				break;
			case maze3:

				break;
			case ended: default:

				break;
		}
	}

	private void generatemaze(){
		//MazeBalanca mazeBalanca = new MazeBalanca(8,8);
		//changeMap(mazeBalanca.pFullarray());
		/*
		Room maze = new MazeBalancaRoom();
		maze.set(0,0,WIDTH-1,HEIGHT-1);
		maze.paint(this);
		buildFlagMaps();
		cleanWalls();
		GameScene.resetMap();
		*/
		GameScene.flash(0xFFFFFF);
		Sample.INSTANCE.play(Assets.SND_BLAST);
	}

	private void changeMap(int[] map){
		this.map = map.clone();
		buildFlagMaps();
		cleanWalls();

		exit = entrance = 0;
		for (int i = 0; i < length(); i ++)
			if (map[i] == Terrain.ENTRANCE)
				entrance = i;
			else if (map[i] == Terrain.EXIT)
				exit = i;

		BArray.setFalse(visited);
		BArray.setFalse(mapped);

		for (Blob blob: blobs.values()){
			blob.fullyClear();
		}
		addVisuals(); //this also resets existing visuals
		//resetTraps();


		GameScene.resetMap();
		Dungeon.observe();
	}

	@Override
	public void occupyCell( Char ch ) {

		super.occupyCell( ch );

		if (!enteredArena && ch == Dungeon.hero && ch.pos != entrance) {
			enteredArena = true;
			seal();
			doMagic( entrance );
			GameScene.updateMap();

			Dungeon.observe();

			Author boss = new Author();

			do {
				boss.pos = Random.Int( length() );
			}
			while (!passable[boss.pos] || !heroFOV[boss.pos]);

			CellEmitter.get( boss.pos ).start( SparkParticle.FACTORY, 0.1f, 3 );
			GameScene.add( boss );

			stairs = entrance;
			entrance = -1;
		}
	}

	@Override
	public Heap drop( Item item, int cell ) {

		if (!keyDropped && item instanceof SkeletonKey) {
			keyDropped = true;
			unseal();

			entrance = stairs;
			set( entrance, Terrain.ENTRANCE );
			GameScene.updateMap( entrance );
		}

		return super.drop( item, cell );
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(HallowLevel.class, "water_name");
			case Terrain.GRASS:
				return Messages.get(HallowLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(HallowLevel.class, "high_grass_name");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(HallowLevel.class, "statue_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(HallowLevel.class, "water_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(HallowLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(HallowLevel.class, "bookshelf_desc");
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
}
