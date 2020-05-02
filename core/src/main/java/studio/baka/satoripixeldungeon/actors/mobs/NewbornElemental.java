package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Chill;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.items.quest.Embers;
import studio.baka.satoripixeldungeon.sprites.NewbornElementalSprite;

public class NewbornElemental extends Elemental {

    {
        spriteClass = NewbornElementalSprite.class;

        HT = 65;
        HP = HT / 2; //32

        defenseSkill = 12;

        EXP = 7;

        properties.add(Property.MINIBOSS);
    }

    @Override
    public void add(Buff buff) {
        if (buff instanceof Frost || buff instanceof Chill) {
            die(buff);
        } else {
            super.add(buff);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.drop(new Embers(), pos).sprite.drop();
    }
}
