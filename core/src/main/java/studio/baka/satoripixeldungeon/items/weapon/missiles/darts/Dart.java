package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.MagicImmune;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.weapon.melee.Crossbow;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndOptions;

import java.util.ArrayList;

public class Dart extends MissileWeapon {

    {
        image = ItemSpriteSheet.DART;

        tier = 1;

        //infinite, even with penalties
        baseUses = 1000;
    }

    protected static final String AC_TIP = "TIP";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TIP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_TIP)) {
            GameScene.selectItem(itemSelector, WndBag.Mode.SEED, "select a seed");
        }

        super.execute(hero, action);
    }

    @Override
    public int min(int lvl) {
        if (bow != null) {
            return 4 +                 //4 base
                    bow.level() + lvl;  //+1 per level or bow level
        } else {
            return 1 +     //1 base, down from 2
                    lvl;    //scaling unchanged
        }
    }

    @Override
    public int max(int lvl) {
        if (bow != null) {
            return 12 +                    //12 base
                    3 * bow.level() + 2 * lvl;  //+3 per bow level, +2 per level (default scaling +2)
        } else {
            return 2 +     //2 base, down from 5
                    2 * lvl;  //scaling unchanged
        }
    }

    private static Crossbow bow;

    private void updateCrossbow() {
        if (Dungeon.hero.belongings.weapon instanceof Crossbow) {
            bow = (Crossbow) Dungeon.hero.belongings.weapon;
        } else {
            bow = null;
        }
    }

    @Override
    public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
        if (bow != null && bow.hasEnchant(type, owner)) {
            return true;
        } else {
            return super.hasEnchant(type, owner);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (bow != null && bow.enchantment != null && attacker.buff(MagicImmune.class) == null) {
            level(bow.level());
            damage = bow.enchantment.proc(this, attacker, defender, damage);
            level(0);
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    protected void onThrow(int cell) {
        updateCrossbow();
        super.onThrow(cell);
    }

    @Override
    public String info() {
        updateCrossbow();
        return super.info();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int price() {
        return super.price() / 2; //half normal value
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener() {

        @Override
        public void onSelect(final Item item) {

            if (item == null) return;

            final int maxToTip = Math.min(curItem.quantity(), item.quantity() * 2);
            final int maxSeedsToUse = (maxToTip + 1) / 2;

            final int singleSeedDarts;

            final String[] options;

            if (curItem.quantity() == 1) {
                singleSeedDarts = 1;
                options = new String[]{
                        Messages.get(Dart.class, "tip_one"),
                        Messages.get(Dart.class, "tip_cancel")};
            } else {
                singleSeedDarts = 2;
                if (maxToTip <= 2) {
                    options = new String[]{
                            Messages.get(Dart.class, "tip_two"),
                            Messages.get(Dart.class, "tip_cancel")};
                } else {
                    options = new String[]{
                            Messages.get(Dart.class, "tip_all", maxToTip, maxSeedsToUse),
                            Messages.get(Dart.class, "tip_two"),
                            Messages.get(Dart.class, "tip_cancel")};
                }
            }

            TippedDart tipResult = TippedDart.getTipped((Plant.Seed) item, 1);

            GameScene.show(new WndOptions(Messages.get(Dart.class, "tip_title"),
                    Messages.get(Dart.class, "tip_desc", tipResult.name()) + "\n\n" + tipResult.desc(),
                    options) {

                @Override
                protected void onSelect(int index) {
                    super.onSelect(index);

                    if (index == 0 && options.length == 3) {
                        if (item.quantity() <= maxSeedsToUse) {
                            item.detachAll(curUser.belongings.backpack);
                        } else {
                            item.quantity(item.quantity() - maxSeedsToUse);
                        }

                        if (maxToTip < curItem.quantity()) {
                            curItem.quantity(curItem.quantity() - maxToTip);
                        } else {
                            curItem.detachAll(curUser.belongings.backpack);
                        }

                        TippedDart newDart = TippedDart.getTipped((Plant.Seed) item, maxToTip);
                        if (!newDart.collect()) Dungeon.level.drop(newDart, curUser.pos).sprite.drop();

                        curUser.spend(1f);
                        curUser.busy();
                        curUser.sprite.operate(curUser.pos);

                    } else if ((index == 1 && options.length == 3) || (index == 0 && options.length == 2)) {
                        item.detach(curUser.belongings.backpack);

                        if (curItem.quantity() <= singleSeedDarts) {
                            curItem.detachAll(curUser.belongings.backpack);
                        } else {
                            curItem.quantity(curItem.quantity() - singleSeedDarts);
                        }

                        TippedDart newDart = TippedDart.getTipped((Plant.Seed) item, singleSeedDarts);
                        if (!newDart.collect()) Dungeon.level.drop(newDart, curUser.pos).sprite.drop();

                        curUser.spend(1f);
                        curUser.busy();
                        curUser.sprite.operate(curUser.pos);
                    }
                }
            });

        }

    };
}
