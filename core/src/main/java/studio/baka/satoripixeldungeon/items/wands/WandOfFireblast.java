package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Blazing;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;
import java.util.HashSet;

public class WandOfFireblast extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_FIREBOLT;

        collisionProperties = Ballistica.STOP_TERRAIN;
    }

    //1x/2x/3x damage
    public int min(int lvl) {
        return (1 + lvl) * chargesPerCast();
    }

    //1x/2x/3x damage
    public int max(int lvl) {
        return (6 + 2 * lvl) * chargesPerCast();
    }

    //the actual affected cells
    private HashSet<Integer> affectedCells;
    //the cells to trace fire shots to, for visual effects.
    private HashSet<Integer> visualCells;
    private int direction = 0;

    public void zap2(Ballistica bolt) {
        fx(bolt, () -> onZap(bolt));
    }

    @Override
    protected void onZap(Ballistica bolt) {

        ArrayList<Char> affectedChars = new ArrayList<>();
        for (int cell : affectedCells) {

            //ignore caster cell
            if (cell == bolt.sourcePos) {
                continue;
            }

            //only ignite cells directly near caster if they are flammable
            if (!Dungeon.level.adjacent(bolt.sourcePos, cell)
                    || Dungeon.level.flamable[cell]) {
                GameScene.add(Blob.seed(cell, 1 + chargesPerCast(), Fire.class));
            }

            Char ch = Actor.findChar(cell);
            if (ch != null) {
                affectedChars.add(ch);
            }
        }

        for (Char ch : affectedChars) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(), this);
            Buff.affect(ch, Burning.class).reignite(ch);
            switch (chargesPerCast()) {
                case 1:
                    break; //no effects
                case 2:
                    Buff.affect(ch, Cripple.class, 4f);
                    break;
                case 3:
                    Buff.affect(ch, Paralysis.class, 4f);
                    break;
            }
        }
    }

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

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        //acts like blazing enchantment
        new Blazing().proc(staff, attacker, defender, damage);
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        //acts like blazing enchantment
        new Blazing().proc(staff, attacker, defender, damage);
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        //need to perform flame spread logic here so we can determine what cells to put flames in.
        affectedCells = new HashSet<>();
        visualCells = new HashSet<>();

        // 4/6/8 distance
        int maxDist = 2 + 2 * chargesPerCast();
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

        for (int cell : visualCells) {
            //this way we only get the cells at the tip, much better performance.
            ((MagicMissile) curUser.sprite.parent.recycle(MagicMissile.class)).reset(
                    MagicMissile.FIRE_CONE,
                    curUser.sprite,
                    cell,
                    null
            );
        }
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FIRE_CONE,
                curUser.sprite,
                bolt.path.get(dist / 2),
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    protected int chargesPerCast() {
        //consumes 30% of current charges, rounded up, with a minimum of one.
        return Math.max(1, (int) Math.ceil(curCharges * 0.3f));
    }

    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_desc", chargesPerCast(), min(), max());
        else
            return Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xEE7722);
        particle.am = 0.5f;
        particle.setLifespan(0.6f);
        particle.acc.set(0, -40);
        particle.setSize(0f, 3f);
        particle.shuffleXY(1.5f);
    }

}
