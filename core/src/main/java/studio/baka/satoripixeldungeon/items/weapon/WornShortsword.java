package studio.baka.satoripixeldungeon.items.weapon;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WornShortsword extends Weapon {

    public static final String AC_TP = "TP";
    public boolean sniperSpecial = false;
    public int tier;
    public static final int manaRequire = 6;


    {
        defaultAction = AC_TP;

        usesTargeting = false;

        unique = true;

        image = ItemSpriteSheet.WORN_SHORTSWORD;

        tier = 0;

        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        //actions.add(AC_EQUIP);
		/*
		if (hero.heroClass == HeroClass.WARRIOR) {
			actions.add(AC_TP);
		}
		*/
        return super.actions(hero);
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        //The function of teleport has been removed
		/*
		if (action.equals(AC_TP)) {

			curUser = hero;
			curItem = this;
			//GameScene.selectCell(shooter);
			if (hero.mana>=manaRequire){
				new ScrollOfTeleportation().doRead2();
				hero.mana-=manaRequire;
			}
			else{
				GLog.w( Messages.get(this, "not_enough_mana"),hero.mana,hero.getMaxmana(),manaRequire);
			}
		}
		*/
    }

    @Override
    public String info() {

        String info = desc();

        if (levelKnown) {
            info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
            if (STRReq() > Dungeon.hero.STR()) {
                info += " " + Messages.get(Weapon.class, "too_heavy");
            } else if (Dungeon.hero.STR() > STRReq()) {
                info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
            }
        } else {
            info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
            if (STRReq(0) > Dungeon.hero.STR()) {
                info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
            }
        }

        String statsInfo = statsInfo();
        if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())) {
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursed && isEquipped(Dungeon.hero)) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown) {
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        return info;
    }

    public String statsInfo() {
        return Messages.get(this, "stats_desc");
    }

    @Override
    public int price() {
        int price = 20 * tier;
        if (hasGoodEnchant()) {
            price *= 1.5;
        }
        if (cursedKnown && (cursed || hasCurseEnchant())) {
            price /= 2;
        }
        if (levelKnown && level() > 0) {
            price *= (level() + 1);
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    @Override
    public int min(int lvl) {
        return (int) Math.pow(lvl, 2);
    }

    @Override
    public int max(int lvl) {
        return (int) Math.pow(lvl, 2) + 1;
    }

    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        //strength req decreases at +1,+3,+6,+10,etc.
        return (8 + tier * 2) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public int damageRoll(Char owner) {
        int damage = augment.damageFactor(super.damageRoll(owner));

        if (owner instanceof Hero) {
            int exStr = ((Hero) owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr);
            }
        }
        return damage;
    }

    private final CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                knockArrow().cast(curUser, target);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public WornShortsword.SpiritArrow knockArrow() {
        return new WornShortsword.SpiritArrow();
    }

    public class SpiritArrow extends MissileWeapon {

        {
            image = ItemSpriteSheet.SPIRIT_ARROW;
        }

        @Override
        public int damageRoll(Char owner) {
            return WornShortsword.this.damageRoll(owner);
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return WornShortsword.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return WornShortsword.this.proc(attacker, defender, damage);
        }

        @Override
        public float speedFactor(Char user) {
            return WornShortsword.this.speedFactor(user);
        }

        @Override
        public float accuracyFactor(Char owner) {
            if (sniperSpecial && WornShortsword.this.augment == Augment.DAMAGE) {
                return Float.POSITIVE_INFINITY;
            } else {
                return super.accuracyFactor(owner);
            }
        }
    }
}
