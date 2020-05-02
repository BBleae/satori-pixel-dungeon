package studio.baka.satoripixeldungeon;

import studio.baka.satoripixeldungeon.items.Item;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;

public class QuickSlot {

	/**
	 * Slots contain objects which are also in a player's inventory. The one exception to this is when quantity is 0,
	 * which can happen for a stackable item that has been 'used up', these are refered to a placeholders.
	 */

	//note that the current max size is coded at 4, due to UI constraints, but it could be much much bigger with no issue.
	public static int SIZE = 4;
	private Item[] slots = new Item[SIZE];
	private String[] slotsAction = new String[SIZE];

	//direct array interaction methods, everything should build from these methods.
	public void setSlot(int slot, Item item){
		/*
		clearItem(item); //we don't want to allow the same item in multiple slots.
		slots[slot] = item;
		 */
		setSlot(slot,item,item.defaultAction);
	}

	public void setSlot(int slot, Item item, String action){
		clearItem(item,action); //we don't want to allow the same action in multiple slots.
		slots[slot] = item;
		if(action == null) action = "";
		if(!action.equals(""))
			slotsAction[slot] = action;
		else if (item.defaultAction != null)
			slotsAction[slot] = item.defaultAction;
		else
			slotsAction[slot] = "THROW";
	}
/*
	public void clearSlot(int slot){
		slots[slot] = null;
	}

	public void reset(){
		slots = new Item[SIZE];
	}
*/
	public void clearSlot(int slot){
		slots[slot] = null;
		slotsAction[slot] = null;
	}

	public void reset(){
		slots = new Item[SIZE];
		slotsAction = new String[SIZE];
	}

	public Item getItem(int slot){
		return slots[slot];
	}


	public String getAction(int slot){
	    if (slotsAction[slot] != null)
	        return slotsAction[slot];
	    else
	        return "";
	}

	public String[] getAction(Item item){
		ArrayList<String> tmpaction = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			if (getItem(i) == item && !slotsAction[i].equals("")){
				tmpaction.add(slotsAction[i]);
			}
		}

		String[] result = new String[tmpaction.size()];
		int j = 0;
        for (String i : tmpaction) {
            result[j] = i;
            j++;
        }
		return result;
	}

	//utility methods, for easier use of the internal array.
	public int getSlot(Item item) {
		for (int i = 0; i < SIZE; i++)
			if (getItem(i) == item)
				return i;
		return -1;
	}

	public boolean[] getSlotSet(Item item) {
		boolean[] itempos = new boolean[SIZE];
		for (int i = 0; i < SIZE; i++)
			if (getItem(i) == item)
				itempos[i] = true;
		return itempos;				//如果没有找到物品，理应返回[0,0,0,0]
	}

	public Boolean isPlaceholder(int slot){
		return getItem(slot) != null && getItem(slot).quantity() == 0;
	}

	public Boolean isNonePlaceholder(int slot){
		return getItem(slot) != null && getItem(slot).quantity() > 0;
	}

	public void clearItem(Item item){
		if (contains(item))
			clearSlot(getSlot(item));
	}

	public void clearItem(Item item, String action){
		if (item != null && contains(item))
		{
			if (getAction(getSlot(item)).equals(action))
				clearSlot(getSlot(item));
		}
	}

	public boolean contains(Item item){
		return getSlot(item) != -1;
	}

	public void replacePlaceholder(Item item){
		for (int i = 0; i < SIZE; i++)
			if (isPlaceholder(i) && item.isSimilar(getItem(i)))
				setSlot( i , item );
	}

	public void convertToPlaceholder(Item item){
		/*
		if (contains(item)) {
			Item placeholder = item.virtual();
			if (placeholder == null) return;
			
			for (int i = 0; i < SIZE; i++) {
				if (getItem(i) == item) setSlot(i, placeholder);
			}
		}
		*/
		Item placeholder = Item.virtual(item.getClass());

		if (placeholder != null && contains(item))
			for (int i = 0; i < SIZE; i++)
				if (getItem(i) == item)
					setSlot( i , placeholder ,"");
	}

	public Item randomNonePlaceholder(){
		ArrayList<Item> result = new ArrayList<>();
		for (int i = 0; i < SIZE; i ++)
			if (getItem(i) != null && !isPlaceholder(i))
				result.add(getItem(i));

		return Random.element(result);
	}

	private final String PLACEHOLDERS = "placeholders";
	private final String ACTIONHOLDERS = "actionholders";
	private final String PLACEMENTS = "placements";

	/**
	 * Placements array is used as order is preserved while bundling, but exact index is not, so if we
	 * bundle both the placeholders (which preserves their order) and an array telling us where the placeholders are,
	 * we can reconstruct them perfectly.
	 */

	public void storePlaceholders(Bundle bundle){
		/*
		ArrayList<Item> placeholders = new ArrayList<>(SIZE);
		boolean[] placements = new boolean[SIZE];

		for (int i = 0; i < SIZE; i++)
			if (isPlaceholder(i)) {
				placeholders.add(getItem(i));
				placements[i] = true;
			}
		bundle.put( PLACEHOLDERS, placeholders );
		bundle.put( PLACEMENTS, placements );
		 */

		ArrayList<Item> placeholders = new ArrayList<>(SIZE);
		String[] actionholders = new String[SIZE];
		boolean[] placements = new boolean[SIZE];
		for (int i = 0; i < SIZE; i++) { actionholders[i] = ""; }

		for (int i = 0; i < SIZE; i++) {
			if (isPlaceholder(i)) {
				placeholders.add(getItem(i));
				actionholders[i] = getAction(i);
				placements[i] = true;
			}
		}
		bundle.put( PLACEHOLDERS, placeholders );
		bundle.put( ACTIONHOLDERS, actionholders );
		bundle.put( PLACEMENTS, placements );
	}

	public void restorePlaceholders(Bundle bundle){
		Collection<Bundlable> placeholders = bundle.getCollection(PLACEHOLDERS);
		String[] actionholders = bundle.getStringArray(ACTIONHOLDERS);
		boolean[] placements = bundle.getBooleanArray( PLACEMENTS );

		int i = 0;
		for (Bundlable item : placeholders){
			while (!placements[i]) i++;
			setSlot( i, (Item)item, actionholders[i] );
			i++;
		}

	}

}
