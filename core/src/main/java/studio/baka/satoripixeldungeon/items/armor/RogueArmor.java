package studio.baka.satoripixeldungeon.items.armor;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Doom;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class RogueArmor extends ClassArmor {

    {
        image = ItemSpriteSheet.ARMOR_ROGUE;
    }

    @Override
    public void doSpecial() {
        GameScene.selectCell(teleporter);
    }

    protected static CellSelector.Listener teleporter = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target != null) {

                PathFinder.buildDistanceMap(curUser.pos, Dungeon.level.passable, 100);

                if (PathFinder.distance[target] == Integer.MAX_VALUE ||
                        !Dungeon.level.heroFOV[target] ||
                        !(Dungeon.level.passable[target] || Dungeon.level.avoid[target]) ||
                        Actor.findChar(target) != null) {

                    GLog.w(Messages.get(RogueArmor.class, "fov"));
                    return;
                }

                if (curUser.mana >= 20) {
                    curUser.mana -= 20;

                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                        if (Dungeon.level.heroFOV[mob.pos] && mob.alignment != Char.Alignment.ALLY) {
                            Buff.prolong(mob, Blindness.class, 5f);
                            Buff.prolong(mob, Terror.class, 5f);
                            Buff.affect(mob, Doom.class);
                            if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
                            mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4);
                        }
                    }

                    ScrollOfTeleportation.appear(curUser, target);
                    CellEmitter.get(target).burst(Speck.factory(Speck.WOOL), 10);
                    Sample.INSTANCE.play(Assets.SND_PUFF);
                    Dungeon.level.occupyCell(curUser);
                    Dungeon.observe();
                    GameScene.updateFog();

                    curUser.spendAndNext(Actor.TICK);
                } else {
                    GLog.w(Messages.get(this, "not_enough_mana"), curUser.mana, curUser.getMaxmana(), 20);
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(RogueArmor.class, "prompt");
        }
    };
}