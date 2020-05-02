package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.BlastParticle;
import studio.baka.satoripixeldungeon.mechanics.ShadowCaster;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShrapnelBomb extends Bomb {

    {
        image = ItemSpriteSheet.SHRAPNEL_BOMB;
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        boolean[] FOV = new boolean[Dungeon.level.length()];
        Point c = Dungeon.level.cellToPoint(cell);
        ShadowCaster.castShadow(c.x, c.y, FOV, Dungeon.level.losBlocking, 8);

        ArrayList<Char> affected = new ArrayList<>();

        for (int i = 0; i < FOV.length; i++) {
            if (FOV[i]) {
                if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
                    //TODO better vfx?
                    CellEmitter.center(i).burst(BlastParticle.FACTORY, 5);
                }
                Char ch = Actor.findChar(i);
                if (ch != null) {
                    affected.add(ch);
                }
            }
        }

        for (Char ch : affected) {
            //regular bomb damage, which falls off at a rate of 5% per tile of distance
            int damage = Math.round(Random.NormalIntRange(Dungeon.depth + 5, 10 + Dungeon.depth * 2));
            damage = Math.round(damage * (1f - .05f * Dungeon.level.distance(cell, ch.pos)));
            damage -= ch.drRoll();
            ch.damage(damage, this);
            if (ch == Dungeon.hero && !ch.isAlive()) {
                Dungeon.fail(Bomb.class);
            }
        }
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 100);
    }
}
