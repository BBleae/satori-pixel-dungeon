package studio.baka.satoripixeldungeon.items.artifacts;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.wands.WandOfLivingEarth;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Earthroot;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class ChaliceOfBlood extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_CHALICE1;

        levelCap = 10;
    }

    public static final String AC_PRICK = "PRICK";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && level() < levelCap && !cursed)
            actions.add(AC_PRICK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_PRICK)) {

            int damage = 3 * (level() * level());

            if (damage > hero.HP * 0.75) {

                GameScene.show(
                        new WndOptions(Messages.titleCase(Messages.get(this, "name")),
                                Messages.get(this, "prick_warn"),
                                Messages.get(this, "yes"),
                                Messages.get(this, "no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0)
                                    prick(Dungeon.hero);
                            }
                        }
                );

            } else {
                prick(hero);
            }
        }
    }

    private int countdmg(Hero hero) {
        return 3 * (level() * level());
    }

    private void prick(Hero hero) {
        int damage = countdmg(hero);
        Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
        if (armor != null) {
            damage = armor.absorb(damage);
        }

        WandOfLivingEarth.RockArmor rockArmor = hero.buff(WandOfLivingEarth.RockArmor.class);
        if (rockArmor != null) {
            damage = rockArmor.absorb(damage);
        }
        damage -= hero.drRoll();

        hero.sprite.operate(hero.pos);
        hero.busy();
        hero.spend(3f);
        GLog.w(Messages.get(this, "onprick"));
        if (damage <= 0) {
            damage = 1;
        } else {
            Sample.INSTANCE.play(Assets.SND_CURSED);
            hero.sprite.emitter().burst(ShadowParticle.CURSE, 4 + (damage / 10));
        }

        hero.damage(damage, this);

        if (!hero.isAlive()) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        } else {
            upgrade();
        }
    }

    @Override
    public Item upgrade() {
        if (level() >= 6)
            image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        else if (level() >= 2)
            image = ItemSpriteSheet.ARTIFACT_CHALICE2;
        return super.upgrade();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (level() >= 7) image = ItemSpriteSheet.ARTIFACT_CHALICE3;
        else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_CHALICE2;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new chaliceRegen();
    }

    @Override
    public void charge(Hero target) {
        target.HP = Math.min(target.HT, target.HP + 1 + Dungeon.depth / 5);
    }

    @Override
    public String desc() {
        String desc;

        if (level() < levelCap) {
            desc = Messages.get(this, "desc", countdmg(curUser));
        } else {
            desc = Messages.get(this, "desc_maxlvl");
        }

        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else if (level() == 0)
                desc += Messages.get(this, "desc_1");
            else if (level() < levelCap)
                desc += Messages.get(this, "desc_2");
            else
                desc += Messages.get(this, "desc_3");
        }

        return desc;
    }

    public class chaliceRegen extends ArtifactBuff {

    }

}
