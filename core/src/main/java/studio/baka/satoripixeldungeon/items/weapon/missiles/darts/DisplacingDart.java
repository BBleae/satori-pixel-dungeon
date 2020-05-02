package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplacingDart extends TippedDart {

    {
        image = ItemSpriteSheet.DISPLACING_DART;
    }

    int distance = 8;

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        if (!defender.properties().contains(Char.Property.IMMOVABLE)) {

            int startDist = Dungeon.level.distance(attacker.pos, defender.pos);

            HashMap<Integer, ArrayList<Integer>> positions = new HashMap<>();

            for (int pos = 0; pos < Dungeon.level.length(); pos++) {
                if (Dungeon.level.heroFOV[pos]
                        && Dungeon.level.passable[pos]
                        && Actor.findChar(pos) == null) {

                    int dist = Dungeon.level.distance(attacker.pos, pos);
                    if (dist > startDist) {
                        positions.computeIfAbsent(dist, k -> new ArrayList<>());
                        positions.get(dist).add(pos);
                    }

                }
            }

            float[] probs = new float[distance + 1];

            for (int i = 0; i <= distance; i++) {
                if (positions.get(i) != null) {
                    probs[i] = i - startDist;
                }
            }

            int chosenDist = Random.chances(probs);

            if (chosenDist != -1) {
                int pos = positions.get(chosenDist).get(Random.index(positions.get(chosenDist)));
                ScrollOfTeleportation.appear(defender, pos);
                Dungeon.level.occupyCell(defender);
            }

        }

        return super.proc(attacker, defender, damage);
    }
}
