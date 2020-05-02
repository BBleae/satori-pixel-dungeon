package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;

public class ScrollOfUpgrade extends InventoryScroll {

    {
        initials = 12;
        mode = WndBag.Mode.UPGRADEABLE;
    }

    @Override
    protected void onItemSelected(Item item) {

        upgrade(curUser);

        //logic for telling the user when item properties change from upgrades
        //...yes this is rather messy
        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            boolean wasCursed = w.cursed;
            boolean hadCursedEnchant = w.hasCurseEnchant();
            boolean hadGoodEnchant = w.hasGoodEnchant();

            w.upgrade();

            if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()) {
                removeCurse(Dungeon.hero);
            } else if (w.cursedKnown && wasCursed && !w.cursed) {
                weakenCurse(Dungeon.hero);
            }
            if (hadGoodEnchant && !w.hasGoodEnchant()) {
                GLog.w(Messages.get(Weapon.class, "incompatible"));
            }

        } else if (item instanceof Armor) {
            Armor a = (Armor) item;
            boolean wasCursed = a.cursed;
            boolean hadCursedGlyph = a.hasCurseGlyph();
            boolean hadGoodGlyph = a.hasGoodGlyph();

            a.upgrade();

            if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()) {
                removeCurse(Dungeon.hero);
            } else if (a.cursedKnown && wasCursed && !a.cursed) {
                weakenCurse(Dungeon.hero);
            }
            if (hadGoodGlyph && !a.hasGoodGlyph()) {
                GLog.w(Messages.get(Armor.class, "incompatible"));
            }

        } else if (item instanceof Wand || item instanceof Ring) {
            boolean wasCursed = item.cursed;

            item.upgrade();

            if (wasCursed && !item.cursed) {
                removeCurse(Dungeon.hero);
            }

        } else {
            item.upgrade();
        }

        Badges.validateItemLevelAquired(item);
        Statistics.upgradesUsed++;
        Badges.validateMageUnlock();
    }

    public static void upgrade(Hero hero) {
        hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
    }

    public static void weakenCurse(Hero hero) {
        GLog.p(Messages.get(ScrollOfUpgrade.class, "weaken_curse"));
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 5);
    }

    public static void removeCurse(Hero hero) {
        GLog.p(Messages.get(ScrollOfUpgrade.class, "remove_curse"));
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
    }

    @Override
    public void empoweredRead() {
        //does nothing for now, this should never happen.
    }

    @Override
    public int price() {
        return isKnown() ? 50 * quantity : super.price();
    }
}
