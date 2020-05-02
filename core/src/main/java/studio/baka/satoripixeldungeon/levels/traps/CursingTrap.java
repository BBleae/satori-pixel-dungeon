package studio.baka.satoripixeldungeon.levels.traps;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.*;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class CursingTrap extends Trap {

    {
        color = VIOLET;
        shape = WAVES;
    }

    @Override
    public void activate() {
        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
            Sample.INSTANCE.play(Assets.SND_CURSED);
        }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) {
            for (Item item : heap.items) {
                if (item.isUpgradable() && !(item instanceof MissileWeapon))
                    curse(item);
            }
        }

        if (Dungeon.hero.pos == pos && !Dungeon.hero.flying) {
            curse(Dungeon.hero);
        }
    }

    public static void curse(Hero hero) {
        //items the trap wants to curse because it will create a more negative effect
        ArrayList<Item> priorityCurse = new ArrayList<>();
        //items the trap can curse if nothing else is available.
        ArrayList<Item> canCurse = new ArrayList<>();

        KindOfWeapon weapon = hero.belongings.weapon;
        if (weapon instanceof Weapon && !weapon.cursed) {
            if (((Weapon) weapon).enchantment == null)
                priorityCurse.add(weapon);
            else
                canCurse.add(weapon);
        }

        Armor armor = hero.belongings.armor;
        if (armor != null && !armor.cursed) {
            if (armor.glyph == null)
                priorityCurse.add(armor);
            else
                canCurse.add(armor);
        }

        KindofMisc misc1 = hero.belongings.misc1;
        if (misc1 != null) {
            canCurse.add(misc1);
        }

        KindofMisc misc2 = hero.belongings.misc2;
        if (misc2 != null) {
            canCurse.add(misc2);
        }

        KindofMisc misc3 = hero.belongings.misc3;
        if (misc3 != null) {
            canCurse.add(misc3);
        }

        Collections.shuffle(priorityCurse);
        Collections.shuffle(canCurse);

        int numCurses = Random.Int(2) == 0 ? 1 : 2;

        for (int i = 0; i < numCurses; i++) {
            if (!priorityCurse.isEmpty()) {
                curse(priorityCurse.remove(0));
            } else if (!canCurse.isEmpty()) {
                curse(canCurse.remove(0));
            }
        }

        EquipableItem.equipCursed(hero);
        GLog.n(Messages.get(CursingTrap.class, "curse"));
    }

    @SuppressWarnings("unchecked")
    private static void curse(Item item) {
        item.cursed = item.cursedKnown = true;

        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            if (w.enchantment == null) {
                w.enchant(Weapon.Enchantment.randomCurse());
            }
        }
        if (item instanceof Armor) {
            Armor a = (Armor) item;
            if (a.glyph == null) {
                a.inscribe(Armor.Glyph.randomCurse());
            }
        }
    }
}
