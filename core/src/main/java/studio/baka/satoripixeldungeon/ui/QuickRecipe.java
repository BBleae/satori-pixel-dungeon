package studio.baka.satoripixeldungeon.ui;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.items.bombs.Bomb;
import studio.baka.satoripixeldungeon.items.food.*;
import studio.baka.satoripixeldungeon.items.potions.AlchemicalCatalyst;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.potions.brews.BlizzardBrew;
import studio.baka.satoripixeldungeon.items.potions.brews.CausticBrew;
import studio.baka.satoripixeldungeon.items.potions.brews.InfernalBrew;
import studio.baka.satoripixeldungeon.items.potions.brews.ShockingBrew;
import studio.baka.satoripixeldungeon.items.potions.elixirs.*;
import studio.baka.satoripixeldungeon.items.potions.exotic.ExoticPotion;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.items.spells.*;
import studio.baka.satoripixeldungeon.items.stones.Runestone;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.scenes.AlchemyScene;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndInfoItem;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuickRecipe extends Component {

    private final ArrayList<Item> ingredients;

    private final ArrayList<ItemSlot> inputs;
    private final QuickRecipe.arrow arrow;
    private final ItemSlot output;

    public QuickRecipe(Recipe.SimpleRecipe r) {
        this(r, r.getIngredients(), r.sampleOutput(null));
    }

    public QuickRecipe(Recipe r, ArrayList<Item> inputs, final Item output) {

        ingredients = inputs;
        int cost = r.cost(inputs);
        boolean hasInputs = true;
        this.inputs = new ArrayList<>();
        for (final Item in : inputs) {
            anonymize(in);
            ItemSlot curr;
            curr = new ItemSlot(in) {
                @Override
                protected void onClick() {
                    SatoriPixelDungeon.scene().addToFront(new WndInfoItem(in));
                }
            };

            ArrayList<Item> similar = Dungeon.hero.belongings.getAllSimilar(in);
            int quantity = 0;
            for (Item sim : similar) {
                //if we are looking for a specific item, it must be IDed
                if (sim.getClass() != in.getClass() || sim.isIdentified()) quantity += sim.quantity();
            }

            if (quantity < in.quantity()) {
                curr.icon.alpha(0.3f);
                hasInputs = false;
            }
            curr.showParams(true, false, true);
            add(curr);
            this.inputs.add(curr);
        }

        if (cost > 0) {
            arrow = new arrow(Icons.get(Icons.ARROW), cost);
            arrow.hardlightText(0x00CCFF);
        } else {
            arrow = new arrow(Icons.get(Icons.ARROW));
        }
        if (hasInputs) {
            arrow.icon.tint(1, 1, 0, 1);
            if (!(SatoriPixelDungeon.scene() instanceof AlchemyScene)) {
                arrow.enable(false);
            }
        } else {
            arrow.icon.color(0, 0, 0);
            arrow.enable(false);
        }
        add(arrow);

        anonymize(output);
        this.output = new ItemSlot(output) {
            @Override
            protected void onClick() {
                SatoriPixelDungeon.scene().addToFront(new WndInfoItem(output));
            }
        };
        if (!hasInputs) {
            this.output.icon.alpha(0.3f);
        }
        this.output.showParams(true, false, true);
        add(this.output);

        layout();
    }

    @Override
    protected void layout() {

        height = 16;
        width = 0;

        for (ItemSlot item : inputs) {
            item.setRect(x + width, y, 16, 16);
            width += 16;
        }

        arrow.setRect(x + width, y, 14, 16);
        width += 14;

        output.setRect(x + width, y, 16, 16);
        width += 16;
    }

    //used to ensure that un-IDed items are not spoiled
    private void anonymize(Item item) {
        if (item instanceof Potion) {
            ((Potion) item).anonymize();
        } else if (item instanceof Scroll) {
            ((Scroll) item).anonymize();
        }
    }

    public class arrow extends IconButton {

        BitmapText text;

        public arrow() {
            super();
        }

        public arrow(Image icon) {
            super(icon);
        }

        public arrow(Image icon, int count) {
            super(icon);
            text = new BitmapText(Integer.toString(count), PixelScene.pixelFont);
            text.measure();
            add(text);
        }

        @Override
        protected void layout() {
            super.layout();

            if (text != null) {
                text.x = x;
                text.y = y;
                PixelScene.align(text);
            }
        }

        @Override
        protected void onClick() {
            super.onClick();

            //find the window this is inside of and close it
            Group parent = this.parent;
            while (parent != null) {
                if (parent instanceof Window) {
                    ((Window) parent).hide();
                    break;
                } else {
                    parent = parent.parent;
                }
            }

            ((AlchemyScene) SatoriPixelDungeon.scene()).populate(ingredients, Dungeon.hero.belongings);
        }

        public void hardlightText(int color) {
            if (text != null) text.hardlight(color);
        }
    }

    //gets recipes for a particular alchemy guide page
    //a null entry indicates a break in section
    public static ArrayList<QuickRecipe> getRecipes(int pageIdx) {
        ArrayList<QuickRecipe> result = new ArrayList<>();
        switch (pageIdx) {
            case 0:
            default:
                result.add(new QuickRecipe(new Potion.SeedToPotion(), new ArrayList<>(Collections.singletonList(new Plant.Seed.PlaceHolder().quantity(3))), new WndBag.Placeholder(ItemSpriteSheet.POTION_HOLDER) {
                    {
                        name = Messages.get(Potion.SeedToPotion.class, "name");
                    }

                    @Override
                    public String info() {
                        return "";
                    }
                }));
                return result;
            case 1:
                Recipe r = new Scroll.ScrollToStone();
                for (Class<?> cls : Generator.Category.SCROLL.classes) {
                    Scroll scroll = (Scroll) Reflection.newInstance(cls);
                    if (!scroll.isKnown()) scroll.anonymize();
                    ArrayList<Item> in = new ArrayList<>(Collections.singletonList(scroll));
                    result.add(new QuickRecipe(r, in, r.sampleOutput(in)));
                }
                return result;
            case 2:
                result.add(new QuickRecipe(new StewedMeat.oneMeat()));
                result.add(new QuickRecipe(new StewedMeat.twoMeat()));
                result.add(new QuickRecipe(new StewedMeat.threeMeat()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new MeatPie.Recipe(),
                        new ArrayList<>(Arrays.asList(new Pasty(), new Food(), new MysteryMeat.PlaceHolder())),
                        new MeatPie()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new Blandfruit.CookFruit(),
                        new ArrayList<>(Arrays.asList(new Blandfruit(), new Plant.Seed.PlaceHolder())),
                        new Blandfruit() {
                            {
                                name = Messages.get(Blandfruit.class, "cooked");
                            }

                            @Override
                            public String info() {
                                return "";
                            }
                        }));
                return result;
            case 3:
                r = new Bomb.EnhanceBomb();
                int i = 0;
                for (Class<?> cls : Bomb.EnhanceBomb.validIngredients.keySet()) {
                    if (i == 2) {
                        result.add(null);
                        i = 0;
                    }
                    Item item = (Item) Reflection.newInstance(cls);
                    ArrayList<Item> in = new ArrayList<>(Arrays.asList(new Bomb(), item));
                    result.add(new QuickRecipe(r, in, r.sampleOutput(in)));
                    i++;
                }
                return result;
            case 4:
                r = new ExoticPotion.PotionToExotic();
                for (Class<?> cls : Generator.Category.POTION.classes) {
                    Potion pot = (Potion) Reflection.newInstance(cls);
                    ArrayList<Item> in = new ArrayList<>(Arrays.asList(pot, new Plant.Seed.PlaceHolder().quantity(2)));
                    result.add(new QuickRecipe(r, in, r.sampleOutput(in)));
                }
                return result;
            case 5:
                r = new ExoticScroll.ScrollToExotic();
                for (Class<?> cls : Generator.Category.SCROLL.classes) {
                    Scroll scroll = (Scroll) Reflection.newInstance(cls);
                    ArrayList<Item> in = new ArrayList<>(Arrays.asList(scroll, new Runestone.PlaceHolder().quantity(2)));
                    result.add(new QuickRecipe(r, in, r.sampleOutput(in)));
                }
                return result;
            case 6:
                result.add(new QuickRecipe(new AlchemicalCatalyst.Recipe(), new ArrayList<>(Arrays.asList(new Potion.PlaceHolder(), new Plant.Seed.PlaceHolder())), new AlchemicalCatalyst()));
                result.add(new QuickRecipe(new AlchemicalCatalyst.Recipe(), new ArrayList<>(Arrays.asList(new Potion.PlaceHolder(), new Runestone.PlaceHolder())), new AlchemicalCatalyst()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new ArcaneCatalyst.Recipe(), new ArrayList<>(Arrays.asList(new Scroll.PlaceHolder(), new Runestone.PlaceHolder())), new ArcaneCatalyst()));
                result.add(new QuickRecipe(new ArcaneCatalyst.Recipe(), new ArrayList<>(Arrays.asList(new Scroll.PlaceHolder(), new Plant.Seed.PlaceHolder())), new ArcaneCatalyst()));
                return result;
            case 7:
                result.add(new QuickRecipe(new CausticBrew.Recipe()));
                result.add(new QuickRecipe(new InfernalBrew.Recipe()));
                result.add(new QuickRecipe(new BlizzardBrew.Recipe()));
                result.add(new QuickRecipe(new ShockingBrew.Recipe()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new ElixirOfHoneyedHealing.Recipe()));
                result.add(new QuickRecipe(new ElixirOfMight.Recipe()));
                result.add(new QuickRecipe(new ElixirOfAquaticRejuvenation.Recipe()));
                result.add(new QuickRecipe(new ElixirOfDragonsBlood.Recipe()));
                result.add(new QuickRecipe(new ElixirOfIcyTouch.Recipe()));
                result.add(new QuickRecipe(new ElixirOfToxicEssence.Recipe()));
                result.add(new QuickRecipe(new ElixirOfArcaneArmor.Recipe()));
                return result;
            case 8:
                result.add(new QuickRecipe(new MagicalPorter.Recipe()));
                result.add(new QuickRecipe(new PhaseShift.Recipe()));
                result.add(new QuickRecipe(new WildEnergy.Recipe()));
                result.add(new QuickRecipe(new BeaconOfReturning.Recipe()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new AquaBlast.Recipe()));
                result.add(new QuickRecipe(new FeatherFall.Recipe()));
                result.add(new QuickRecipe(new ReclaimTrap.Recipe()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new CurseInfusion.Recipe()));
                result.add(new QuickRecipe(new MagicalInfusion.Recipe()));
                result.add(new QuickRecipe(new Alchemize.Recipe()));
                result.add(new QuickRecipe(new Recycle.Recipe()));
                return result;
        }
    }

}
