package studio.baka.satoripixeldungeon.items.quest;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.Wraith;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CorpseDust extends Item {

    {
        image = ItemSpriteSheet.DUST;

        cursed = true;
        cursedKnown = true;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return new ArrayList<>(); //yup, no dropping this one
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
    public boolean doPickUp(Hero hero) {
        if (super.doPickUp(hero)) {
            GLog.n(Messages.get(this, "chill"));
            Buff.affect(hero, DustGhostSpawner.class);
            return true;
        }
        return false;
    }

    @Override
    protected void onDetach() {
        DustGhostSpawner spawner = Dungeon.hero.buff(DustGhostSpawner.class);
        if (spawner != null) {
            spawner.dispel();
        }
    }

    public static class DustGhostSpawner extends Buff {

        int spawnPower = 0;

        @Override
        public boolean act() {
            spawnPower++;
            int wraiths = 1; //we include the wraith we're trying to spawn
            for (Mob mob : Dungeon.level.mobs) {
                if (mob instanceof Wraith) {
                    wraiths++;
                }
            }

            int powerNeeded = Math.min(25, wraiths * wraiths);

            if (powerNeeded <= spawnPower) {
                spawnPower -= powerNeeded;
                int pos = 0;
                int tries = 20;
                do {
                    pos = Random.Int(Dungeon.level.length());
                    tries--;
                } while (tries > 0 && (!Dungeon.level.heroFOV[pos] || !Dungeon.level.passable[pos] || Actor.findChar(pos) != null));
                if (tries > 0) {
                    Wraith.spawnAt(pos);
                    Sample.INSTANCE.play(Assets.SND_CURSED);
                }
            }

            spend(TICK);
            return true;
        }

        public void dispel() {
            detach();
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob instanceof Wraith) {
                    mob.die(null);
                }
            }
        }

        private static final String SPAWNPOWER = "spawnpower";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SPAWNPOWER, spawnPower);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            spawnPower = bundle.getInt(SPAWNPOWER);
        }
    }

}
