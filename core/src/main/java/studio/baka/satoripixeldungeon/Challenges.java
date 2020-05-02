package studio.baka.satoripixeldungeon;

import studio.baka.satoripixeldungeon.items.Dewdrop;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.ClassArmor;
import studio.baka.satoripixeldungeon.items.armor.ClothArmor;
import studio.baka.satoripixeldungeon.items.artifacts.HornOfPlenty;
import studio.baka.satoripixeldungeon.items.food.Blandfruit;
import studio.baka.satoripixeldungeon.items.food.Food;
import studio.baka.satoripixeldungeon.items.food.SmallRation;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;

public class Challenges {

	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;

	public static final int MAX_VALUE           = 127;

	public static final String[] NAME_IDS = {
			"no_food",
			"no_armor",
			"no_healing",
			"no_herbalism",
			"swarm_intelligence",
			"darkness",
			"no_scrolls"
	};

	public static final int[] MASKS = {
			NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS
	};

	public static boolean isItemBlocked( Item item ){
		if (Dungeon.isChallenged(NO_FOOD)){
			if (item instanceof Food && !(item instanceof SmallRation)) {
				return true;
			} else if (item instanceof HornOfPlenty){
				return true;
			}
		}

		if (Dungeon.isChallenged(NO_ARMOR)){
			if (item instanceof Armor && !(item instanceof ClothArmor || item instanceof ClassArmor)) {
				return true;
			}
		}

		if (Dungeon.isChallenged(NO_HEALING)){
			if (item instanceof PotionOfHealing){
				return true;
			} else if (item instanceof Blandfruit
					&& ((Blandfruit) item).potionAttrib instanceof PotionOfHealing){
				return true;
			}
		}

		if (Dungeon.isChallenged(NO_HERBALISM)){
            return item instanceof Dewdrop;
		}

		return false;

	}

}