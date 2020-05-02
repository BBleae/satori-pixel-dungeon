package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Lightning;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShockingDart extends TippedDart {

    {
        image = ItemSpriteSheet.SHOCKING_DART;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        defender.damage(Random.NormalIntRange(8, 12), this);

        CharSprite s = defender.sprite;
        if (s != null && s.parent != null) {
            ArrayList<Lightning.Arc> arcs = new ArrayList<>();
            arcs.add(new Lightning.Arc(new PointF(s.x, s.y + s.height / 2), new PointF(s.x + s.width, s.y + s.height / 2)));
            arcs.add(new Lightning.Arc(new PointF(s.x + s.width / 2, s.y), new PointF(s.x + s.width / 2, s.y + s.height)));
            s.parent.add(new Lightning(arcs, null));
            Sample.INSTANCE.play(Assets.SND_LIGHTNING);
        }

        return super.proc(attacker, defender, damage);
    }
}
