package studio.baka.satoripixeldungeon.items.weapon.melee;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.bags.Bag;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.items.wands.*;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndItem;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Objects;

public class MahoStaff extends MeleeWeapon {

    private Wand wand;

    public static final String AC_IMBUE = "IMBUE";
    public static final String AC_ZAP = "ZAP";
    public static final String AC_CHARGE = "CHARGE";

    private static final float STAFF_SCALE_FACTOR = 0.75f;

    {
        image = ItemSpriteSheet.MAGES_STAFF;

        tier = 1;

        defaultAction = AC_ZAP;
        usesTargeting = true;

        unique = true;
        bones = false;
    }

    public MahoStaff() {
        wand = new WandOfDisintegration();
        wand.identify();
        wand.cursed = false;
        updateWand(false);
        wand.curCharges = wand.maxCharges;
        name = Messages.get(wand, "staff_name");
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //8 base damage, down from 10
                lvl * (tier + 1);   //scaling unaffected
    }

    public MahoStaff(Wand wand) {
        this();
        wand.identify();
        wand.cursed = false;
        this.wand = wand;
        updateWand(false);
        wand.curCharges = wand.maxCharges;
        name = Messages.get(wand, "staff_name");
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_IMBUE);
        if (wand != null && wand.curCharges > 0) {
            actions.add(AC_ZAP);
        }
        if (Objects.requireNonNull(wand).curCharges < wand.maxCharges && hero.heroClass == HeroClass.MAHOU_SHOUJO) {
            actions.add(AC_CHARGE);
        }
        return actions;
    }

    @Override
    public void activate(Char ch) {
        if (wand != null) wand.charge(ch, STAFF_SCALE_FACTOR);
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        switch (action) {
            case AC_IMBUE:

                curUser = hero;
                GameScene.selectItem(itemSelector, WndBag.Mode.WAND, Messages.get(this, "prompt"));

                break;
            case AC_ZAP:

                if (wand == null) {
                    GameScene.show(new WndItem(null, this, true));
                    return;
                }

                wand.cursed = cursed || hasCurseEnchant();
                wand.execute(hero, AC_ZAP);
                break;
            case AC_CHARGE:
                curUser = hero;
                curItem = this;
                wand.ForceCharge(hero);
                break;
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (wand != null &&
                attacker instanceof Hero && ((Hero) attacker).subClass == HeroSubClass.BATTLEMAGE) {
            if (wand.curCharges < wand.maxCharges) wand.partialCharge += 0.33f;
            ScrollOfRecharging.charge((Hero) attacker);
            wand.onHit(this, attacker, defender, damage);
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = super.reachFactor(owner);
        if (owner instanceof Hero
                && wand instanceof WandOfDisintegration
                && ((Hero) owner).subClass == HeroSubClass.BATTLEMAGE) {
            reach++;
        }
        return reach;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner != null && wand != null) {
                wand.charge(container.owner, STAFF_SCALE_FACTOR);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDetach() {
        if (wand != null) wand.stopCharging();
    }

    public Item imbueWand(Wand wand, Char owner) {

        //this.wand = null;

        //syncs the level of the two items.
        //int targetLevel = Math.max(this.level() - (curseInfusionBonus ? 1 : 0), wand.level());

        //if the staff's level is being overridden by the wand, preserve 1 upgrade
        //if (wand.level() >= this.level() && this.level() > (curseInfusionBonus ? 1 : 0)) targetLevel++;

        int targetLevel = this.level();
        for (int i = 0; i <= wand.level(); i++)
            if (targetLevel < 10) targetLevel++;

        level(targetLevel);
        this.wand = new WandOfDisintegration();
        this.wand.identify();
        updateWand(false);
        this.wand.curCharges = this.wand.maxCharges;
        if (owner != null) wand.charge(owner);

        name = Messages.get(wand, "staff_name");

        //This is necessary to reset any particles.
        //FIXME this is gross, should implement a better way to fully reset quickslot visuals
        int slot = Dungeon.quickslot.getSlot(this);
        if (slot != -1) {
            Dungeon.quickslot.clearSlot(slot);
            updateQuickslot();
            Dungeon.quickslot.setSlot(slot, this);
            updateQuickslot();
        }

        Badges.validateItemLevelAquired(this);

        return this;
    }

    public void gainCharge(float amt) {
        if (wand != null) {
            wand.gainCharge(amt);
        }
    }

    public Class<? extends Wand> wandClass() {
        return wand != null ? wand.getClass() : null;
    }

    @Override
    public Item upgrade(boolean enchant) {
        super.upgrade(enchant);

        updateWand(true);

        return this;
    }

    @Override
    public Item degrade() {
        super.degrade();

        updateWand(false);

        return this;
    }

    public void updateWand(boolean levelled) {
        if (wand != null) {
            int curCharges = wand.curCharges;
            wand.level(level());
            //gives the wand one additional max charge
            wand.maxCharges = Math.min(wand.maxCharges + 1, 10);
            wand.curCharges = Math.min(curCharges + (levelled ? 1 : 0), wand.maxCharges);
            updateQuickslot();
        }
    }

    @Override
    public String status() {
        wand.levelKnown = true;
        return wand.status();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public String info() {
        String info = super.info();

        if (wand != null) {
            info += "\n\n" + Messages.get(this, "has_wand") + " " + wand.statsDesc();
        }

        return info;
    }

    @Override
    public Emitter emitter() {
        if (wand == null) return null;
        Emitter emitter = new Emitter();
        emitter.pos(12.5f, 3);
        emitter.fillTarget = false;
        emitter.pour(StaffParticleFactory, 0.1f);
        return emitter;
    }

    private static final String WAND = "wand";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WAND, wand);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        wand = (Wand) bundle.get(WAND);
        if (wand != null) {
            wand.maxCharges = Math.min(wand.maxCharges + 1, 10);
            name = Messages.get(wand, "staff_name");
        }
    }

    @Override
    public int price() {
        return 0;
    }

    @Override
    public Weapon enchant(Enchantment ench) {
        if (curseInfusionBonus && (ench == null || !ench.curse())) {
            curseInfusionBonus = false;
            updateWand(false);
        }
        return super.enchant(ench);
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(final Item item) {
            if (item != null) {

                if (!item.isIdentified()) {
                    GLog.w(Messages.get(MahoStaff.class, "id_first"));
                    return;
                } else if (item.cursed) {
                    GLog.w(Messages.get(MahoStaff.class, "cursed"));
                    return;
                }

                if (wand == null) {
                    applyWand((Wand) item);
                } else {
                    final int newLevel = Math.min((item.level() + level() + 1), 10);
                    GameScene.show(
                            new WndOptions("",
                                    Messages.get(MahoStaff.class, "warning", newLevel),
                                    Messages.get(MahoStaff.class, "yes"),
                                    Messages.get(MahoStaff.class, "no")) {
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        applyWand((Wand) item);
                                    }
                                }
                            }
                    );
                }
            }
        }

        private void applyWand(Wand wand) {
            Sample.INSTANCE.play(Assets.SND_LEVELUP);
            curUser.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
            evoke(curUser);

            Dungeon.quickslot.clearItem(wand);

            wand.detach(curUser.belongings.backpack);

            GLog.p(Messages.get(MahoStaff.class, "imbue", wand.name()));
            imbueWand(wand, curUser);

            updateQuickslot();
        }
    };

    private final Emitter.Factory StaffParticleFactory = new Emitter.Factory() {
        @Override
        //reimplementing this is needed as instance creation of new staff particles must be within this class.
        public void emit(Emitter emitter, int index, float x, float y) {
            StaffParticle c = (StaffParticle) emitter.getFirstAvailable(StaffParticle.class);
            if (c == null) {
                c = new StaffParticle();
                emitter.add(c);
            }
            c.reset(x, y);
        }

        @Override
        //some particles need light mode, others don't
        public boolean lightMode() {
            return !((wand instanceof WandOfDisintegration)
                    || (wand instanceof WandOfCorruption)
                    || (wand instanceof WandOfCorrosion)
                    || (wand instanceof WandOfRegrowth)
                    || (wand instanceof WandOfLivingEarth));
        }
    };

    //determines particle effects to use based on wand the staff owns.
    public class StaffParticle extends PixelParticle {

        private float minSize;
        private float maxSize;
        public float sizeJitter = 0;

        public StaffParticle() {
            super();
        }

        public void reset(float x, float y) {
            revive();

            speed.set(0);

            this.x = x;
            this.y = y;

            if (wand != null)
                wand.staffFx(this);

        }

        public void setSize(float minSize, float maxSize) {
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public void setLifespan(float life) {
            lifespan = left = life;
        }

        public void shuffleXY(float amt) {
            x += Random.Float(-amt, amt);
            y += Random.Float(-amt, amt);
        }

        public void radiateXY(float amt) {
            float hypot = (float) Math.hypot(speed.x, speed.y);
            this.x += speed.x / hypot * amt;
            this.y += speed.y / hypot * amt;
        }

        @Override
        public void update() {
            super.update();
            size(minSize + (left / lifespan) * (maxSize - minSize) + Random.Float(sizeJitter));
        }
    }
}
