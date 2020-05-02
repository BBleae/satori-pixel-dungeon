package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.ElmoParticle;
import studio.baka.satoripixeldungeon.items.Amulet;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.levels.HallowBossLevel;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.AuthorSprite;
import studio.baka.satoripixeldungeon.ui.BossHealthBar;
import studio.baka.satoripixeldungeon.utils.GLog;
import studio.baka.satoripixeldungeon.windows.WndBag;
import studio.baka.satoripixeldungeon.windows.WndMessage;
import studio.baka.satoripixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Author extends NPC {

    {
        spriteClass = AuthorSprite.class;
        properties.add(Property.BOSS);
        properties.add(Property.IMMOVABLE);
    }

    @Override
    protected boolean act() {

        throwItem();

        sprite.turnTo(pos, Dungeon.hero.pos);
        spend(TICK);
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        yell(Messages.get(this, "no_damage_" + Random.Int(0, 3)));
    }

    public void reallytakedamage(int dmg) {                //to reduce Author's HP safely
        if (!isAlive() || dmg < 0) {
            return;
        }

        if (HP < 0) HP = 0;

        if (!isAlive()) {
            die(Hero.class);
        }
    }

    @Override
    public void add(Buff buff) {

    }

    @Override
    public void destroy() {
        super.destroy();
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type == Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
                heap.destroy();
            }
        }
    }

    @Override
    public boolean reset() {
        return true;
    }

    public static WndBag sell() {
        return GameScene.selectItem(itemSelector, WndBag.Mode.ALL, Messages.get(Author.class, "ask"));
    }

    private static final WndBag.Listener itemSelector = item -> {
        if (item != null) {
            WndBag parentWnd = sell();
            GameScene.show(new WndTradeItem(item, parentWnd));
        }
    };

    private boolean haventasked = true;

    @Override
    public boolean interact() {
        if (haventasked) {
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(Messages.get(this, "question"))));
            haventasked = false;
        } else {
            switch (((HallowBossLevel) Dungeon.level).levelstat) {
                default:
                    yell("?????");
                    break;
                case not_started:
                    yell(Messages.get(this, "find_me"));
                    break;
                case maze1:
                case maze2:
                    yell(Messages.get(this, "maze_finished"));
                    break;
                case maze3:
                    yell(Messages.get(this, "puzzle_finished"));
                    break;
                case ended:
                    yell(Messages.get(this, "right_answer"));
                    break;
            }

            ((HallowBossLevel) Dungeon.level).process();
            //Game.runOnRenderThread(() -> sell());
        }

        return false;
    }

    @Override
    public void die(Object cause) {
        GameScene.bossSlain();
        Dungeon.level.drop(new Amulet(), pos).sprite.drop();
        super.die(cause);
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
            for (Char ch : Actor.chars()) {
                if (ch instanceof DriedRose.GhostHero) {
                    GLog.n("\n");
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
        }
    }

    private final String HAVENTASKED = "haventasked";

    @Override
    public void storeInBundle(Bundle bundle) {

        super.storeInBundle(bundle);
        bundle.put(HAVENTASKED, haventasked);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
        haventasked = bundle.getBoolean(HAVENTASKED);
    }
}
