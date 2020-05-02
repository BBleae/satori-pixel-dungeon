package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Ankh extends Item {

    public static final String AC_BLESS = "BLESS";

    {
        image = ItemSpriteSheet.ANKH;

        //You tell the ankh no, don't revive me, and then it comes back to revive you again in another run.
        //I'm not sure if that's enthusiasm or passive-aggression.
        bones = true;
    }

    public Boolean blessed = false;

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        DewVial vial = hero.belongings.getItem(DewVial.class);
        if (vial != null && vial.isEnough() && !blessed)
            actions.add(AC_BLESS);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_BLESS)) {

            DewVial vial = hero.belongings.getItem(DewVial.class);
            if (vial != null) {
                blessed = true;
                vial.empty(20);
                GLog.p(Messages.get(this, "bless"));
                hero.spend(1f);
                hero.busy();


                Sample.INSTANCE.play(Assets.SND_DRINK);
                CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                hero.sprite.operate(hero.pos);
            }
        }
    }

    @Override
    public String desc() {
        if (blessed)
            return Messages.get(this, "desc_blessed");
        else
            return super.desc();
    }

    public Boolean isBlessed() {
        return blessed;
    }

    private static final Glowing WHITE = new Glowing(0xFFFFCC);

    @Override
    public Glowing glowing() {
        return isBlessed() ? WHITE : null;
    }

    private static final String BLESSED = "blessed";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLESSED, blessed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        blessed = bundle.getBoolean(BLESSED);
    }

    @Override
    public int price() {
        return 50 * quantity;
    }
}
