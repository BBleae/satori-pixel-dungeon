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
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TestItem;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
//import com.shatteredpixel.shatteredpixeldungeon.levels.features.MazeBalanca;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.MazeBalanca;
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

	public enum stat{
		not_started,maze1,maze2,maze3,ended
	}
	public stat levelstat = stat.not_started;
	
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
				moveplayer();
				moveauthor();
				levelstat = stat.maze1;
				break;
			case maze1:
				generatemaze();
				moveplayer();
				levelstat = stat.maze2;
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
		GameScene.flash(0x660000);		//Dark Red  //8*8的maze的生成时间肯定比较长。先闪它一下。
		MazeBalanca mazeBalanca = new MazeBalanca(8,8);
		int[] test_map = mazeBalanca.paint_pArray();//17*17=289 map
		changeMap(test_map);
		cleanMapState();
		GameScene.flash(0x339900);		//Dark Green
		Sample.INSTANCE.play(Assets.SND_BLAST);
	}

	private void moveplayer(){
		//ScrollOfTeleportation.teleportToLocation(Dungeon.hero,getPos(2,2));
		ScrollOfTeleportation.appear( Dungeon.hero, getPos(2,2) );
		occupyCell( Dungeon.hero );
		Dungeon.observe();
		GameScene.updateFog();

		for (Mob m : mobs){
			//bring the first ally with you
			if (m.alignment == Char.Alignment.ALLY && !m.properties().contains(Char.Property.IMMOVABLE)){
				m.pos = getPos(1,1);
				m.sprite.place(m.pos);
				break;
			}
		}
	}

	private void moveauthor(){
		for (Mob m : mobs){
			//bring the Author to the end of the maze
			if (m instanceof Author){
				m.pos = getPos(30,30);
				m.sprite.place(m.pos);
				break;
			}
		}
	}

	private void changeMap(int[] map){
		this.map = map.clone();
		buildFlagMaps();
		cleanWalls();

		BArray.setFalse(visited);
		BArray.setFalse(mapped);

		for (Blob blob: blobs.values()){
			blob.fullyClear();
		}
		addVisuals(); //this also resets existing visuals

		GameScene.resetMap(); //???
		Dungeon.observe();
	}

	private void cleanMapState(){
		buildFlagMaps();
		cleanWalls();

		BArray.setFalse(visited);
		BArray.setFalse(mapped);

		for (Blob blob: blobs.values()){
			blob.fullyClear();
		}
		addVisuals(); //this also resets existing visuals
		traps.clear();
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
			boss.pos = entrance;
			/*
			do {
				boss.pos = Random.Int( length() );
			}
			while (!passable[boss.pos] || !heroFOV[boss.pos]);
			*/
			CellEmitter.get( boss.pos ).start( SparkParticle.FACTORY, 0.1f, 3 );
			GameScene.add( boss );

			boss.notice();

			stairs = entrance;
			entrance = -1;
		}
	}

	@Override
	public Heap drop( Item item, int cell ) {

		if (!keyDropped && item instanceof Amulet) {
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
