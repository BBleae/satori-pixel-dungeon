package studio.baka.satoripixeldungeon.items.wands;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Challenges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.buffs.Light;
import studio.baka.satoripixeldungeon.effects.Beam;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.RainbowParticle;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMagicMapping;
import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.items.weapon.melee.MahoStaff;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfPrismaticLight extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_PRISMATIC_LIGHT;

        collisionProperties = Ballistica.MAGIC_BOLT;
    }

    public int min(int lvl) {
        return 1 + lvl;
    }

    public int max(int lvl) {
        return 5 + 3 * lvl;
    }

    @Override
    protected void onZap(Ballistica beam) {
        affectMap(beam);

        if (Dungeon.level.viewDistance < 6) {
            if (Dungeon.isChallenged(Challenges.DARKNESS)) {
                Buff.prolong(curUser, Light.class, 2f + level());
            } else {
                Buff.prolong(curUser, Light.class, 10f + level() * 5);
            }
        }

        Char ch = Actor.findChar(beam.collisionPos);
        if (ch != null) {
            processSoulMark(ch, chargesPerCast());
            affectTarget(ch);
        }
    }

    private void affectTarget(Char ch) {
        int dmg = damageRoll();

        //three in (5+lvl) chance of failing
        if (Random.Int(5 + level()) >= 3) {
            Buff.prolong(ch, Blindness.class, 2f + (level() * 0.333f));
            ch.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
        }

        if (ch.properties().contains(Char.Property.DEMONIC) || ch.properties().contains(Char.Property.UNDEAD)) {
            ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + level());
            Sample.INSTANCE.play(Assets.SND_BURNING);

            ch.damage(Math.round(dmg * 1.333f), this);
        } else {
            ch.sprite.centerEmitter().burst(RainbowParticle.BURST, 10 + level());

            ch.damage(dmg, this);
        }

    }

    private void affectMap(Ballistica beam) {
        boolean noticed = false;
        for (int c : beam.subPath(0, beam.dist)) {
            for (int n : PathFinder.NEIGHBOURS9) {
                int cell = c + n;

                if (Dungeon.level.discoverable[cell])
                    Dungeon.level.mapped[cell] = true;

                int terr = Dungeon.level.map[cell];
                if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                    Dungeon.level.discover(cell);

                    GameScene.discoverTile(cell, terr);
                    ScrollOfMagicMapping.discover(cell);

                    noticed = true;
                }
            }

            CellEmitter.center(c).burst(RainbowParticle.BURST, Random.IntRange(1, 2));
        }
        if (noticed)
            Sample.INSTANCE.play(Assets.SND_SECRET);

        GameScene.updateFog();
    }

    @Override
    protected void fx(Ballistica beam, Callback callback) {
        curUser.sprite.parent.add(
                new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        callback.call();
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        //cripples enemy
        Buff.prolong(defender, Cripple.class, 1f + staff.level());
    }

    @Override
    public void onHit(MahoStaff staff, Char attacker, Char defender, int damage) {
        //cripples enemy
        Buff.prolong(defender, Cripple.class, 1f + staff.level());
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(Random.Int(0x1000000));
        particle.am = 0.5f;
        particle.setLifespan(1f);
        particle.speed.polar(Random.Float(PointF.PI2), 2f);
        particle.setSize(1f, 2f);
        particle.radiateXY(0.5f);
    }

}
