package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.ShieldBuff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndItem;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class BrokenSeal extends Item {

    private static final String AC_AFFIX = "AFFIX";
    private static final String AC_TP = "TP";
    private static final String AC_DP = "DP";

    private static final int manaRequire = 6;

    //only to be used from the quickslot, for tutorial purposes mostly.
    public static final String AC_INFO = "INFO_WINDOW";

    {
        image = ItemSpriteSheet.SEAL;

        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;

        defaultAction = AC_TP;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_DROP);
        actions.add(AC_DP);
        actions.remove(AC_THROW);
        actions.add(AC_AFFIX);
        actions.add(AC_TP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        switch (action) {
            case AC_AFFIX:
                curItem = this;
                GameScene.selectItem(armorSelector, WndBag.Mode.ARMOR, Messages.get(this, "prompt"));
                break;
            case AC_INFO:
                GameScene.show(new WndItem(null, this, true));
                break;
            case AC_TP:
                curUser = hero;
                curItem = this;
                //GameScene.selectCell(shooter);
                if (hero.mana >= manaRequire) {
                    new ScrollOfTeleportation().doRead2();
                    hero.mana -= manaRequire;
                } else {
                    GLog.w(Messages.get(this, "not_enough_mana"), hero.mana, hero.getMaxmana(), manaRequire);
                }
                break;
            case AC_DP:
                if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
                    doDrop(hero);
                    hero.damage(9999, this);
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(BrokenSeal.class, "ondeath"));
                }
                break;
        }
    }

    @Override
    //scroll of upgrade can be used directly once, same as upgrading armor the seal is affixed to then removing it.
    public boolean isUpgradable() {
        return level() == 0;
    }

    protected static WndBag.Listener armorSelector = item -> {
        if (item instanceof Armor) {
            Armor armor = (Armor) item;
            if (!armor.levelKnown) {
                GLog.w(Messages.get(BrokenSeal.class, "unknown_armor"));
            } else if (armor.cursed || armor.level() < 0) {
                GLog.w(Messages.get(BrokenSeal.class, "degraded_armor"));
            } else {
                GLog.p(Messages.get(BrokenSeal.class, "affix"));
                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                Sample.INSTANCE.play(Assets.SND_UNLOCK);
                armor.affixSeal((BrokenSeal) curItem);
                curItem.detach(Dungeon.hero.belongings.backpack);
            }
        }
    };

    public static class WarriorShield extends ShieldBuff {

        private Armor armor;
        private float partialShield;

        @Override
        public synchronized boolean act() {
            if (shielding() < maxShield()) {
                partialShield += 1 / 30f;
            }

            while (partialShield >= 1) {
                incShield();
                partialShield--;
            }

            if (shielding() <= 0 && maxShield() <= 0) {
                detach();
            }

            spend(TICK);
            return true;
        }

        public synchronized void supercharge(int maxShield) {
            if (maxShield > shielding()) {
                setShield(maxShield);
            }
        }

        public synchronized void setArmor(Armor arm) {
            armor = arm;
        }

        public synchronized int maxShield() {
            if (armor != null && armor.isEquipped((Hero) target)) {
                return 1 + armor.tier + armor.level();
            } else {
                return 0;
            }
        }

        @Override
        //logic edited slightly as buff should not detach
        public int absorbDamage(int dmg) {
            if (shielding() >= dmg) {
                decShield(dmg);
                dmg = 0;
            } else {
                dmg -= shielding();
                setShield(0);
            }
            return dmg;
        }
    }
}
