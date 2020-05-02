package studio.baka.satoripixeldungeon;

import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.hero.Belongings;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.bags.Bag;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.ui.QuickSlotButton;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public enum Rankings {
	
	INSTANCE;
	
	public static final int TABLE_SIZE	= 11;
	
	public static final String RANKINGS_FILE = "rankings.dat";
	
	public ArrayList<Record> records;
	public int lastRecord;
	public int totalNumber;
	public int wonNumber;

	public void submit( boolean win, Class cause ) {

		load();
		
		Record rec = new Record();
		
		rec.cause = cause;
		rec.win		= win;
		rec.heroClass	= Dungeon.hero.heroClass;
		rec.armorTier	= Dungeon.hero.tier();
		rec.herolevel	= Dungeon.hero.lvl;
		rec.depth		= Dungeon.depth;
		rec.score	= score( win );
		
		INSTANCE.saveGameData(rec);

		rec.gameID = UUID.randomUUID().toString();
		
		records.add( rec );
		
		records.sort(scoreComparator);
		
		lastRecord = records.indexOf( rec );
		int size = records.size();
		while (size > TABLE_SIZE) {

			if (lastRecord == size - 1) {
				records.remove( size - 2 );
				lastRecord--;
			} else {
				records.remove( size - 1 );
			}

			size = records.size();
		}
		
		totalNumber++;
		if (win) {
			wonNumber++;
		}

		Badges.validateGamesPlayed();
		
		save();
	}

	private int score( boolean win ) {
		return (Statistics.goldCollected + Dungeon.hero.lvl * (win ? 26 : Dungeon.depth ) * 100) * (win ? 2 : 1);
	}

	public static final String HERO = "hero";
	public static final String STATS = "stats";
	public static final String BADGES = "badges";
	public static final String HANDLERS = "handlers";
	public static final String CHALLENGES = "challenges";

	public void saveGameData(Record rec){
		rec.gameData = new Bundle();

		Belongings belongings = Dungeon.hero.belongings;

		//save the hero and belongings
		@SuppressWarnings("unchecked") ArrayList<Item> allItems = (ArrayList<Item>) belongings.backpack.items.clone();
		//remove items that won't show up in the rankings screen
		for (Item item : belongings.backpack.items.toArray( new Item[0])) {
			if (item instanceof Bag){
				for (Item bagItem : ((Bag) item).items.toArray( new Item[0])){
					if (Dungeon.quickslot.contains(bagItem)) belongings.backpack.items.add(bagItem);
				}
				belongings.backpack.items.remove(item);
			} else if (!Dungeon.quickslot.contains(item))
				belongings.backpack.items.remove(item);
		}
		rec.gameData.put( HERO, Dungeon.hero );

		//save stats
		Bundle stats = new Bundle();
		Statistics.storeInBundle(stats);
		rec.gameData.put( STATS, stats);

		//save badges
		Bundle badges = new Bundle();
		Badges.saveLocal(badges);
		rec.gameData.put( BADGES, badges);

		//save handler information
		Bundle handler = new Bundle();
		Scroll.saveSelectively(handler, belongings.backpack.items);
		Potion.saveSelectively(handler, belongings.backpack.items);
		//include worn rings
		if (belongings.misc1 != null) belongings.backpack.items.add(belongings.misc1);
		if (belongings.misc2 != null) belongings.backpack.items.add(belongings.misc2);
		if (belongings.misc3 != null) belongings.backpack.items.add(belongings.misc3);
		Ring.saveSelectively(handler, belongings.backpack.items);
		rec.gameData.put( HANDLERS, handler);

		//restore items now that we're done saving
		belongings.backpack.items = allItems;
		
		//save challenges
		rec.gameData.put( CHALLENGES, Dungeon.challenges );
	}

	public void loadGameData(Record rec){
		Bundle data = rec.gameData;

		Actor.clear();
		Dungeon.hero = null;
		Dungeon.level = null;
		Generator.reset();
		Notes.reset();
		Dungeon.quickslot.reset();
		QuickSlotButton.reset();

		Bundle handler = data.getBundle(HANDLERS);
		Scroll.restore(handler);
		Potion.restore(handler);
		Ring.restore(handler);

		Badges.loadLocal(data.getBundle(BADGES));

		Dungeon.hero = (Hero)data.get(HERO);

		Statistics.restoreFromBundle(data.getBundle(STATS));
		
		Dungeon.challenges = data.getInt(CHALLENGES);

	}
	
	private static final String RECORDS	= "records";
	private static final String LATEST	= "latest";
	private static final String TOTAL	= "total";
	private static final String WON     = "won";

	public void save() {
		Bundle bundle = new Bundle();
		bundle.put( RECORDS, records );
		bundle.put( LATEST, lastRecord );
		bundle.put( TOTAL, totalNumber );
		bundle.put( WON, wonNumber );

		try {
			FileUtils.bundleToFile( RANKINGS_FILE, bundle);
		} catch (IOException e) {
			SatoriPixelDungeon.reportException(e);
		}

	}
	
	public void load() {
		
		if (records != null) {
			return;
		}
		
		records = new ArrayList<>();
		
		try {
			Bundle bundle = FileUtils.bundleFromFile( RANKINGS_FILE );
			
			for (Bundlable record : bundle.getCollection( RECORDS )) {
				records.add( (Record)record );
			}
			lastRecord = bundle.getInt( LATEST );
			
			totalNumber = bundle.getInt( TOTAL );
			if (totalNumber == 0) {
				totalNumber = records.size();
			}

			wonNumber = bundle.getInt( WON );
			if (wonNumber == 0) {
				for (Record rec : records) {
					if (rec.win) {
						wonNumber++;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class Record implements Bundlable {

		private static final String CAUSE   = "cause";
		private static final String WIN		= "win";
		private static final String SCORE	= "score";
		private static final String TIER	= "tier";
		private static final String LEVEL	= "level";
		private static final String DEPTH	= "depth";
		private static final String DATA	= "gameData";
		private static final String ID      = "gameID";

		public Class cause;
		public boolean win;
		
		public HeroClass heroClass;
		public int armorTier;
		public int herolevel;
		public int depth;
		
		public Bundle gameData;
		public String gameID;

		public int score;

		public String desc(){
			if (cause == null) {
				return Messages.get(this, "something");
			} else {
				String result = Messages.get(cause, "rankings_desc", (Messages.get(cause, "name")));
				if (result.contains("!!!NO TEXT FOUND!!!")){
					return Messages.get(this, "something");
				} else {
					return result;
				}
			}
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			
			if (bundle.contains( CAUSE )) {
				cause   = bundle.getClass( CAUSE );
			} else {
				cause = null;
			}
			
			win		= bundle.getBoolean( WIN );
			score	= bundle.getInt( SCORE );
			
			heroClass	= HeroClass.restoreInBundle( bundle );
			armorTier	= bundle.getInt( TIER );
			
			if (bundle.contains(DATA))  gameData = bundle.getBundle(DATA);
			if (bundle.contains(ID))   gameID = bundle.getString(ID);
			
			if (gameID == null) gameID = UUID.randomUUID().toString();

			depth = bundle.getInt( DEPTH );
			herolevel = bundle.getInt( LEVEL );

		}
		
		@Override
		public void storeInBundle( Bundle bundle ) {
			
			if (cause != null) bundle.put( CAUSE, cause );

			bundle.put( WIN, win );
			bundle.put( SCORE, score );
			
			heroClass.storeInBundle( bundle );
			bundle.put( TIER, armorTier );
			bundle.put( LEVEL, herolevel );
			bundle.put( DEPTH, depth );
			
			if (gameData != null) bundle.put( DATA, gameData );
			bundle.put( ID, gameID );
		}
	}

	private static final Comparator<Record> scoreComparator = (lhs, rhs) -> {
        int result = (int)Math.signum( rhs.score - lhs.score );
        if (result == 0) {
            return (int)Math.signum( rhs.gameID.hashCode() - lhs.gameID.hashCode());
        } else{
            return result;
        }
    };
}
