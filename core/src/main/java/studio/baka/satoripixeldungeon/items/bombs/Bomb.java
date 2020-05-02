package studio.baka.satoripixeldungeon.items.bombs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.DM300;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.BlastParticle;
import studio.baka.satoripixeldungeon.effects.particles.SmokeParticle;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.items.potions.PotionOfFrost;
import studio.baka.satoripixeldungeon.items.potions.PotionOfHealing;
import studio.baka.satoripixeldungeon.items.potions.PotionOfInvisibility;
import studio.baka.satoripixeldungeon.items.potions.PotionOfLiquidFlame;
import studio.baka.satoripixeldungeon.items.quest.GooBlob;
import studio.baka.satoripixeldungeon.items.quest.MetalShard;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMirrorImage;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRage;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRecharging;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import studio.baka.satoripixeldungeon.messages.Languages;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Bomb extends Item {

    {
        image = ItemSpriteSheet.BOMB;

        defaultAction = AC_LIGHTTHROW;
        usesTargeting = true;

        stackable = true;
    }

    public Fuse fuse;

    //FIXME using a static variable for this is kinda gross, should be a better way
    private static boolean lightingFuse = false;

    private static final String AC_LIGHTTHROW = "LIGHTTHROW";

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && this.fuse == ((Bomb) item).fuse;
    }

    public boolean explodesDestructively() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_LIGHTTHROW);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        if (action.equals(AC_LIGHTTHROW)) {
            lightingFuse = true;
            action = AC_THROW;
        } else
            lightingFuse = false;

        super.execute(hero, action);
    }

    @Override
    protected void onThrow(int cell) {
        if (!Dungeon.level.pit[cell] && lightingFuse) {
            Actor.addDelayed(fuse = new Fuse().ignite(this), 2);
        }
        if (Actor.findChar(cell) != null && !(Actor.findChar(cell) instanceof Hero)) {
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8)
                if (Dungeon.level.passable[cell + i])
                    candidates.add(cell + i);
            int newCell = candidates.isEmpty() ? cell : Random.element(candidates);
            Dungeon.level.drop(this, newCell).sprite.drop(cell);
        } else
            super.onThrow(cell);
    }

    @Override
    public boolean doPickUp(Hero hero) {
        if (fuse != null) {
            GLog.w(Messages.get(this, "snuff_fuse"));
            fuse = null;
        }
        return super.doPickUp(hero);
    }

    public void explode(int cell) {
        explode(cell, 0, false);
    }

    public void explode(int cell, int setdmg, boolean isDM) {
        //We're blowing up, so no need for a fuse anymore.
        this.fuse = null;
        Sample.INSTANCE.play(Assets.SND_BLAST);

        if (explodesDestructively()) {

            ArrayList<Char> affected = new ArrayList<>();

            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
            }

            boolean terrainAffected = false;
            for (int n : PathFinder.NEIGHBOURS9) {
                int c = cell + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                    }

                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                        terrainAffected = true;
                    }

                    //destroys items / triggers bombs caught in the blast.
                    Heap heap = Dungeon.level.heaps.get(c);
                    if (heap != null)
                        heap.explode();

                    Char ch = Actor.findChar(c);
                    if (ch != null) {
                        affected.add(ch);
                    }
                }
            }

            for (Char ch : affected) {
                int dmg = (setdmg != 0 ? setdmg : Random.NormalIntRange(5 + Dungeon.depth, 10 + Dungeon.depth * 2));

                //those not at the center of the blast take less damage
                if (ch.pos != cell) {
                    dmg = Math.round(dmg * 0.67f);
                }

                dmg -= ch.drRoll();

                if (dmg > 0) {
                    if (!isDM) ch.damage(dmg, this);
                    if (isDM && !(ch instanceof DM300)) ch.damage(dmg, this);
                }

                if (ch == Dungeon.hero && !ch.isAlive()) {
                    Dungeon.fail(Bomb.class);
                }
            }

            if (terrainAffected) {
                Dungeon.observe();
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public Item random() {
        if (Random.Int(4) == 0) {
            return new DoubleBomb();
        }
        return this;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return fuse != null ? new ItemSprite.Glowing(0xFF0000, 0.6f) : null;
    }

    @Override
    public int price() {
        return 20 * quantity;
    }

    @Override
    public String desc() {
        if (fuse == null)
            return super.desc() + "\n\n" + Messages.get(this, "desc_fuse");
        else
            return super.desc() + "\n\n" + Messages.get(this, "desc_burning");
    }

    private static final String FUSE = "fuse";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FUSE, fuse);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(FUSE))
            Actor.add(fuse = ((Fuse) bundle.get(FUSE)).ignite(this));
    }


    public static class Fuse extends Actor {

        {
            actPriority = BLOB_PRIO + 1; //after hero, before other actors
        }

        private Bomb bomb;

        public Fuse ignite(Bomb bomb) {
            this.bomb = bomb;
            return this;
        }

        @Override
        protected boolean act() {

            //something caused our bomb to explode early, or be defused. Do nothing.
            if (bomb.fuse != this) {
                Actor.remove(this);
                return true;
            }

            //look for our bomb, remove it from its heap, and blow it up.
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.items.contains(bomb)) {

                    //FIXME this is a bit hacky, might want to generalize the functionality
                    //of bombs that don't explode instantly when their fuse ends
                    if (bomb instanceof Noisemaker) {

                        ((Noisemaker) bomb).setTrigger(heap.pos);

                    } else {

                        heap.items.remove(bomb);
                        if (heap.items.isEmpty()) {
                            heap.destroy();
                        }

                        bomb.explode(heap.pos);
                    }

                    diactivate();
                    Actor.remove(this);
                    return true;
                }
            }

            //can't find our bomb, something must have removed it, do nothing.
            bomb.fuse = null;
            Actor.remove(this);
            return true;
        }
    }


    public static class DoubleBomb extends Bomb {

        {
            image = ItemSpriteSheet.DBL_BOMB;
            stackable = false;
        }

        @Override
        public boolean doPickUp(Hero hero) {
            Bomb bomb = new Bomb();
            bomb.quantity(2);
            if (bomb.doPickUp(hero)) {
                //isaaaaac.... (don't bother doing this when not in english)
                if (SPDSettings.language() == Languages.CHINESE)
                    hero.sprite.showStatus(CharSprite.NEUTRAL, "超值1+1");
                return true;
            }
            return false;
        }
    }

    public static class EnhanceBomb extends Recipe {

        public static final LinkedHashMap<Class<? extends Item>, Class<? extends Bomb>> validIngredients = new LinkedHashMap<>();

        static {
            validIngredients.put(PotionOfFrost.class, FrostBomb.class);
            validIngredients.put(ScrollOfMirrorImage.class, WoollyBomb.class);

            validIngredients.put(PotionOfLiquidFlame.class, Firebomb.class);
            validIngredients.put(ScrollOfRage.class, Noisemaker.class);

            validIngredients.put(PotionOfInvisibility.class, Flashbang.class);
            validIngredients.put(ScrollOfRecharging.class, ShockBomb.class);

            validIngredients.put(PotionOfHealing.class, RegrowthBomb.class);
            validIngredients.put(ScrollOfRemoveCurse.class, HolyBomb.class);

            validIngredients.put(GooBlob.class, ArcaneBomb.class);
            validIngredients.put(MetalShard.class, ShrapnelBomb.class);
        }

        private static final HashMap<Class<? extends Bomb>, Integer> bombCosts = new HashMap<>();

        static {
            bombCosts.put(FrostBomb.class, 2);
            bombCosts.put(WoollyBomb.class, 2);

            bombCosts.put(Firebomb.class, 4);
            bombCosts.put(Noisemaker.class, 4);

            bombCosts.put(Flashbang.class, 6);
            bombCosts.put(ShockBomb.class, 6);

            bombCosts.put(RegrowthBomb.class, 8);
            bombCosts.put(HolyBomb.class, 8);

            bombCosts.put(ArcaneBomb.class, 10);
            bombCosts.put(ShrapnelBomb.class, 10);
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean bomb = false;
            boolean ingredient = false;

            for (Item i : ingredients) {
                if (!i.isIdentified()) return false;
                if (i.getClass().equals(Bomb.class)) {
                    bomb = true;
                } else if (validIngredients.containsKey(i.getClass())) {
                    ingredient = true;
                }
            }

            return bomb && ingredient;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (validIngredients.containsKey(i.getClass())) {
                    return (bombCosts.get(validIngredients.get(i.getClass())));
                }
            }
            return 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = null;

            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
                if (validIngredients.containsKey(i.getClass())) {
                    result = Reflection.newInstance(validIngredients.get(i.getClass()));
                }
            }

            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                if (validIngredients.containsKey(i.getClass())) {
                    return Reflection.newInstance(validIngredients.get(i.getClass()));
                }
            }
            return null;
        }
    }
}
