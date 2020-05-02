package studio.baka.satoripixeldungeon.items.potions;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ConfusionGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Levitation;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfLevitation extends Potion {

    {
        initials = 5;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.level.heroFOV[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        GameScene.add(Blob.seed(cell, 1000, ConfusionGas.class));
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect(hero, Levitation.class, Levitation.DURATION);
        GLog.i(Messages.get(this, "float"));
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
