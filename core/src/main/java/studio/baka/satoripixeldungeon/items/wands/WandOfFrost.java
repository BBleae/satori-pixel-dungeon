package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Chill;
import studio.baka.satoripixeldungeon.actors.buffs.FlavourBuff;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfFrost extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_FROST;
    }

    public int min(int lvl) {
        return 2 + lvl;
    }

    public int max(int lvl) {
        return 8 + 5 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {

        Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
        if (heap != null) {
            heap.freeze();
        }

        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {

            int damage = damageRoll();

            if (ch.buff(Frost.class) != null) {
                return; //do nothing, can't affect a frozen target
            }
            if (ch.buff(Chill.class) != null) {
                //7.5% less damage per turn of chill remaining
                float chill = ch.buff(Chill.class).cooldown();
                damage = (int) Math.round(damage * Math.pow(0.9f, chill));
            } else {
                ch.sprite.burst(0xFF99CCFF, level() / 2 + 2);
            }

            processSoulMark(ch, chargesPerCast());
            ch.damage(damage, this);

            if (ch.isAlive()) {
                if (Dungeon.level.water[ch.pos])
                    Buff.prolong(ch, Chill.class, 4 + level());
                else
                    Buff.prolong(ch, Chill.class, 2 + level());
            }
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FROST,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        Chill chill = defender.buff(Chill.class);
        if (chill != null && Random.IntRange(2, 10) <= chill.cooldown()) {
            //need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
            new FlavourBuff() {
                {
                    actPriority = VFX_PRIORITY;
                }

                public boolean act() {
                    Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(1f, 2f));
                    return super.act();
                }
            }.attachTo(defender);
        }
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        Chill chill = defender.buff(Chill.class);
        if (chill != null && Random.IntRange(2, 10) <= chill.cooldown()) {
            //need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
            new FlavourBuff() {
                {
                    actPriority = VFX_PRIORITY;
                }

                public boolean act() {
                    Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(1f, 2f));
                    return super.act();
                }
            }.attachTo(defender);
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x88CCFF);
        particle.am = 0.6f;
        particle.setLifespan(2f);
        float angle = Random.Float(PointF.PI2);
        particle.speed.polar(angle, 2f);
        particle.acc.set(0f, 1f);
        particle.setSize(0f, 1.5f);
        particle.radiateXY(Random.Float(1f));
    }

}
