package studio.baka.satoripixeldungeon.items.weapon.missiles.darts;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.PinCushion;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.*;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class TippedDart extends Dart {

    {
        tier = 2;

        //so that slightly more than 1.5x durability is needed for 2 uses
        baseUses = 0.65f;
    }

    private static final String AC_CLEAN = "CLEAN";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_TIP);
        actions.add(AC_CLEAN);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        if (action.equals(AC_CLEAN)) {

            GameScene.show(new WndOptions(Messages.get(this, "clean_title"),
                    Messages.get(this, "clean_desc"),
                    Messages.get(this, "clean_all"),
                    Messages.get(this, "clean_one"),
                    Messages.get(this, "cancel")) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        detachAll(hero.belongings.backpack);
                        new Dart().quantity(quantity).collect();

                        hero.spend(1f);
                        hero.busy();
                        hero.sprite.operate(hero.pos);
                    } else if (index == 1) {
                        detach(hero.belongings.backpack);
                        if (!new Dart().collect()) Dungeon.level.drop(new Dart(), hero.pos).sprite.drop();

                        hero.spend(1f);
                        hero.busy();
                        hero.sprite.operate(hero.pos);
                    }
                }
            });

        }
        super.execute(hero, action);
    }

    //exact same damage as regular darts, despite being higher tier.

    @Override
    protected void rangedHit(Char enemy, int cell) {
        super.rangedHit(enemy, cell);

        //need to spawn a dart
        if (durability <= 0) {
            //attempt to stick the dart to the enemy, just drop it if we can't.
            Dart d = new Dart();
            if (enemy.isAlive() && sticky) {
                PinCushion p = Buff.affect(enemy, PinCushion.class);
                if (p.target == enemy) {
                    p.stick(d);
                    return;
                }
            }
            Dungeon.level.drop(d, enemy.pos).sprite.drop();
        }
    }

    @Override
    protected float durabilityPerUse() {
        float use = super.durabilityPerUse();

        if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
            use /= 2f;
        }

        return use;
    }

    @Override
    public int price() {
        //value of regular dart plus half of the seed
        return 8 * quantity;
    }

    private static final HashMap<Class<? extends Plant.Seed>, Class<? extends TippedDart>> types = new HashMap<>();

    static {
        types.put(Blindweed.Seed.class, BlindingDart.class);
        types.put(Dreamfoil.Seed.class, SleepDart.class);
        types.put(Earthroot.Seed.class, ParalyticDart.class);
        types.put(Fadeleaf.Seed.class, DisplacingDart.class);
        types.put(Firebloom.Seed.class, IncendiaryDart.class);
        types.put(Icecap.Seed.class, ChillingDart.class);
        types.put(Rotberry.Seed.class, RotDart.class);
        types.put(Sorrowmoss.Seed.class, PoisonDart.class);
        types.put(Starflower.Seed.class, HolyDart.class);
        types.put(Stormvine.Seed.class, ShockingDart.class);
        types.put(Sungrass.Seed.class, HealingDart.class);
        types.put(Swiftthistle.Seed.class, AdrenalineDart.class);
    }

    public static TippedDart getTipped(Plant.Seed s, int quantity) {
        return (TippedDart) Reflection.newInstance(types.get(s.getClass())).quantity(quantity);
    }

    public static TippedDart randomTipped(int quantity) {
        Plant.Seed s;
        do {
            s = (Plant.Seed) Generator.random(Generator.Category.SEED);
        } while (!types.containsKey(s.getClass()));

        return getTipped(s, quantity);

    }

}
