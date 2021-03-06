package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.mobs.Golem;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.Monk;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.quest.DwarfToken;
import studio.baka.satoripixeldungeon.items.rings.Ring;
import studio.baka.satoripixeldungeon.journal.Notes;
import studio.baka.satoripixeldungeon.levels.CityLevel;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ImpSprite;
import studio.baka.satoripixeldungeon.windows.WndImp;
import studio.baka.satoripixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Imp extends NPC {

    {
        spriteClass = ImpSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    private boolean seenBefore = false;

    @Override
    protected boolean act() {

        if (!Quest.given && Dungeon.level.heroFOV[pos]) {
            if (!seenBefore) {
                yell(Messages.get(this, "hey", Dungeon.hero.givenName()));
            }
            seenBefore = true;
        } else {
            seenBefore = false;
        }

        throwItem();

        return super.act();
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

    @Override
    public boolean interact() {

        sprite.turnTo(pos, Dungeon.hero.pos);
        if (Quest.given) {

            DwarfToken tokens = Dungeon.hero.belongings.getItem(DwarfToken.class);
            if (tokens != null && (tokens.quantity() >= 8 || (!Quest.alternative && tokens.quantity() >= 6))) {
                Game.runOnRenderThread(() -> GameScene.show(new WndImp(Imp.this, tokens)));
            } else {
                tell(Quest.alternative ?
                        Messages.get(this, "monks_2", Dungeon.hero.givenName())
                        : Messages.get(this, "golems_2", Dungeon.hero.givenName()));
            }

        } else {
            tell(Quest.alternative ? Messages.get(this, "monks_1") : Messages.get(this, "golems_1"));
            Quest.given = true;
            Quest.completed = false;

            Notes.add(Notes.Landmark.IMP);
        }

        return false;
    }

    private void tell(String text) {
        Game.runOnRenderThread(() -> GameScene.show(new WndQuest(Imp.this, text)));
    }

    public void flee() {

        yell(Messages.get(this, "cya", Dungeon.hero.givenName()));

        destroy();
        sprite.die();
    }

    public static class Quest {

        private static boolean alternative;

        private static boolean spawned;
        private static boolean given;
        private static boolean completed;

        public static Ring reward;

        public static void reset() {
            spawned = false;

            reward = null;
        }

        private static final String NODE = "demon";

        private static final String ALTERNATIVE = "alternative";
        private static final String SPAWNED = "spawned";
        private static final String GIVEN = "given";
        private static final String COMPLETED = "completed";
        private static final String REWARD = "reward";

        public static void storeInBundle(Bundle bundle) {

            Bundle node = new Bundle();

            node.put(SPAWNED, spawned);

            if (spawned) {
                node.put(ALTERNATIVE, alternative);

                node.put(GIVEN, given);
                node.put(COMPLETED, completed);
                node.put(REWARD, reward);
            }

            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {

            Bundle node = bundle.getBundle(NODE);

            if (node.notNull() && (spawned = node.getBoolean(SPAWNED))) {
                alternative = node.getBoolean(ALTERNATIVE);

                given = node.getBoolean(GIVEN);
                completed = node.getBoolean(COMPLETED);
                reward = (Ring) node.get(REWARD);
            }
        }

        public static void spawn(CityLevel level) {
            if (!spawned && Dungeon.depth > 16 && Random.Int(20 - Dungeon.depth) == 0) {

                Imp npc = new Imp();
                do {
                    npc.pos = level.randomRespawnCell();
                } while (
                        npc.pos == -1 ||
                                level.heaps.get(npc.pos) != null ||
                                level.traps.get(npc.pos) != null ||
                                level.findMob(npc.pos) != null ||
                                //The imp doesn't move, so he cannot obstruct a passageway
                                !(level.passable[npc.pos + PathFinder.CIRCLE4[0]] && level.passable[npc.pos + PathFinder.CIRCLE4[2]]) ||
                                !(level.passable[npc.pos + PathFinder.CIRCLE4[1]] && level.passable[npc.pos + PathFinder.CIRCLE4[3]]));
                level.mobs.add(npc);

                spawned = true;
                alternative = Random.Int(2) == 0;

                given = false;

                do {
                    reward = (Ring) Generator.random(Generator.Category.RING);
                } while (reward.cursed);
                reward.upgrade(2);
                reward.cursed = true;
            }
        }

        public static void process(Mob mob) {
            if (spawned && given && !completed) {
                if ((alternative && mob instanceof Monk) ||
                        (!alternative && mob instanceof Golem)) {

                    Dungeon.level.drop(new DwarfToken(), mob.pos).sprite.drop();
                }
            }
        }

        public static void complete() {
            reward = null;
            completed = true;

            Notes.remove(Notes.Landmark.IMP);
        }

        public static boolean isCompleted() {
            return completed;
        }
    }
}
