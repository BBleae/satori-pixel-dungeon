package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Healing;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Lightning;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Shocking;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.BArray;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfHumor extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_HUMOR;
    }

    private final ArrayList<Char> affected = new ArrayList<>();

    private final ArrayList<Lightning.Arc> arcs = new ArrayList<>();

    public int min(int lvl) {
        return 5 + (int) (lvl * 0.8f);
    }

    public int max(int lvl) {
        return 10 + 4 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {

        //lightning deals less damage per-target, the more targets that are hit.
        float multipler = 0.4f + (0.6f / affected.size());
        int healcount = affected.size();
        //if the main target is in water, all affected take full damage
        if (Dungeon.level.water[bolt.collisionPos]) multipler = 1f;

        for (Char ch : affected) {
            processSoulMark(ch, chargesPerCast());
            if (ch != Dungeon.hero) ch.damage(Math.round(damageRoll() * multipler), this);
            else {
                Buff.affect(ch, Healing.class).setHeal(healcount, 1f, 0);
            }

            //if (ch == Dungeon.hero) Camera.main.shake( 2, 0.3f );
            ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            ch.sprite.flash();
        }

        if (!curUser.isAlive()) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        //acts like shocking enchantment
        new Shocking().proc(staff, attacker, defender, damage);
        Buff.affect(attacker, Healing.class).setHeal((int) (damage / 5f), 1f, 0);
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        //acts like shocking enchantment
        //new Shocking().proc(staff, attacker, defender, damage);
        //Buff.affect(attacker,Healing.class).setHeal((int)(damage/5f),1f,0);
    }

    private void arc(Char ch) {

        int dist = (Dungeon.level.water[ch.pos] && !ch.flying) ? 2 : 1;

        ArrayList<Char> hitThisArc = new ArrayList<>();
        PathFinder.buildDistanceMap(ch.pos, BArray.not(Dungeon.level.solid, null), dist);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n == Dungeon.hero && PathFinder.distance[i] > 1)
                //the hero is only zapped if they are adjacent
                {
                } else if (n != null && !affected.contains(n)) {
                    hitThisArc.add(n);
                }
            }
        }

        affected.addAll(hitThisArc);
        for (Char hit : hitThisArc) {
            arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
            arc(hit);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {

        affected.clear();
        arcs.clear();

        int cell = bolt.collisionPos;

        Char ch = Actor.findChar(cell);
        if (ch != null) {
            affected.add(ch);
            arcs.add(new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
            arc(ch);
        } else {
            arcs.add(new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
        }

        //don't want to wait for the effect before processing damage.
        curUser.sprite.parent.addToFront(new Lightning(arcs, null));
        Sample.INSTANCE.play(Assets.SND_LIGHTNING);
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xFFFFFF);
        particle.am = 0.6f;
        particle.setLifespan(0.6f);
        particle.acc.set(0, +10);
        particle.speed.polar(-Random.Float(3.1415926f), 6f);
        particle.setSize(0f, 1.5f);
        particle.sizeJitter = 1f;
        particle.shuffleXY(1f);
        float dst = Random.Float(1f);
        particle.x -= dst;
        particle.y += dst;
    }

}
