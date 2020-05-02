package studio.baka.satoripixeldungeon.items.stones;

import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfUpgrade;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import studio.baka.satoripixeldungeon.windows.IconTitle;
import studio.baka.satoripixeldungeon.windows.WndBag;

public class StoneOfAugmentation extends InventoryStone {

    {
        mode = WndBag.Mode.ENCHANTABLE;
        image = ItemSpriteSheet.STONE_AUGMENTATION;
    }

    @Override
    protected void onItemSelected(Item item) {

        GameScene.show(new WndAugment(item));

    }

    public void apply(Weapon weapon, Weapon.Augment augment) {

        weapon.augment = augment;
        useAnimation();
        ScrollOfUpgrade.upgrade(curUser);

    }

    public void apply(Armor armor, Armor.Augment augment) {

        armor.augment = augment;
        useAnimation();
        ScrollOfUpgrade.upgrade(curUser);
    }

    @Override
    public int price() {
        return 30 * quantity;
    }

    public class WndAugment extends Window {

        private static final int WIDTH = 120;
        private static final int MARGIN = 2;
        private static final int BUTTON_WIDTH = WIDTH - MARGIN * 2;
        private static final int BUTTON_HEIGHT = 20;

        public WndAugment(final Item toAugment) {
            super();

            IconTitle titlebar = new IconTitle(toAugment);
            titlebar.setRect(0, 0, WIDTH, 0);
            add(titlebar);

            RenderedTextBlock tfMesage = PixelScene.renderTextBlock(Messages.get(this, "choice"), 8);
            tfMesage.maxWidth(WIDTH - MARGIN * 2);
            tfMesage.setPos(MARGIN, titlebar.bottom() + MARGIN);
            add(tfMesage);

            float pos = tfMesage.top() + tfMesage.height();

            if (toAugment instanceof Weapon) {
                for (final Weapon.Augment aug : Weapon.Augment.values()) {
                    if (((Weapon) toAugment).augment != aug) {
                        RedButton btnSpeed = new RedButton(Messages.get(this, aug.name())) {
                            @Override
                            protected void onClick() {
                                hide();
                                StoneOfAugmentation.this.apply((Weapon) toAugment, aug);
                            }
                        };
                        btnSpeed.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
                        add(btnSpeed);

                        pos = btnSpeed.bottom();
                    }
                }

            } else if (toAugment instanceof Armor) {
                for (final Armor.Augment aug : Armor.Augment.values()) {
                    if (((Armor) toAugment).augment != aug) {
                        RedButton btnSpeed = new RedButton(Messages.get(this, aug.name())) {
                            @Override
                            protected void onClick() {
                                hide();
                                StoneOfAugmentation.this.apply((Armor) toAugment, aug);
                            }
                        };
                        btnSpeed.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
                        add(btnSpeed);

                        pos = btnSpeed.bottom();
                    }
                }
            }

            RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
                @Override
                protected void onClick() {
                    hide();
                    StoneOfAugmentation.this.collect();
                }
            };
            btnCancel.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
            add(btnCancel);

            resize(WIDTH, (int) btnCancel.bottom() + MARGIN);
        }

        @Override
        public void onBackPressed() {
            StoneOfAugmentation.this.collect();
            super.onBackPressed();
        }
    }
}
