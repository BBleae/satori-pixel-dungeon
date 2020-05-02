package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.quest.MetalShard;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMagicMapping;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.levels.traps.Trap;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

public class ReclaimTrap extends TargetedSpell {

    {
        image = ItemSpriteSheet.RECLAIM_TRAP;
    }

    private Class<? extends Trap> storedTrap = null;

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        if (storedTrap == null) {
            quantity++; //storing a trap doesn't consume the spell
            Trap t = Dungeon.level.traps.get(bolt.collisionPos);
            if (t != null && t.active && t.visible) {
                t.disarm();

                Sample.INSTANCE.play(Assets.SND_LIGHTNING);
                ScrollOfRecharging.charge(hero);
                storedTrap = t.getClass();

            } else {
                GLog.w(Messages.get(this, "no_trap"));
            }
        } else {

            Trap t = Reflection.newInstance(storedTrap);
            storedTrap = null;

            t.pos = bolt.collisionPos;
            t.activate();

        }
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (storedTrap != null) {
            desc += "\n\n" + Messages.get(this, "desc_trap", Messages.get(storedTrap, "name"));
        }
        return desc;
    }

    @Override
    protected void onThrow(int cell) {
        storedTrap = null;
        super.onThrow(cell);
    }

    @Override
    public void doDrop(Hero hero) {
        storedTrap = null;
        super.doDrop(hero);
    }

    private static final ItemSprite.Glowing[] COLORS = new ItemSprite.Glowing[]{
            new ItemSprite.Glowing(0xFF0000),
            new ItemSprite.Glowing(0xFF8000),
            new ItemSprite.Glowing(0xFFFF00),
            new ItemSprite.Glowing(0x00FF00),
            new ItemSprite.Glowing(0x00FFFF),
            new ItemSprite.Glowing(0x8000FF),
            new ItemSprite.Glowing(0xFFFFFF),
            new ItemSprite.Glowing(0x808080),
            new ItemSprite.Glowing(0x000000)
    };

    @Override
    public ItemSprite.Glowing glowing() {
        if (storedTrap != null) {
            return COLORS[Reflection.newInstance(storedTrap).color];
        }
        return null;
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((40 + 100) / 3f));
    }

    private static final String STORED_TRAP = "stored_trap";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STORED_TRAP, storedTrap);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        storedTrap = bundle.getClass(STORED_TRAP);
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfMagicMapping.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 6;

            output = ReclaimTrap.class;
            outQuantity = 3;
        }

    }

}
