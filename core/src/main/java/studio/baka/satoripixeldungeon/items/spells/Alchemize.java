package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.AlchemyScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;

public class Alchemize extends Spell implements AlchemyScene.AlchemyProvider {

    {
        image = ItemSpriteSheet.ALCHEMIZE;
    }

    @Override
    protected void onCast(Hero hero) {
        if (hero.visibleEnemies() > hero.mindVisionEnemies.size()) {
            GLog.i(Messages.get(this, "enemy_near"));
            return;
        }
        detach(curUser.belongings.backpack);
        updateQuickslot();
        AlchemyScene.setProvider(this);
        SatoriPixelDungeon.switchScene(AlchemyScene.class);
    }

    @Override
    public int getEnergy() {
        return 0;
    }

    @Override
    public void spendEnergy(int reduction) {
        //do nothing
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((40 + 40) / 4f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            //noinspection unchecked
            inputs = new Class[]{ArcaneCatalyst.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = Alchemize.class;
            outQuantity = 4;
        }

    }
}
