package studio.baka.satoripixeldungeon.items;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.MagicImmune;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public abstract class EquipableItem extends Item {

    public static final String AC_EQUIP = "EQUIP";
    public static final String AC_UNEQUIP = "UNEQUIP";

    {
        bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(isEquipped(hero) ? AC_UNEQUIP : AC_EQUIP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_EQUIP)) {
            //In addition to equipping itself, item reassigns itself to the quickslot
            //This is a special case as the item is being removed from inventory, but is staying with the hero.
            int slot = Dungeon.quickslot.getSlot(this);
            doEquip(hero);
            if (slot != -1) {
                Dungeon.quickslot.setSlot(slot, this);
                updateQuickslot();
            }
        } else if (action.equals(AC_UNEQUIP)) {
            doUnequip(hero, true);
        }
    }

    @Override
    public void doDrop(Hero hero) {
        if (!isEquipped(hero) || doUnequip(hero, false, false)) {
            super.doDrop(hero);
        }
    }

    @Override
    public void cast(final Hero user, int dst) {

        if (isEquipped(user)) {
            if (quantity == 1 && !this.doUnequip(user, false, false)) {
                return;
            }
        }

        super.cast(user, dst);
    }

    public static void equipCursed(Hero hero) {
        hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
        Sample.INSTANCE.play(Assets.SND_CURSED);
    }

    protected float time2equip(Hero hero) {
        return 1;
    }

    public abstract boolean doEquip(Hero hero);

    public boolean doUnequip(Hero hero, boolean collect, boolean single) {

        if (cursed && hero.buff(MagicImmune.class) == null) {
            GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
            return false;
        }

        if (single) {
            hero.spendAndNext(time2equip(hero));
        } else {
            hero.spend(time2equip(hero));
        }

        if (!collect || !collect(hero.belongings.backpack)) {
            onDetach();
            Dungeon.quickslot.clearItem(this);
            updateQuickslot();
            if (collect) Dungeon.level.drop(this, hero.pos);
        }

        return true;
    }

    final public boolean doUnequip(Hero hero, boolean collect) {
        return doUnequip(hero, collect, true);
    }

    public void activate(Char ch) {
    }
}
