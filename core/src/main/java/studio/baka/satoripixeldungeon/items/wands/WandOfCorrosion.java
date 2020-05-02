package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.CorrosiveGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Ooze;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.effects.particles.CorrosionParticle;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfCorrosion extends Wand {

    {
        image = ItemSpriteSheet.WAND_CORROSION;

        collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_TERRAIN;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        CorrosiveGas gas = Blob.seed(bolt.collisionPos, 50 + 10 * level(), CorrosiveGas.class);
        CellEmitter.center(bolt.collisionPos).burst(CorrosionParticle.SPLASH, 10);
        gas.setStrength(2 + level());
        GameScene.add(gas);

        for (int i : PathFinder.NEIGHBOURS9) {
            Char ch = Actor.findChar(bolt.collisionPos + i);
            if (ch != null) {
                processSoulMark(ch, chargesPerCast());
            }
        }

        if (Actor.findChar(bolt.collisionPos) == null) {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(
                curUser.sprite.parent,
                MagicMissile.CORROSION,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        if (Random.Int(level() + 3) >= 2) {

            Buff.affect(defender, Ooze.class).set(20f);
            CellEmitter.center(defender.pos).burst(CorrosionParticle.SPLASH, 5);

        }
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        if (Random.Int(level() + 3) >= 2) {

            Buff.affect(defender, Ooze.class).set(20f);
            CellEmitter.center(defender.pos).burst(CorrosionParticle.SPLASH, 5);

        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(ColorMath.random(0xAAAAAA, 0xFF8800));
        particle.am = 0.6f;
        particle.setLifespan(1f);
        particle.acc.set(0, 20);
        particle.setSize(0.5f, 3f);
        particle.shuffleXY(1f);
    }

}
