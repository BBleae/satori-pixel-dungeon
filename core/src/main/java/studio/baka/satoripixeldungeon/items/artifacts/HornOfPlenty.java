package studio.baka.satoripixeldungeon.items.artifacts;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Hunger;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.SpellSprite;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.food.Blandfruit;
import studio.baka.satoripixeldungeon.items.food.Food;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class HornOfPlenty extends Artifact {


    {
        image = ItemSpriteSheet.ARTIFACT_HORN1;

        levelCap = 10;

        charge = 0;
        partialCharge = 0;
        chargeCap = 10 + level();

        defaultAction = AC_EAT;
    }

    private int storedFoodEnergy = 0;

    public static final String AC_EAT = "EAT";
    public static final String AC_STORE = "STORE";

    protected WndBag.Mode mode = WndBag.Mode.FOOD;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge > 0)
            actions.add(AC_EAT);
        if (isEquipped(hero) && level() < levelCap && !cursed)
            actions.add(AC_STORE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_EAT)) {

            if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            else if (charge == 0) GLog.i(Messages.get(this, "no_food"));
            else {
                //consume as much food as it takes to be full, to a minimum of 1
                Hunger hunger = Buff.affect(Dungeon.hero, Hunger.class);
                int chargesToUse = Math.max(1, hunger.hunger() / (int) (Hunger.STARVING / 10));
                if (chargesToUse > charge) chargesToUse = charge;
                hunger.satisfy((Hunger.STARVING / 10) * chargesToUse);

                Food.foodProc(hero);

                Statistics.foodEaten++;

                charge -= chargesToUse;

                hero.sprite.operate(hero.pos);
                hero.busy();
                SpellSprite.show(hero, SpellSprite.FOOD);
                Sample.INSTANCE.play(Assets.SND_EAT);
                GLog.i(Messages.get(this, "eat"));

                hero.spend(Food.TIME_TO_EAT);

                Badges.validateFoodEaten();

                if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
                else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
                else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;
                else image = ItemSpriteSheet.ARTIFACT_HORN1;

                updateQuickslot();
            }

        } else if (action.equals(AC_STORE)) {

            GameScene.selectItem(itemSelector, mode, Messages.get(this, "prompt"));

        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new hornRecharge();
    }

    @Override
    public void charge(Hero target) {
        if (charge < chargeCap) {
            partialCharge += 0.25f;
            if (partialCharge >= 1) {
                partialCharge--;
                charge++;

                if (charge == chargeCap) {
                    GLog.p(Messages.get(HornOfPlenty.class, "full"));
                    partialCharge = 0;
                }

                if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
                else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
                else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;

                updateQuickslot();
            }
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (!cursed) {
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this, "desc_hint");
            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        return desc;
    }

    @Override
    public void level(int value) {
        super.level(value);
        chargeCap = 10 + level();
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        chargeCap = 10 + level();
        return this;
    }

    public void gainFoodValue(Food food) {
        if (level() >= 10) return;

        storedFoodEnergy += food.energy;
        if (storedFoodEnergy >= Hunger.HUNGRY) {
            int upgrades = storedFoodEnergy / (int) Hunger.HUNGRY;
            upgrades = Math.min(upgrades, 10 - level());
            upgrade(upgrades);
            storedFoodEnergy -= upgrades * Hunger.HUNGRY;
            if (level() == 10) {
                storedFoodEnergy = 0;
                GLog.p(Messages.get(this, "maxlevel"));
            } else {
                GLog.p(Messages.get(this, "levelup"));
            }
        } else {
            GLog.i(Messages.get(this, "feed"));
        }
    }

    private static final String STORED = "stored";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STORED, storedFoodEnergy);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        storedFoodEnergy = bundle.getInt(STORED);

        if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
        else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
        else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;
    }

    public class hornRecharge extends ArtifactBuff {

        public void gainCharge(float levelPortion) {
            if (cursed) return;

            if (charge < chargeCap) {

                //generates 0.2x max hunger value every hero level, +0.1x max value per horn level
                //to a max of 1.2x max hunger value per hero level
                //This means that a standard ration will be recovered in 6.67 hero levels
                partialCharge += Hunger.STARVING * levelPortion * (0.2f + (0.1f * level()));

                //charge is in increments of 1/10 max hunger value.
                while (partialCharge >= Hunger.STARVING / 10) {
                    charge++;
                    partialCharge -= Hunger.STARVING / 10;

                    if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
                    else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
                    else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;
                    else image = ItemSpriteSheet.ARTIFACT_HORN1;

                    if (charge == chargeCap) {
                        GLog.p(Messages.get(HornOfPlenty.class, "full"));
                        partialCharge = 0;
                    }

                    updateQuickslot();
                }
            } else
                partialCharge = 0;
        }

    }

    protected static WndBag.Listener itemSelector = item -> {
        if (item instanceof Food) {
            if (item instanceof Blandfruit && ((Blandfruit) item).potionAttrib == null) {
                GLog.w(Messages.get(HornOfPlenty.class, "reject"));
            } else {
                Hero hero = Dungeon.hero;
                hero.sprite.operate(hero.pos);
                hero.busy();
                hero.spend(Food.TIME_TO_EAT);

                ((HornOfPlenty) curItem).gainFoodValue(((Food) item));
                item.detach(hero.belongings.backpack);
            }

        }
    };
}
