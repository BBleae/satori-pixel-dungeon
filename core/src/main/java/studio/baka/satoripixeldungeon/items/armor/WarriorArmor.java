package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.PathFinder;

public class WarriorArmor extends ClassArmor {

    private static final int LEAP_TIME = 1;
    private static final int SHOCK_TIME = 5;

    {
        image = ItemSpriteSheet.ARMOR_WARRIOR;
    }

    @Override
    public void doSpecial() {
        GameScene.selectCell(leaper);
    }

    protected static CellSelector.Listener leaper = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target != null && target != curUser.pos) {

                Ballistica route = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
                int cell = route.collisionPos;

                //can't occupy the same cell as another char, so move back one.
                if (Actor.findChar(cell) != null && cell != curUser.pos)
                    cell = route.path.get(route.dist - 1);

                if (curUser.mana >= 20) {
                    curUser.mana -= 20;

                    final int dest = cell;
                    curUser.busy();
                    curUser.sprite.jump(curUser.pos, cell, () -> {
                        curUser.move(dest);
                        Dungeon.level.occupyCell(curUser);
                        Dungeon.observe();
                        GameScene.updateFog();

                        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                            Char mob = Actor.findChar(curUser.pos + PathFinder.NEIGHBOURS8[i]);
                            if (mob != null && mob != curUser && mob.alignment != Char.Alignment.ALLY) {
                                Buff.prolong(mob, Paralysis.class, SHOCK_TIME);
                            }
                        }

                        CellEmitter.center(dest).burst(Speck.factory(Speck.DUST), 10);
                        Camera.main.shake(2, 0.5f);

                        curUser.spendAndNext(LEAP_TIME);
                    });
                } else {
                    GLog.w(Messages.get(this, "not_enough_mana"), curUser.mana, curUser.getMaxmana(), 20);
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(WarriorArmor.class, "prompt");
        }
    };
}