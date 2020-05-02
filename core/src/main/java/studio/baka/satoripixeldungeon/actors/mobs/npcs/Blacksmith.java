package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.items.BrokenSeal;
import studio.baka.satoripixeldungeon.items.EquipableItem;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.quest.DarkGold;
import studio.baka.satoripixeldungeon.items.quest.Pickaxe;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfUpgrade;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.levels.rooms.Room;
import studio.baka.satoripixeldungeon.levels.rooms.standard.BlacksmithRoom;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.BlacksmithSprite;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBlacksmith;
import studio.baka.satoripixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Blacksmith extends NPC {

    {
        spriteClass = BlacksmithSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    protected boolean act() {
        throwItem();
        return super.act();
    }

    @Override
    public boolean interact() {

        sprite.turnTo(pos, Dungeon.hero.pos);

        if (!Quest.given) {

            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndQuest(Blacksmith.this,
                            Quest.alternative ? Messages.get(Blacksmith.this, "blood_1") : Messages.get(Blacksmith.this, "gold_1")) {

                        @Override
                        public void onBackPressed() {
                            super.onBackPressed();

                            Quest.given = true;
                            Quest.completed = false;

                            Pickaxe pick = new Pickaxe();
                            if (pick.doPickUp(Dungeon.hero)) {
                                GLog.i(Messages.get(Dungeon.hero, "you_now_have", pick.name()));
                            } else {
                                Dungeon.level.drop(pick, Dungeon.hero.pos).sprite.drop();
                            }
                        }
                    });
                }
            });

            Notes.add(Notes.Landmark.TROLL);

        } else if (!Quest.completed) {
            if (Quest.alternative) {

                Pickaxe pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
                if (pick == null) {
                    tell(Messages.get(this, "lost_pick"));
                } else if (!pick.bloodStained) {
                    tell(Messages.get(this, "blood_2"));
                } else {
                    if (pick.isEquipped(Dungeon.hero)) {
                        pick.doUnequip(Dungeon.hero, false);
                    }
                    pick.detach(Dungeon.hero.belongings.backpack);
                    tell(Messages.get(this, "completed"));

                    Quest.completed = true;
                    Quest.reforged = false;
                }

            } else {

                Pickaxe pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
                DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
                if (pick == null) {
                    tell(Messages.get(this, "lost_pick"));
                } else if (gold == null || gold.quantity() < 15) {
                    tell(Messages.get(this, "gold_2"));
                } else {
                    if (pick.isEquipped(Dungeon.hero)) {
                        pick.doUnequip(Dungeon.hero, false);
                    }
                    pick.detach(Dungeon.hero.belongings.backpack);
                    gold.detachAll(Dungeon.hero.belongings.backpack);
                    tell(Messages.get(this, "completed"));

                    Quest.completed = true;
                    Quest.reforged = false;
                }

            }
        } else if (!Quest.reforged) {

            Game.runOnRenderThread(() -> GameScene.show(new WndBlacksmith(Blacksmith.this, Dungeon.hero)));

        } else {

            tell(Messages.get(this, "get_lost"));

        }

        return false;
    }

    private void tell(String text) {
        Game.runOnRenderThread(() -> GameScene.show(new WndQuest(Blacksmith.this, text)));
    }

    public static String verify(Item item1, Item item2) {

        if (item1 == item2 && (item1.quantity() == 1 && item2.quantity() == 1)) {
            return Messages.get(Blacksmith.class, "same_item");
        }

        if (item1.getClass() != item2.getClass()) {
            return Messages.get(Blacksmith.class, "diff_type");
        }

        if (!item1.isIdentified() || !item2.isIdentified()) {
            return Messages.get(Blacksmith.class, "un_ided");
        }

        if (item1.cursed || item2.cursed) {
            return Messages.get(Blacksmith.class, "cursed");
        }

        if (item1.level() < 0 || item2.level() < 0) {
            return Messages.get(Blacksmith.class, "degraded");
        }

        if (!item1.isUpgradable() || !item2.isUpgradable()) {
            return Messages.get(Blacksmith.class, "cant_reforge");
        }

        return null;
    }

    public static void upgrade(Item item1, Item item2) {

        Item first, second;
        if (item2.level() > item1.level()) {
            first = item2;
            second = item1;
        } else {
            first = item1;
            second = item2;
        }

        Sample.INSTANCE.play(Assets.SND_EVOKE);
        ScrollOfUpgrade.upgrade(Dungeon.hero);
        Item.evoke(Dungeon.hero);

        if (first.isEquipped(Dungeon.hero)) {
            ((EquipableItem) first).doUnequip(Dungeon.hero, true);
        }
        if (first instanceof MissileWeapon && first.quantity() > 1) {
            first = first.split(1);
        }
        first.level(first.level() + 1); //prevents on-upgrade effects like enchant/glyph removal
        if (first instanceof MissileWeapon && Dungeon.hero.belongings.notContains(first)) {
            if (!first.collect()) {
                Dungeon.level.drop(first, Dungeon.hero.pos);
            }
        }
        Dungeon.hero.spendAndNext(2f);
        Badges.validateItemLevelAquired(first);

        if (second.isEquipped(Dungeon.hero)) {
            ((EquipableItem) second).doUnequip(Dungeon.hero, false);
        }
        second.detach(Dungeon.hero.belongings.backpack);

        if (second instanceof Armor) {
            BrokenSeal seal = ((Armor) second).checkSeal();
            if (seal != null) {
                Dungeon.level.drop(seal, Dungeon.hero.pos);
            }
        }

        Quest.reforged = true;

        Notes.remove(Notes.Landmark.TROLL);
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 100_000_000;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public void add(Buff buff) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    public static class Quest {

        private static boolean spawned;

        private static boolean alternative;
        private static boolean given;
        private static boolean completed;
        private static boolean reforged;

        public static void reset() {
            spawned = false;
            given = false;
            completed = false;
            reforged = false;
        }

        private static final String NODE = "blacksmith";

        private static final String SPAWNED = "spawned";
        private static final String ALTERNATIVE = "alternative";
        private static final String GIVEN = "given";
        private static final String COMPLETED = "completed";
        private static final String REFORGED = "reforged";

        public static void storeInBundle(Bundle bundle) {

            Bundle node = new Bundle();

            node.put(SPAWNED, spawned);

            if (spawned) {
                node.put(ALTERNATIVE, alternative);
                node.put(GIVEN, given);
                node.put(COMPLETED, completed);
                node.put(REFORGED, reforged);
            }

            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {

            Bundle node = bundle.getBundle(NODE);

            if (node.notNull() && (spawned = node.getBoolean(SPAWNED))) {
                alternative = node.getBoolean(ALTERNATIVE);
                given = node.getBoolean(GIVEN);
                completed = node.getBoolean(COMPLETED);
                reforged = node.getBoolean(REFORGED);
            } else {
                reset();
            }
        }

        public static ArrayList<Room> spawn(ArrayList<Room> rooms) {
            if (!spawned && Dungeon.depth > 11 && Random.Int(15 - Dungeon.depth) == 0) {

                rooms.add(new BlacksmithRoom());
                spawned = true;
                alternative = Random.Int(2) == 0;

                given = false;

            }
            return rooms;
        }
    }
}
