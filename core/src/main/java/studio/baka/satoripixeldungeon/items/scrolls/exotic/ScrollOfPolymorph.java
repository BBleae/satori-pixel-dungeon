package studio.baka.satoripixeldungeon.items.scrolls.exotic;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Sheep;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ScrollOfPolymorph extends ExoticScroll {

    {
        initials = 10;
    }

    @Override
    public void doRead() {

        new Flare(5, 32).color(0xFFFFFF, true).show(curUser.sprite, 2f);
        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
                if (!mob.properties().contains(Char.Property.BOSS)
                        && !mob.properties().contains(Char.Property.MINIBOSS)) {
                    Sheep sheep = new Sheep();
                    sheep.lifespan = 10;
                    sheep.pos = mob.pos;

                    //awards half exp for each sheep-ified mob
                    //50% chance to round up, 50% to round down
                    if (mob.EXP % 2 == 1) mob.EXP += Random.Int(2);
                    mob.EXP /= 2;

                    mob.destroy();
                    mob.sprite.killAndErase();
                    Dungeon.level.mobs.remove(mob);
                    TargetHealthIndicator.instance.target(null);
                    GameScene.add(sheep);
                    CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
                }
            }
        }
        setKnown();

        readAnimation();

    }

}
