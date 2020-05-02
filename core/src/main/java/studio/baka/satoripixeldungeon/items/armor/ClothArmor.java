package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class ClothArmor extends Armor {

    {
        image = ItemSpriteSheet.ARMOR_CLOTH;

        bones = false; //Finding them in bones would be semi-frequent and disappointing.
    }

    public ClothArmor() {
        super(1);
    }

}
