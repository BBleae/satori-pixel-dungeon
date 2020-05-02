package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.artifacts.MasterThievesArmband;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Gold extends Item {

    private static final String TXT_VALUE = "%+d";

    {
        image = ItemSpriteSheet.GOLD;
        stackable = true;
    }

    public Gold() {
        this(1);
    }

    public Gold(int value) {
        this.quantity = value;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return new ArrayList<>();
    }

    @Override
    public boolean doPickUp(Hero hero) {

        Dungeon.gold += quantity;
        Statistics.goldCollected += quantity;
        Badges.validateGoldCollected();

        MasterThievesArmband.Thievery thievery = hero.buff(MasterThievesArmband.Thievery.class);
        if (thievery != null)
            thievery.collect(quantity);

        GameScene.pickUp(this, hero.pos);
        hero.sprite.showStatus(CharSprite.NEUTRAL, TXT_VALUE, quantity);
        hero.spendAndNext(TIME_TO_PICK_UP);

        Sample.INSTANCE.play(Assets.SND_GOLD, 1, 1, Random.Float(0.9f, 1.1f));

        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public Item random() {
        quantity = Random.Int(30 + Dungeon.depth * 10, 60 + Dungeon.depth * 20);
        return this;
    }

    private static final String VALUE = "value";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(VALUE, quantity);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        quantity = bundle.getInt(VALUE);
    }

    @Override
    public int price() {
        return 50 * quantity;
    }
}
