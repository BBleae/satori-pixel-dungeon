package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Displacing extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        if (Random.Int(12) == 0 && !defender.properties().contains(Char.Property.IMMOVABLE)) {
            int count = 10;
            int newPos;
            do {
                newPos = Dungeon.level.randomRespawnCell();
                if (count-- <= 0) {
                    break;
                }
            } while (newPos == -1);

            if (newPos != -1 && !Dungeon.bossLevel()) {

                if (Dungeon.level.heroFOV[defender.pos]) {
                    CellEmitter.get(defender.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                }

                defender.pos = newPos;
                if (defender instanceof Mob && ((Mob) defender).state == ((Mob) defender).HUNTING) {
                    ((Mob) defender).state = ((Mob) defender).WANDERING;
                }
                defender.sprite.place(defender.pos);
                defender.sprite.visible = Dungeon.level.heroFOV[defender.pos];

                return 0;

            }
        }

        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

}
