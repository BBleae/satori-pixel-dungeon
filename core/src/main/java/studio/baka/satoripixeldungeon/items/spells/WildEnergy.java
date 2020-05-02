package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.actors.buffs.ArtifactRecharge;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.artifacts.Artifact;
import studio.baka.satoripixeldungeon.items.quest.MetalShard;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ScrollOfMysticalEnergy;
import studio.baka.satoripixeldungeon.items.wands.CursedWand;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;

public class WildEnergy extends TargetedSpell {

    {
        image = ItemSpriteSheet.WILD_ENERGY;
    }

    //we rely on cursedWand to do fx instead
    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        affectTarget(bolt, curUser);
    }

    @Override
    protected void affectTarget(Ballistica bolt, final Hero hero) {
        CursedWand.cursedZap(this, hero, bolt, () -> {
            ScrollOfRecharging.charge(hero);

            hero.belongings.charge(1f);
            for (int i = 0; i < 4; i++) {
                if (hero.belongings.misc1 instanceof Artifact) ((Artifact) hero.belongings.misc1).charge(hero);
                if (hero.belongings.misc2 instanceof Artifact) ((Artifact) hero.belongings.misc2).charge(hero);
                if (hero.belongings.misc3 instanceof Artifact) ((Artifact) hero.belongings.misc3).charge(hero);
            }

            Buff.affect(hero, Recharging.class, 8f);
            Buff.affect(hero, ArtifactRecharge.class).prolong(8);

            detach(curUser.belongings.backpack);
            updateQuickslot();
            curUser.spendAndNext(1f);
        });
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((50 + 100) / 5f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfMysticalEnergy.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = WildEnergy.class;
            outQuantity = 5;
        }

    }
}
