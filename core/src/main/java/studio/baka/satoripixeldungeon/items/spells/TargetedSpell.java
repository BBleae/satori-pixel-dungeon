package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.MagicMissile;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.CellSelector;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public abstract class TargetedSpell extends Spell {

    protected int collisionProperties = Ballistica.PROJECTILE;

    @Override
    protected void onCast(Hero hero) {
        GameScene.selectCell(targeter);
    }

    protected abstract void affectTarget(Ballistica bolt, Hero hero);

    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.MAGIC_MISSILE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    private static final CellSelector.Listener targeter = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {

            if (target != null) {

                //FIXME this safety check shouldn't be necessary
                //it would be better to eliminate the curItem static variable.
                final TargetedSpell curSpell;
                if (curItem instanceof TargetedSpell) {
                    curSpell = (TargetedSpell) curItem;
                } else {
                    return;
                }

                final Ballistica shot = new Ballistica(curUser.pos, target, curSpell.collisionProperties);
                int cell = shot.collisionPos;

                curUser.sprite.zap(cell);

                //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));

                curUser.busy();
                Invisibility.dispel();

                curSpell.fx(shot, () -> {
                    curSpell.affectTarget(shot, curUser);
                    curSpell.detach(curUser.belongings.backpack);
                    updateQuickslot();
                    curUser.spendAndNext(1f);
                });

            }

        }

        @Override
        public String prompt() {
            return Messages.get(TargetedSpell.class, "prompt");
        }
    };

}
