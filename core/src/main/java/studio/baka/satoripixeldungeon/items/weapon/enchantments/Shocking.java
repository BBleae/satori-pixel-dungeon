package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Lightning;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Shocking extends Weapon.Enchantment {

    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF, 0.5f);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        int level = Math.max(0, weapon.level());

        if (Random.Int(level + 3) >= 2) {

            affected.clear();

            arcs.clear();
            arc(attacker, defender, 2);

            affected.remove(defender); //defender isn't hurt by lightning
            for (Char ch : affected) {
                ch.damage(Math.round(damage * 0.4f), this);
            }

            attacker.sprite.parent.addToFront(new Lightning(arcs, null));
            Sample.INSTANCE.play(Assets.SND_LIGHTNING);

        }

        return damage;

    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }

    private final ArrayList<Char> affected = new ArrayList<>();

    private final ArrayList<Lightning.Arc> arcs = new ArrayList<>();

    private void arc(Char attacker, Char defender, int dist) {

        affected.add(defender);

        defender.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
        defender.sprite.flash();

        PathFinder.buildDistanceMap(defender.pos, BArray.not(Dungeon.level.solid, null), dist);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n != null && n != attacker && !affected.contains(n)) {
                    arcs.add(new Lightning.Arc(defender.sprite.center(), n.sprite.center()));
                    arc(attacker, n, (Dungeon.level.water[n.pos] && !n.flying) ? 2 : 1);
                }
            }
        }
    }
}
