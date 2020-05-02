package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HolyBomb extends Bomb {

    {
        image = ItemSpriteSheet.HOLY_BOMB;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);

        if (Dungeon.level.heroFOV[cell]) {
            new Flare(10, 64).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2f);
        }

        ArrayList<Char> affected = new ArrayList<>();

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char ch = Actor.findChar(i);
                if (ch != null) {
                    affected.add(ch);

                }
            }
        }

        for (Char ch : affected) {
            if (ch.properties().contains(Char.Property.UNDEAD) || ch.properties().contains(Char.Property.DEMONIC)) {
                ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);

                //bomb deals an additional 67% damage to unholy enemies in a 5x5 range
                int damage = Math.round(Random.NormalIntRange(Dungeon.depth + 5, 10 + Dungeon.depth * 2) * 0.67f);
                ch.damage(damage, this);
            }
        }

        Sample.INSTANCE.play(Assets.SND_READ);
    }

    @Override
    public int price() {
        //prices of ingredients
        return quantity * (20 + 30);
    }
}
