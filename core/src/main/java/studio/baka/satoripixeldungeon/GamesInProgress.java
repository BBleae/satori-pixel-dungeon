package studio.baka.satoripixeldungeon;

import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class GamesInProgress {
	
	public static final int MAX_SLOTS = 4;
	
	//null means we have loaded info and it is empty, no entry means unknown.
	private static final HashMap<Integer, Info> slotStates = new HashMap<>();
	public static int curSlot;
	
	public static HeroClass selectedClass;
	
	private static final String GAME_FOLDER = "game%d";
	private static final String GAME_FILE	= "game.dat";
	private static final String DEPTH_FILE	= "depth%d.dat";
	
	public static boolean gameExists( int slot ){
		return FileUtils.dirExists(Messages.format(GAME_FOLDER, slot));
	}
	
	public static String gameFolder( int slot ){
		return Messages.format(GAME_FOLDER, slot);
	}
	
	public static String gameFile( int slot ){
		return gameFolder(slot) + "/" + GAME_FILE;
	}
	
	public static String depthFile( int slot, int depth ) {
		return gameFolder(slot) + "/" + Messages.format(DEPTH_FILE, depth);
	}
	
	public static int firstEmpty(){
		for (int i = 1; i <= MAX_SLOTS; i++){
			if (check(i) == null) return i;
		}
		return -1;
	}
	
	public static ArrayList<Info> checkAll(){
		ArrayList<Info> result = new ArrayList<>();
		for (int i = 0; i <= MAX_SLOTS; i++){
			Info curr = check(i);
			if (curr != null) result.add(curr);
		}
		result.sort(scoreComparator);
		return result;
	}
	
	public static Info check( int slot ) {
		
		if (slotStates.containsKey( slot )) {
			
			return slotStates.get( slot );
			
		} else if (!gameExists( slot )) {
			
			slotStates.put(slot, null);
			return null;
			
		} else {
			
			Info info;
			try {
				
				Bundle bundle = FileUtils.bundleFromFile(gameFile(slot));
				info = new Info();
				info.slot = slot;
				Dungeon.preview(info, bundle);
				
				//saves from before 0.6.5c are not supported
				if (info.version < SatoriPixelDungeon.v0_6_5c) {
					info = null;
				}

			} catch (IOException e) {
				info = null;
			} catch (Exception e){
				SatoriPixelDungeon.reportException( e );
				info = null;
			}
			
			slotStates.put( slot, info );
			return info;
			
		}
	}

	public static void set(int slot, int depth, int challenges,
	                       Hero hero) {
		Info info = new Info();
		info.slot = slot;
		
		info.depth = depth;
		info.challenges = challenges;
		
		info.level = hero.lvl;
		info.str = hero.STR;
		info.exp = hero.exp;
		info.hp = hero.HP;
		info.ht = hero.HT;
		info.shld = hero.shielding();
		info.heroClass = hero.heroClass;
		info.subClass = hero.subClass;
		info.armorTier = hero.tier();
		
		info.goldCollected = Statistics.goldCollected;
		info.maxDepth = Statistics.deepestFloor;
		
		slotStates.put( slot, info );
	}
	
	public static void setUnknown( int slot ) {
		slotStates.remove( slot );
	}
	
	public static void delete( int slot ) {
		slotStates.put( slot, null );
	}
	
	public static class Info {
		public int slot;
		
		public int depth;
		public int version;
		public int challenges;
		
		public int level;
		public int str;
		public int exp;
		public int hp;
		public int ht;
		public int shld;
		public HeroClass heroClass;
		public HeroSubClass subClass;
		public int armorTier;
		
		public int goldCollected;
		public int maxDepth;
	}
	
	public static final Comparator<GamesInProgress.Info> scoreComparator = (lhs, rhs) -> {
        int lScore = (lhs.level * lhs.maxDepth * 100) + lhs.goldCollected;
        int rScore = (rhs.level * rhs.maxDepth * 100) + rhs.goldCollected;
        return (int)Math.signum( rScore - lScore );
    };
}
