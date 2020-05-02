package studio.baka.satoripixeldungeon.items.scrolls;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRetribution extends Scroll {

    {
        initials = 4;
    }

    @Override
    public void doRead() {

        GameScene.flash(0xFFFFFF);

        //scales from 0x to 1x power, maxing at ~10% HP
        float hpPercent = (curUser.HT - curUser.HP) / (float) (curUser.HT);
        float power = Math.min(4f, 4.45f * hpPercent);

        Sample.INSTANCE.play(Assets.SND_BLAST);
        Invisibility.dispel();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                //deals 10%HT, plus 0-90%HP based on scaling
                mob.damage(Math.round(mob.HT / 10f + (mob.HP * power * 0.225f)), this);
                if (mob.isAlive()) {
                    Buff.prolong(mob, Blindness.class, Math.round(6 + power));
                }
            }
        }

        Buff.prolong(curUser, Weakness.class, Weakness.DURATION / 2f);
        Buff.prolong(curUser, Blindness.class, Math.round(6 + power));
        Dungeon.observe();

        setKnown();

        readAnimation();

    }

    @Override
    public void empoweredRead() {
        GameScene.flash(0xFFFFFF);

        Sample.INSTANCE.play(Assets.SND_BLAST);
        Invisibility.dispel();

        //scales from 3x to 5x power, maxing at ~20% HP
        float hpPercent = (curUser.HT - curUser.HP) / (float) (curUser.HT);
        float power = Math.min(5f, 3f + 2.5f * hpPercent);

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Dungeon.level.heroFOV[mob.pos]) {
                mob.damage(Math.round(mob.HP * power / 5f), this);
            }
        }

        setKnown();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
