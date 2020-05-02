package studio.baka.satoripixeldungeon.actors.blobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.BlobEmitter;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.effects.particles.ShaftParticle;
import studio.baka.satoripixeldungeon.items.DewVial;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.journal.Notes.Landmark;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WaterOfHealth extends WellWater {

    @Override
    protected boolean affectHero(Hero hero) {

        if (!hero.isAlive()) return false;

        Sample.INSTANCE.play(Assets.SND_DRINK);

        hero.HP = hero.HT;
        hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);

        PotionOfHealing.cure(hero);
        hero.belongings.uncurseEquipped();
        hero.buff(Hunger.class).satisfy(Hunger.STARVING);

        CellEmitter.get(hero.pos).start(ShaftParticle.FACTORY, 0.2f, 3);

        Dungeon.hero.interrupt();

        GLog.p(Messages.get(this, "procced"));

        return true;
    }

    @Override
    protected Item affectItem(Item item, int pos) {
        if (item instanceof DewVial && !((DewVial) item).isFull()) {
            ((DewVial) item).fill();
            return item;
        }

        return null;
    }

    @Override
    protected Landmark record() {
        return Landmark.WELL_OF_HEALTH;
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(Speck.HEALING), 0.5f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
