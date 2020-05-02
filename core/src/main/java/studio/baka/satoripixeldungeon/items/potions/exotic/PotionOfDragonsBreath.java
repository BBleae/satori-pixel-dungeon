package studio.baka.satoripixeldungeon.items.potions.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.HashSet;

public class PotionOfDragonsBreath extends ExoticPotion {

    {
        initials = 6;
    }

    //a lot of this is copy-paste from wand of fireblast

    //the actual affected cells
    private HashSet<Integer> affectedCells;
    //the cells to trace fire shots to, for visual effects.
    private HashSet<Integer> visualCells;
    private int direction = 0;

    @Override
    //need to override drink so that time isn't spent right away
    protected void drink(final Hero hero) {
        curItem = detach(hero.belongings.backpack);
        setKnown();

        GameScene.selectCell(targeter);
    }

    private final CellSelector.Listener targeter = new CellSelector.Listener() {
        @Override
        public void onSelect(final Integer cell) {

            if (cell == null) {
                //TODO if this can ever be found un-IDed, need logic for that
                curItem.collect();
            } else {
                Sample.INSTANCE.play(Assets.SND_DRINK);
                curUser.sprite.operate(curUser.pos, () -> {

                    curUser.spend(1f);
                    curUser.sprite.idle();
                    curUser.sprite.zap(cell);

                    final Ballistica bolt
                            = new Ballistica(curUser.pos, cell, Ballistica.MAGIC_BOLT);

                    affectedCells = new HashSet<>();
                    visualCells = new HashSet<>();

                    int maxDist = 6;
                    int dist = Math.min(bolt.dist, maxDist);

                    for (int i = 0; i < PathFinder.CIRCLE8.length; i++) {
                        if (bolt.sourcePos + PathFinder.CIRCLE8[i] == bolt.path.get(1)) {
                            direction = i;
                            break;
                        }
                    }

                    float strength = maxDist;
                    for (int c : bolt.subPath(1, dist)) {
                        strength--; //as we start at dist 1, not 0.
                        affectedCells.add(c);
                        if (strength > 1) {
                            spreadFlames(c + PathFinder.CIRCLE8[left(direction)], strength - 1);
                            spreadFlames(c + PathFinder.CIRCLE8[direction], strength - 1);
                            spreadFlames(c + PathFinder.CIRCLE8[right(direction)], strength - 1);
                        } else {
                            visualCells.add(c);
                        }
                    }

                    //going to call this one manually
                    visualCells.remove(bolt.path.get(dist));

                    for (int c : visualCells) {
                        //this way we only get the cells at the tip, much better performance.
                        ((MagicMissile) curUser.sprite.parent.recycle(MagicMissile.class)).reset(
                                MagicMissile.FIRE_CONE,
                                curUser.sprite,
                                c,
                                null
                        );
                    }

                    MagicMissile.boltFromChar(curUser.sprite.parent,
                            MagicMissile.FIRE_CONE,
                            curUser.sprite,
                            bolt.path.get(dist / 2),
                            () -> {
                                for (int cell1 : affectedCells) {
                                    //ignore caster cell
                                    if (cell1 == bolt.sourcePos) {
                                        continue;
                                    }

                                    GameScene.add(Blob.seed(cell1, 5, Fire.class));

                                    Char ch = Actor.findChar(cell1);
                                    if (ch != null) {

                                        Buff.affect(ch, Burning.class).reignite(ch);
                                        Buff.affect(ch, Cripple.class, 5f);
                                        break;
                                    }
                                }
                            });

                });
            }
        }

        @Override
        public String prompt() {
            return Messages.get(PotionOfDragonsBreath.class, "prompt");
        }
    };

    //burn... BURNNNNN!.....
    private void spreadFlames(int cell, float strength) {
        if (strength >= 0 && (Dungeon.level.passable[cell] || Dungeon.level.flamable[cell])) {
            affectedCells.add(cell);
            if (strength >= 1.5f) {
                visualCells.remove(cell);
                spreadFlames(cell + PathFinder.CIRCLE8[left(direction)], strength - 1.5f);
                spreadFlames(cell + PathFinder.CIRCLE8[direction], strength - 1.5f);
                spreadFlames(cell + PathFinder.CIRCLE8[right(direction)], strength - 1.5f);
            } else {
                visualCells.add(cell);
            }
        } else if (!Dungeon.level.passable[cell])
            visualCells.add(cell);
    }

    private int left(int direction) {
        return direction == 0 ? 7 : direction - 1;
    }

    private int right(int direction) {
        return direction == 7 ? 0 : direction + 1;
    }

}
