package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.effects.Identification;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.potions.*;
import studio.baka.satoripixeldungeon.items.potions.exotic.ExoticPotion;
import studio.baka.satoripixeldungeon.items.scrolls.*;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ExoticScroll;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.IconButton;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.IconTitle;
import studio.baka.satoripixeldungeon.windows.WndBag;
import com.watabou.noosa.Image;

import java.util.HashSet;

@SuppressWarnings("rawtypes")
public class StoneOfIntuition extends InventoryStone {


    {
        mode = WndBag.Mode.UNIDED_POTION_OR_SCROLL;
        image = ItemSpriteSheet.STONE_INTUITION;
    }

    @Override
    protected void onItemSelected(Item item) {

        GameScene.show(new WndGuess(item));

    }

    //in order of their consumable icon
    public static Class[] potions = new Class[]{
            PotionOfExperience.class,
            PotionOfFrost.class,
            PotionOfHaste.class,
            PotionOfHealing.class,
            PotionOfInvisibility.class,
            PotionOfLevitation.class,
            PotionOfLiquidFlame.class,
            PotionOfMindVision.class,
            PotionOfParalyticGas.class,
            PotionOfPurity.class,
            PotionOfStrength.class,
            PotionOfToxicGas.class
    };

    public static Class[] scrolls = new Class[]{
            ScrollOfIdentify.class,
            ScrollOfLullaby.class,
            ScrollOfMagicMapping.class,
            ScrollOfMirrorImage.class,
            ScrollOfRetribution.class,
            ScrollOfRage.class,
            ScrollOfRecharging.class,
            ScrollOfRemoveCurse.class,
            ScrollOfTeleportation.class,
            ScrollOfTerror.class,
            ScrollOfTransmutation.class,
            ScrollOfUpgrade.class
    };

    static Class curGuess = null;

    public class WndGuess extends Window {

        private static final int WIDTH = 120;
        private static final int BTN_SIZE = 20;

        public WndGuess(final Item item) {

            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(ItemSpriteSheet.STONE_INTUITION, null));
            titlebar.label(Messages.get(StoneOfIntuition.class, "name"));
            titlebar.setRect(0, 0, WIDTH, 0);
            add(titlebar);

            RenderedTextBlock text = PixelScene.renderTextBlock(6);
            text.text(Messages.get(this, "text"));
            text.setPos(0, titlebar.bottom());
            text.maxWidth(WIDTH);
            add(text);

            final RedButton guess = new RedButton("") {
                @Override
                protected void onClick() {
                    super.onClick();
                    useAnimation();
                    if (item.getClass() == curGuess) {
                        item.identify();
                        GLog.p(Messages.get(WndGuess.class, "correct"));
                        curUser.sprite.parent.add(new Identification(curUser.sprite.center().offset(0, -16)));
                    } else {
                        GLog.n(Messages.get(WndGuess.class, "incorrect"));
                    }
                    curGuess = null;
                    hide();
                }
            };
            guess.visible = false;
            guess.icon(new ItemSprite(item));
            guess.enable(false);
            guess.setRect(0, 80, WIDTH, 20);
            add(guess);

            float left;
            float top = text.bottom() + 5;
            int rows;
            int placed = 0;

            HashSet<Class<? extends Item>> unIDed = new HashSet<>();
            final Class[] all;

            final int row;
            if (item.isIdentified()) {
                hide();
                return;
            } else if (item instanceof Potion) {
                unIDed.addAll(Potion.getUnknown());
                all = potions.clone();
                if (item instanceof ExoticPotion) {
                    row = 8;
                    for (int i = 0; i < all.length; i++) {
                        all[i] = ExoticPotion.regToExo.get(all[i]);
                    }
                    HashSet<Class<? extends Item>> exoUID = new HashSet<>();
                    for (Class<? extends Item> i : unIDed) {
                        exoUID.add(ExoticPotion.regToExo.get(i));
                    }
                    unIDed = exoUID;
                } else {
                    row = 0;
                }
            } else if (item instanceof Scroll) {
                unIDed.addAll(Scroll.getUnknown());
                all = scrolls.clone();
                if (item instanceof ExoticScroll) {
                    row = 24;
                    for (int i = 0; i < all.length; i++) {
                        all[i] = ExoticScroll.regToExo.get(all[i]);
                    }
                    HashSet<Class<? extends Item>> exoUID = new HashSet<>();
                    for (Class<? extends Item> i : unIDed) {
                        exoUID.add(ExoticScroll.regToExo.get(i));
                    }
                    unIDed = exoUID;
                } else {
                    row = 16;
                }
            } else {
                hide();
                return;
            }

            if (unIDed.size() < 6) {
                rows = 1;
                top += BTN_SIZE / 2f;
                left = (WIDTH - BTN_SIZE * unIDed.size()) / 2f;
            } else {
                rows = 2;
                left = (WIDTH - BTN_SIZE * ((unIDed.size() + 1) / 2)) / 2f;
            }

            for (int i = 0; i < all.length; i++) {
                if (!unIDed.contains(all[i])) {
                    continue;
                }

                final int j = i;
                IconButton btn = new IconButton() {
                    @Override
                    protected void onClick() {
                        curGuess = all[j];
                        guess.visible = true;
                        guess.text(Messages.get(curGuess, "name"));
                        guess.enable(true);
                        super.onClick();
                    }
                };
                Image im = new Image(Assets.CONS_ICONS, 7 * i, row, 7, 8);
                im.scale.set(2f);
                btn.icon(im);
                btn.setRect(left + placed * BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
                add(btn);

                placed++;
                if (rows == 2 && placed == ((unIDed.size() + 1) / 2)) {
                    placed = 0;
                    if (unIDed.size() % 2 == 1) {
                        left += BTN_SIZE / 2f;
                    }
                    top += BTN_SIZE;
                }
            }

            resize(WIDTH, 100);

        }


        @Override
        public void onBackPressed() {
            super.onBackPressed();
            new StoneOfIntuition().collect();
        }
    }
}
