package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.effects.Beam;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.PurpleParticle;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfDisintegration extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_DISINTEGRATION;

        collisionProperties = Ballistica.WONT_STOP;
    }


    public int min(int lvl) {
        return 2 + lvl;
    }

    public int max(int lvl) {
        return 8 + 4 * lvl;
    }

    @Override
    protected void onZap(Ballistica beam) {

        boolean terrainAffected = false;

        int level = level();

        int maxDistance = Math.min(distance(), beam.dist);

        ArrayList<Char> chars = new ArrayList<>();

        int terrainPassed = 2, terrainBonus = 0;
        for (int c : beam.subPath(1, maxDistance)) {

            Char ch;
            if ((ch = Actor.findChar(c)) != null) {

                //we don't want to count passed terrain after the last enemy hit. That would be a lot of bonus levels.
                //terrainPassed starts at 2, equivalent of rounding up when /3 for integer arithmetic.
                terrainBonus += terrainPassed / 3;
                terrainPassed = terrainPassed % 3;

                chars.add(ch);
            }

            if (Dungeon.level.flamable[c]) {

                Dungeon.level.destroy(c);
                GameScene.updateMap(c);
                terrainAffected = true;

            }

            if (Dungeon.level.solid[c])
                terrainPassed++;

            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
        }

        if (terrainAffected) {
            Dungeon.observe();
        }

        int lvl = level + (chars.size() - 1) + terrainBonus;
        for (Char ch : chars) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(lvl), this);
            ch.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2));
            ch.sprite.flash();
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        //no direct effect, see magesStaff.reachfactor
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        //no direct effect, see magesStaff.reachfactor
        defender.damage((int) (damage * 0.1f), attacker);
    }

    private int distance() {
        return level() * 2 + 4;
    }

    @Override
    protected void fx(Ballistica beam, Callback callback) {

        int cell = beam.path.get(Math.min(beam.dist, distance()));
        curUser.sprite.parent.add(new Beam.DeathRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x220022);
        particle.am = 0.6f;
        particle.setLifespan(1f);
        particle.acc.set(10, -10);
        particle.setSize(0.5f, 3f);
        particle.shuffleXY(1f);
    }

}
