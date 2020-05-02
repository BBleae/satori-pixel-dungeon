package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.effects.Enchanting;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.stones.StoneOfEnchantment;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;

@SuppressWarnings("unchecked")
public class ScrollOfEnchantment extends ExoticScroll {

    {
        initials = 11;
    }

    @Override
    public void doRead() {
        setKnown();

        GameScene.selectItem(itemSelector, WndBag.Mode.ENCHANTABLE, Messages.get(this, "inv_title"));
    }

    protected WndBag.Listener itemSelector = item -> {

        if (item instanceof Weapon) {

            final Weapon.Enchantment[] enchants = new Weapon.Enchantment[3];

            Class<? extends Weapon.Enchantment> existing = ((Weapon) item).enchantment != null ? ((Weapon) item).enchantment.getClass() : null;
            enchants[0] = Weapon.Enchantment.randomCommon(existing);
            enchants[1] = Weapon.Enchantment.randomUncommon(existing);
            enchants[2] = Weapon.Enchantment.random(existing, enchants[0].getClass(), enchants[1].getClass());

            GameScene.show(new WndOptions(Messages.titleCase(ScrollOfEnchantment.this.name()),
                    Messages.get(ScrollOfEnchantment.class, "weapon") +
                            "\n\n" +
                            Messages.get(ScrollOfEnchantment.class, "cancel_warn"),
                    enchants[0].name(),
                    enchants[1].name(),
                    enchants[2].name(),
                    Messages.get(ScrollOfEnchantment.class, "cancel")) {

                @Override
                protected void onSelect(int index) {
                    if (index < 3) {
                        ((Weapon) item).enchant(enchants[index]);
                        GLog.p(Messages.get(StoneOfEnchantment.class, "weapon"));
                        ((ScrollOfEnchantment) curItem).readAnimation();

                        Sample.INSTANCE.play(Assets.SND_READ);
                        Invisibility.dispel();
                        Enchanting.show(curUser, item);
                    }
                }

                @Override
                public void onBackPressed() {
                    //do nothing, reader has to cancel
                }
            });

        } else if (item instanceof Armor) {

            final Armor.Glyph[] glyphs = new Armor.Glyph[3];

            Class<? extends Armor.Glyph> existing = ((Armor) item).glyph != null ? ((Armor) item).glyph.getClass() : null;
            glyphs[0] = Armor.Glyph.randomCommon(existing);
            glyphs[1] = Armor.Glyph.randomUncommon(existing);
            glyphs[2] = Armor.Glyph.random(existing, glyphs[0].getClass(), glyphs[1].getClass());

            GameScene.show(new WndOptions(Messages.titleCase(ScrollOfEnchantment.this.name()),
                    Messages.get(ScrollOfEnchantment.class, "armor") +
                            "\n\n" +
                            Messages.get(ScrollOfEnchantment.class, "cancel_warn"),
                    glyphs[0].name(),
                    glyphs[1].name(),
                    glyphs[2].name(),
                    Messages.get(ScrollOfEnchantment.class, "cancel")) {

                @Override
                protected void onSelect(int index) {
                    if (index < 3) {
                        ((Armor) item).inscribe(glyphs[index]);
                        GLog.p(Messages.get(StoneOfEnchantment.class, "armor"));
                        ((ScrollOfEnchantment) curItem).readAnimation();

                        Sample.INSTANCE.play(Assets.SND_READ);
                        Invisibility.dispel();
                        Enchanting.show(curUser, item);
                    }
                }

                @Override
                public void onBackPressed() {
                    //do nothing, reader has to cancel
                }
            });
        } else {
            //TODO if this can ever be found un-IDed, need logic for that
            curItem.collect();
        }
    };
}
