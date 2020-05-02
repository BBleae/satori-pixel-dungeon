package studio.baka.satoripixeldungeon.actors.mobs.npcs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.sprites.RatKingSprite;

public class RatKing extends NPC {

    {
        spriteClass = RatKingSprite.class;

        state = SLEEPING;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 100_000_000;
    }

    @Override
    public float speed() {
        return 2f;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public void add(Buff buff) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact() {
        sprite.turnTo(pos, Dungeon.hero.pos);
        if (state == SLEEPING) {
            notice();
            yell(Messages.get(this, "not_sleeping"));
            state = WANDERING;
        } else {
            yell(Messages.get(this, "what_is_it"));
        }
        return true;
    }

    @Override
    public String description() {
        return ((RatKingSprite) sprite).festive ?
                Messages.get(this, "desc_festive")
                : super.description();
    }
}
