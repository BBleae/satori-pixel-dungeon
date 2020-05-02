package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.quest.MetalShard;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.SpiritBow;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

public class CurseInfusion extends InventorySpell {

    {
        image = ItemSpriteSheet.CURSE_INFUSE;
        mode = WndBag.Mode.CURSABLE;
    }

    @Override
    protected void onItemSelected(Item item) {

        CellEmitter.get(curUser.pos).burst(ShadowParticle.UP, 5);
        Sample.INSTANCE.play(Assets.SND_CURSED);

        item.cursed = true;
        if (item instanceof MeleeWeapon || item instanceof SpiritBow) {
            Weapon w = (Weapon) item;
            if (w.enchantment != null) {
                w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
            } else {
                w.enchant(Weapon.Enchantment.randomCurse());
            }
            w.curseInfusionBonus = true;
            if (w instanceof MagesStaff) {
                ((MagesStaff) w).updateWand(true);
            }
        } else if (item instanceof Armor) {
            Armor a = (Armor) item;
            if (a.glyph != null) {
                a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
            } else {
                a.inscribe(Armor.Glyph.randomCurse());
            }
            a.curseInfusionBonus = true;
        } else if (item instanceof Wand) {
            ((Wand) item).curseInfusionBonus = true;
            ((Wand) item).updateLevel();
        }
        updateQuickslot();
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((30 + 100) / 3f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfRemoveCurse.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 4;

            output = CurseInfusion.class;
            outQuantity = 3;
        }

    }
}
