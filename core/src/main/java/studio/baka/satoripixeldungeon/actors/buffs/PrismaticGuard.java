package studio.baka.satoripixeldungeon.actors.buffs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.PrismaticImage;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class PrismaticGuard extends Buff {

    {
        type = buffType.POSITIVE;
    }

    private float HP;

    @Override
    public boolean act() {

        Hero hero = (Hero) target;

        Mob closest = null;
        int v = hero.visibleEnemies();
        for (int i = 0; i < v; i++) {
            Mob mob = hero.visibleEnemy(i);
            if (mob.isAlive() && mob.state != mob.PASSIVE && mob.state != mob.WANDERING && mob.state != mob.SLEEPING && !hero.mindVisionEnemies.contains(mob)
                    && (closest == null || Dungeon.level.distance(hero.pos, mob.pos) < Dungeon.level.distance(hero.pos, closest.pos))) {
                closest = mob;
            }
        }

        if (closest != null && Dungeon.level.distance(hero.pos, closest.pos) < 5) {
            //spawn guardian
            int bestPos = -1;
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                    if (bestPos == -1 || Dungeon.level.trueDistance(p, closest.pos) < Dungeon.level.trueDistance(bestPos, closest.pos)) {
                        bestPos = p;
                    }
                }
            }
            if (bestPos != -1) {
                PrismaticImage pris = new PrismaticImage();
                pris.duplicate(hero, (int) Math.floor(HP));
                pris.state = pris.HUNTING;
                GameScene.add(pris, 1);
                ScrollOfTeleportation.appear(pris, bestPos);

                detach();
            } else {
                spend(TICK);
            }


        } else {
            spend(TICK);
        }

        LockedFloor lock = target.buff(LockedFloor.class);
        if (HP < maxHP() && (lock == null || lock.regenOn())) {
            HP += 0.1f;
        }

        return true;
    }

    public void set(int HP) {
        this.HP = HP;
    }

    public int maxHP() {
        return maxHP((Hero) target);
    }

    public static int maxHP(Hero hero) {
        return 8 + (int) Math.floor(hero.lvl * 2.5f);
    }

    @Override
    public int icon() {
        return BuffIndicator.ARMOR;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.tint(0.5f, 0.5f, 1, 0.5f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int) HP, maxHP());
    }

    private static final String HEALTH = "hp";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEALTH, HP);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        HP = bundle.getFloat(HEALTH);
    }
}
