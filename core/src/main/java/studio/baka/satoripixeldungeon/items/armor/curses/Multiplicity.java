package studio.baka.satoripixeldungeon.items.armor.curses;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.PinCushion;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mimic;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.Statue;
import studio.baka.satoripixeldungeon.actors.mobs.Thief;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.MirrorImage;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Multiplicity extends Armor.Glyph {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (Random.Int(20) == 0) {
            ArrayList<Integer> spawnPoints = new ArrayList<>();

            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = defender.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                    spawnPoints.add(p);
                }
            }

            if (spawnPoints.size() > 0) {

                Mob m = null;
                if (Random.Int(2) == 0 && defender instanceof Hero) {
                    m = new MirrorImage();
                    ((MirrorImage) m).duplicate((Hero) defender);

                } else {
                    //FIXME should probably have a mob property for this
                    if (attacker.properties().contains(Char.Property.BOSS) || attacker.properties().contains(Char.Property.MINIBOSS)
                            || attacker instanceof Mimic || attacker instanceof Statue) {
                        m = Dungeon.level.createMob();
                    } else {
                        Actor.fixTime();

                        m = (Mob) Reflection.newInstance(attacker.getClass());

                        if (m != null) {

                            Bundle store = new Bundle();
                            attacker.storeInBundle(store);
                            m.restoreFromBundle(store);
                            m.pos = 0;
                            m.HP = m.HT;
                            if (m.buff(PinCushion.class) != null) {
                                m.remove(m.buff(PinCushion.class));
                            }

                            //If a thief has stolen an item, that item is not duplicated.
                            if (m instanceof Thief) {
                                ((Thief) m).item = null;
                            }
                        }
                    }
                }

                if (m != null) {
                    GameScene.add(m);
                    ScrollOfTeleportation.appear(m, Random.element(spawnPoints));
                }

            }
        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}
