package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.actors.mobs.Rat;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.NPC;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Effects;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.ActionIndicator;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Preparation extends Buff implements ActionIndicator.Action {

    {
        //always acts after other buffs, so invisibility effects can process first
        actPriority = BUFF_PRIORITY - 1;
    }

    public enum AttackLevel {
        LVL_1(1, 0.1f, 0.0f, 1, 0),
        LVL_2(3, 0.2f, 0.0f, 1, 1),
        LVL_3(6, 0.3f, 0.0f, 2, 3),
        LVL_4(11, 0.4f, 0.6f, 2, 5),
        LVL_5(16, 0.5f, 1.0f, 3, 7);

        final int turnsReq;
        final float baseDmgBonus, missingHPBonus;
        final int damageRolls, blinkDistance;

        AttackLevel(int turns, float base, float missing, int rolls, int dist) {
            turnsReq = turns;
            baseDmgBonus = base;
            missingHPBonus = missing;
            damageRolls = rolls;
            blinkDistance = dist;
        }

        public boolean canInstakill(Char defender) {
            return this == LVL_5
                    && !defender.properties().contains(Char.Property.MINIBOSS)
                    && !defender.properties().contains(Char.Property.BOSS);
        }

        public int damageRoll(Char attacker, Char defender) {
            int dmg = attacker.damageRoll();
            for (int i = 1; i < damageRolls; i++) {
                int newDmg = attacker.damageRoll();
                if (newDmg > dmg) dmg = newDmg;
            }
            float defenderHPPercent = 1f - (defender.HP / (float) defender.HT);
            return Math.round(dmg * (1f + baseDmgBonus + (missingHPBonus * defenderHPPercent)));
        }

        public static AttackLevel getLvl(int turnsInvis) {
            List<AttackLevel> values = Arrays.asList(values());
            Collections.reverse(values);
            for (AttackLevel lvl : values) {
                if (turnsInvis >= lvl.turnsReq) {
                    return lvl;
                }
            }
            return LVL_1;
        }
    }

    private int turnsInvis = 0;
    private boolean isnotyukari = false;
    private int koishitimelast = 2;

    public void setTurnsInvis(int value) {
        turnsInvis = value;
        isnotyukari = true;
    }

    @Override
    public boolean act() {
        if (target.invisible > 0) {

            turnsInvis++;
            if (AttackLevel.getLvl(turnsInvis).blinkDistance > 0 && target == Dungeon.hero) {
                ActionIndicator.setAction(this);
            }

            if (isnotyukari) {
                if (koishitimelast > 1) koishitimelast--;
                else {
                    detach();
                }
            }

            BuffIndicator.refreshHero();
            spend(TICK);
        } else {
            detach();
        }
        return true;
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    public int damageRoll(Char attacker, Char defender) {
        AttackLevel lvl = AttackLevel.getLvl(turnsInvis);
        if (lvl.canInstakill(defender)) {
            int dmg = lvl.damageRoll(attacker, defender);
            defender.damage(Math.max(defender.HT, dmg), attacker);
            //even though the defender is dead, other effects should still proc (enchants, etc.)
            return Math.max(defender.HT, dmg);
        } else {
            return lvl.damageRoll(attacker, defender);
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.PREPARATION;
    }

    @Override
    public void tintIcon(Image icon) {
        switch (AttackLevel.getLvl(turnsInvis)) {
            case LVL_1:
                icon.hardlight(1f, 1f, 1f);
                break;
            case LVL_2:
                icon.hardlight(0f, 1f, 0f);
                break;
            case LVL_3:
                icon.hardlight(1f, 1f, 0f);
                break;
            case LVL_4:
                icon.hardlight(1f, 0.6f, 0f);
                break;
            case LVL_5:
                icon.hardlight(1f, 0f, 0f);
                break;
        }
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc");
        if (Dungeon.hero.heroClass == HeroClass.WARRIOR)
            desc = Messages.get(this, "desck");

        AttackLevel lvl = AttackLevel.getLvl(turnsInvis);

        final int v = (int) (lvl.baseDmgBonus * 100 + lvl.missingHPBonus * 100);
        if (lvl.canInstakill(new Rat())) {
            desc += "\n\n" + Messages.get(this, "desc_dmg_instakill",
                    (int) (lvl.baseDmgBonus * 100), v);
        } else if (lvl.missingHPBonus > 0) {
            desc += "\n\n" + Messages.get(this, "desc_dmg_scale",
                    (int) (lvl.baseDmgBonus * 100), v);
        } else {
            desc += "\n\n" + Messages.get(this, "desc_dmg", (int) (lvl.baseDmgBonus * 100));
        }

        if (lvl.damageRolls > 1) {
            desc += " " + Messages.get(this, "desc_dmg_likely");
        }

        if (lvl.blinkDistance > 0) {
            if (Dungeon.hero.heroClass == HeroClass.WARRIOR)
                desc += "\n\n" + Messages.get(this, "desc_blinkk", lvl.blinkDistance);
            else
                desc += "\n\n" + Messages.get(this, "desc_blink", lvl.blinkDistance);
        }

        if (Dungeon.hero.heroClass == HeroClass.WARRIOR)
            desc += "\n\n" + Messages.get(this, "desc_invis_timek", turnsInvis);
        else
            desc += "\n\n" + Messages.get(this, "desc_invis_time", turnsInvis);

        if (lvl.ordinal() != AttackLevel.values().length - 1) {
            AttackLevel next = AttackLevel.values()[lvl.ordinal() + 1];
            if (Dungeon.hero.heroClass != HeroClass.WARRIOR)
                desc += "\n" + Messages.get(this, "desc_invis_next", next.turnsReq);
        }

        return desc;
    }

    private static final String TURNS = "turnsInvis";
    private static final String ISNOTYOKARI = "isnotyokari";
    private static final String KOISHITIMELAST = "kstl";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnsInvis = bundle.getInt(TURNS);
        isnotyukari = bundle.getBoolean(ISNOTYOKARI);
        koishitimelast = bundle.getInt(KOISHITIMELAST);
        if (AttackLevel.getLvl(turnsInvis).blinkDistance > 0) {
            ActionIndicator.setAction(this);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TURNS, turnsInvis);
        bundle.put(ISNOTYOKARI, isnotyukari);
        bundle.put(KOISHITIMELAST, koishitimelast);
    }

    @Override
    public Image getIcon() {
        Image actionIco = Effects.get(Effects.Type.WOUND);
        tintIcon(actionIco);
        return actionIco;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(attack);
    }

    private final CellSelector.Listener attack = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            final Char enemy = Actor.findChar(cell);
            if (enemy == null || Dungeon.hero.isCharmedBy(enemy) || enemy instanceof NPC) {
                GLog.w(Messages.get(Preparation.class, "no_target"));
            } else {

                //just attack them then!
                if (Dungeon.hero.canAttack(enemy)) {
                    if (Dungeon.hero.handle(cell)) {
                        Dungeon.hero.next();
                    }
                    return;
                }

                AttackLevel lvl = AttackLevel.getLvl(turnsInvis);

                boolean[] passable = Dungeon.level.passable.clone();
                //need to consider enemy cell as passable in case they are on a trap or chasm
                passable[cell] = true;
                PathFinder.buildDistanceMap(Dungeon.hero.pos, passable, lvl.blinkDistance + 1);
                if (PathFinder.distance[cell] == Integer.MAX_VALUE) {
                    GLog.w(Messages.get(Preparation.class, "out_of_reach"));
                    return;
                }

                //we can move through enemies when determining blink distance,
                // but not when actually jumping to a location
                for (Char ch : Actor.chars()) {
                    if (ch != Dungeon.hero) passable[ch.pos] = false;
                }

                PathFinder.Path path = PathFinder.find(Dungeon.hero.pos, cell, passable);
                int attackPos = path == null ? -1 : path.get(path.size() - 2);

                if (attackPos == -1 ||
                        Dungeon.level.distance(attackPos, Dungeon.hero.pos) > lvl.blinkDistance) {
                    GLog.w(Messages.get(Preparation.class, "out_of_reach"));
                    return;
                }

                Dungeon.hero.pos = attackPos;
                Dungeon.level.occupyCell(Dungeon.hero);
                //prevents the hero from being interrupted by seeing new enemies
                Dungeon.observe();
                Dungeon.hero.checkVisibleMobs();

                Dungeon.hero.sprite.place(Dungeon.hero.pos);
                Dungeon.hero.sprite.turnTo(Dungeon.hero.pos, cell);
                CellEmitter.get(Dungeon.hero.pos).burst(Speck.factory(Speck.WOOL), 6);
                Sample.INSTANCE.play(Assets.SND_PUFF);

                if (Dungeon.hero.handle(cell)) {
                    Dungeon.hero.next();
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Preparation.class, "prompt", AttackLevel.getLvl(turnsInvis).blinkDistance);
        }
    };
}
