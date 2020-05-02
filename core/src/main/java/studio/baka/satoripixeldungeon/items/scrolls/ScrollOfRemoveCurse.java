package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.EquipableItem;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRemoveCurse extends InventoryScroll {

    {
        initials = 7;
        mode = WndBag.Mode.UNCURSABLE;
    }

    @Override
    public void empoweredRead() {
        for (Item item : curUser.belongings) {
            if (item.cursed) {
                item.cursedKnown = true;
            }
        }
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();
        doRead();
    }

    @Override
    protected void onItemSelected(Item item) {
        new Flare(6, 32).show(curUser.sprite, 2f);

        boolean procced = uncurse(curUser, item);

        Weakness.detach(curUser, Weakness.class);

        if (procced) {
            GLog.p(Messages.get(this, "cleansed"));
        } else {
            GLog.i(Messages.get(this, "not_cleansed"));
        }
    }

    public static boolean uncurse(Hero hero, Item... items) {

        boolean procced = false;
        for (Item item : items) {
            if (item != null) {
                item.cursedKnown = true;
                if (item.cursed) {
                    procced = true;
                    item.cursed = false;
                }
            }
            if (item instanceof Weapon) {
                Weapon w = (Weapon) item;
                if (w.hasCurseEnchant()) {
                    w.enchant(null);
                    procced = true;
                }
            }
            if (item instanceof Armor) {
                Armor a = (Armor) item;
                if (a.hasCurseGlyph()) {
                    a.inscribe(null);
                    procced = true;
                }
            }
            if (item instanceof Wand) {
                ((Wand) item).updateLevel();
            }
        }

        if (procced) {
            hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
            hero.updateHT(false); //for ring of might
            updateQuickslot();
        }

        return procced;
    }

    public static boolean uncursable(Item item) {
        if ((item instanceof EquipableItem || item instanceof Wand) && (!item.isIdentified() || item.cursed)) {
            return true;
        } else if (item instanceof Weapon) {
            return ((Weapon) item).hasCurseEnchant();
        } else if (item instanceof Armor) {
            return ((Armor) item).hasCurseGlyph();
        } else {
            return false;
        }
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
