package studio.baka.satoripixeldungeon.items.spells;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.artifacts.TimekeepersHourglass;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Swiftthistle;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class BeaconOfReturning extends Spell {

    {
        image = ItemSpriteSheet.RETURN_BEACON;
    }

    public int returnDepth = -1;
    public int returnPos;

    @Override
    protected void onCast(final Hero hero) {

        if (returnDepth == -1) {
            setBeacon(hero);
        } else {
            GameScene.show(new WndOptions(Messages.titleCase(name()),
                    Messages.get(BeaconOfReturning.class, "wnd_body"),
                    Messages.get(BeaconOfReturning.class, "wnd_set"),
                    Messages.get(BeaconOfReturning.class, "wnd_return")) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        setBeacon(hero);
                    } else if (index == 1) {
                        returnBeacon(hero);
                    }
                }
            });

        }
    }

    //we reset return depth when beacons are dropped to prevent
    //having two stacks of beacons with different return locations

    @Override
    protected void onThrow(int cell) {
        returnDepth = -1;
        super.onThrow(cell);
    }

    @Override
    public void doDrop(Hero hero) {
        returnDepth = -1;
        super.doDrop(hero);
    }

    private void setBeacon(Hero hero) {
        returnDepth = Dungeon.depth;
        returnPos = hero.pos;

        hero.spend(1f);
        hero.busy();

        GLog.i(Messages.get(this, "set"));

        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play(Assets.SND_BEACON);
        updateQuickslot();
    }

    private void returnBeacon(Hero hero) {
        if (Dungeon.bossLevel()) {
            GLog.w(Messages.get(this, "preventing"));
            return;
        }

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            Char ch = Actor.findChar(hero.pos + PathFinder.NEIGHBOURS8[i]);
            if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                GLog.w(Messages.get(this, "creatures"));
                return;
            }
        }

        if (returnDepth == Dungeon.depth) {
            ScrollOfTeleportation.appear(hero, returnPos);
            for (Mob m : Dungeon.level.mobs) {
                if (m.pos == hero.pos) {
                    //displace mob
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (Actor.findChar(m.pos + i) == null && Dungeon.level.passable[m.pos + i]) {
                            m.pos += i;
                            m.sprite.point(m.sprite.worldToCamera(m.pos));
                            break;
                        }
                    }
                }
            }
            Dungeon.level.occupyCell(hero);
            Dungeon.observe();
            GameScene.updateFog();
        } else {

            Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
            if (buff != null) buff.detach();
            buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
            if (buff != null) buff.detach();

            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            InterlevelScene.returnDepth = returnDepth;
            InterlevelScene.returnPos = returnPos;
            Game.switchScene(InterlevelScene.class);
        }
        detach(hero.belongings.backpack);
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (returnDepth != -1) {
            desc += "\n\n" + Messages.get(this, "desc_set", returnDepth);
        }
        return desc;
    }

    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);

    @Override
    public ItemSprite.Glowing glowing() {
        return returnDepth != -1 ? WHITE : null;
    }

    private static final String DEPTH = "depth";
    private static final String POS = "pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, returnDepth);
        if (returnDepth != -1) {
            bundle.put(POS, returnPos);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        returnDepth = bundle.getInt(DEPTH);
        returnPos = bundle.getInt(POS);
    }

    @Override
    public int price() {
        //prices of ingredients, divided by output quantity
        return Math.round(quantity * ((50 + 40) / 5f));
    }

    public static class Recipe extends studio.baka.satoripixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfPassage.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 10;

            output = BeaconOfReturning.class;
            outQuantity = 5;
        }

    }
}
