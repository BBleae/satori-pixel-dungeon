package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.CharSprite;
import studio.baka.satoripixeldungeon.sprites.SheepSprite;
import com.watabou.utils.Random;

public class Sheep extends NPC {

    private static final String[] LINE_KEYS = {"Baa!", "Baa?", "Baa.", "Baa..."};

    {
        spriteClass = SheepSprite.class;
    }

    public float lifespan;

    private boolean initialized = false;

    @Override
    protected boolean act() {
        if (initialized) {
            HP = 0;

            destroy();
            sprite.die();

        } else {
            initialized = true;
            spend(lifespan + Random.Float(2));
        }
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 100_000_000;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public void add(Buff buff) {
    }

    @Override
    public boolean interact() {
        sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, Random.element(LINE_KEYS)));
        Dungeon.hero.spendAndNext(1f);
        return false;
    }
}