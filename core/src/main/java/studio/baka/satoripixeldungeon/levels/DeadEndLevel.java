package studio.baka.satoripixeldungeon.levels;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;

public class DeadEndLevel extends Level {

    private static final int SIZE = 5;

    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_CAVES;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_HALLS;
    }

    @Override
    protected boolean build() {

        setSize(7, 7);

        for (int i = 2; i < SIZE; i++) {
            for (int j = 2; j < SIZE; j++) {
                map[i * width() + j] = Terrain.EMPTY;
            }
        }

        for (int i = 1; i <= SIZE; i++) {
            map[width() + i] =
                    map[width() * SIZE + i] =
                            map[width() * i + 1] =
                                    map[width() * i + SIZE] =
                                            Terrain.WATER;
        }

        entrance = SIZE * width() + SIZE / 2 + 1;
        map[entrance] = Terrain.ENTRANCE;

        exit = 0;

        return true;
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    public Actor respawner() {
        return null;
    }

    @Override
    protected void createItems() {
    }

    @Override
    public int randomRespawnCell() {
        return entrance - width();
    }

}
