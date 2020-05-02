package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Barrier;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Charm;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.Beam;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.BloodParticle;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfTransfusion extends Wand {

    {
        image = ItemSpriteSheet.WAND_TRANSFUSION;

        collisionProperties = Ballistica.PROJECTILE;
    }

    private boolean freeCharge = false;

    @Override
    protected void onZap(Ballistica beam) {

        for (int c : beam.subPath(0, beam.dist))
            CellEmitter.center(c).burst(BloodParticle.BURST, 1);

        int cell = beam.collisionPos;

        Char ch = Actor.findChar(cell);

        if (ch instanceof Mob) {

            processSoulMark(ch, chargesPerCast());

            //this wand does different things depending on the target.

            //heals/shields an ally or a charmed enemy while damaging self
            if (ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null) {

                // 10% of max hp
                int selfDmg = Math.round(curUser.HT * 0.10f);

                int healing = selfDmg + 3 * level();
                int shielding = (ch.HP + healing) - ch.HT;
                if (shielding > 0) {
                    healing -= shielding;
                    Buff.affect(ch, Barrier.class).setShield(shielding);
                } else {
                    shielding = 0;
                }

                ch.HP += healing;

                ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2 + level() / 2);
                ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing + shielding);

                if (!freeCharge) {
                    damageHero(selfDmg);
                } else {
                    freeCharge = false;
                }

                //for enemies...
            } else {

                //charms living enemies
                if (!ch.properties().contains(Char.Property.UNDEAD)) {
                    Buff.affect(ch, Charm.class, 5).object = curUser.id();
                    ch.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 3 + level() / 2);

                    //harms the undead
                } else {
                    ch.damage(Random.NormalIntRange(3 + level() / 2, 6 + level()), this);
                    ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + level());
                    Sample.INSTANCE.play(Assets.SND_BURNING);
                }

                //and grants a self shield
                Buff.affect(curUser, Barrier.class).setShield((5 + 2 * level()));

            }

        }

    }

    //this wand costs health too
    private void damageHero(int damage) {

        curUser.damage(damage, this);

        if (!curUser.isAlive()) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        }
    }

    @Override
    protected int initialCharges() {
        return 1;
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // lvl 0 - 10%
        // lvl 1 - 18%
        // lvl 2 - 25%
        if (Random.Int(level() + 10) >= 9) {
            //grants a free use of the staff
            freeCharge = true;
            GLog.p(Messages.get(this, "charged"));
            attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
        }
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        // lvl 0 - 10%
        // lvl 1 - 18%
        // lvl 2 - 25%
        if (Random.Int(level() + 10) >= 9) {
            //grants a free use of the staff
            freeCharge = true;
            GLog.p(Messages.get(this, "charged"));
            attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
        }
    }

    @Override
    protected void fx(Ballistica beam, Callback callback) {
        curUser.sprite.parent.add(
                new Beam.HealthRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xCC0000);
        particle.am = 0.6f;
        particle.setLifespan(1f);
        particle.speed.polar(Random.Float(PointF.PI2), 2f);
        particle.setSize(1f, 2f);
        particle.radiateXY(0.5f);
    }

    private static final String FREECHARGE = "freecharge";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        freeCharge = bundle.getBoolean(FREECHARGE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FREECHARGE, freeCharge);
    }

}
