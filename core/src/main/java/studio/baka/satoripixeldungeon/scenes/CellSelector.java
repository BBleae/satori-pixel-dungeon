package studio.baka.satoripixeldungeon.scenes;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ScrollArea;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public class CellSelector extends ScrollArea {

    public Listener listener = null;

    public boolean enabled;

    private final float dragThreshold;

    public CellSelector(DungeonTilemap map) {
        super(map);
        camera = map.camera();

        dragThreshold = PixelScene.defaultZoom * DungeonTilemap.SIZE / 2;

        mouseZoom = camera.zoom;
    }

    private float mouseZoom;

    @Override
    protected void onScroll(ScrollEvent event) {
        float diff = event.amount / 10f;

        //scale zoom difference so zooming is consistent
        diff /= ((camera.zoom + 1) / camera.zoom) - 1;
        diff = Math.min(1, diff);
        mouseZoom = GameMath.gate(PixelScene.minZoom, mouseZoom - diff, PixelScene.maxZoom);

        zoom((int) Math.floor(mouseZoom));
    }

    @Override
    protected void onClick(PointerEvent event) {
        if (dragging) {

            dragging = false;

        } else {

            PointF p = Camera.main.screenToCamera((int) event.current.x, (int) event.current.y);
            for (Char mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (mob.sprite != null && mob.sprite.overlapsPoint(p.x, p.y)) {
                    select(mob.pos);
                    return;
                }
            }

            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.sprite != null && heap.sprite.overlapsPoint(p.x, p.y)) {
                    select(heap.pos);
                    return;
                }
            }

            select(((DungeonTilemap) target).screenToTile(
                    (int) event.current.x,
                    (int) event.current.y,
                    true));
        }
    }

    private float zoom(float value) {

        value = GameMath.gate(PixelScene.minZoom, value, PixelScene.maxZoom);
        SPDSettings.zoom((int) (value - PixelScene.defaultZoom));
        camera.zoom(value);

        //Resets character sprite positions with the new camera zoom
        //This is important as characters are centered on a 16x16 tile, but may have any sprite size
        //This can lead to none-whole coordinate, which need to be aligned with the zoom
        for (Char c : Actor.chars()) {
            if (c.sprite != null && !c.sprite.isMoving) {
                c.sprite.point(c.sprite.worldToCamera(c.pos));
            }
        }

        return value;
    }

    public void select(int cell) {
        if (enabled && listener != null && cell != -1) {

            listener.onSelect(cell);
            GameScene.ready();

        } else {

            GameScene.cancel();

        }
    }

    private boolean pinching = false;
    private PointerEvent another;
    private float startZoom;
    private float startSpan;

    @Override
    protected void onPointerDown(PointerEvent event) {

        if (event != curEvent && another == null) {

            if (!curEvent.down) {
                curEvent = event;
                onPointerDown(event);
                return;
            }

            pinching = true;

            another = event;
            startSpan = PointF.distance(curEvent.current, another.current);
            startZoom = camera.zoom;

            dragging = false;
        } else if (event != curEvent) {
            reset();
        }
    }

    @Override
    protected void onPointerUp(PointerEvent event) {
        if (pinching && (event == curEvent || event == another)) {

            pinching = false;

            zoom(Math.round(camera.zoom));

            dragging = true;
            if (event == curEvent) {
                curEvent = another;
            }
            another = null;
            lastPos.set(curEvent.current);
        }
    }

    private boolean dragging = false;
    private final PointF lastPos = new PointF();

    @Override
    protected void onDrag(PointerEvent event) {

        if (pinching) {

            float curSpan = PointF.distance(curEvent.current, another.current);
            float zoom = (startZoom * curSpan / startSpan);
            camera.zoom(GameMath.gate(
                    PixelScene.minZoom,
                    zoom - (zoom % 0.1f),
                    PixelScene.maxZoom));

        } else {

            if (!dragging && PointF.distance(event.current, event.start) > dragThreshold) {

                dragging = true;
                lastPos.set(event.current);

            } else if (dragging) {
                camera.shift(PointF.diff(lastPos, event.current).invScale(camera.zoom));
                lastPos.set(event.current);
            }
        }

    }

    public void cancel() {

        if (listener != null) {
            listener.onSelect(null);
        }

        GameScene.ready();
    }

    @Override
    public void reset() {
        super.reset();
        another = null;
        if (pinching) {
            pinching = false;

            zoom(Math.round(camera.zoom));
        }
    }

    public void enable(boolean value) {
        if (enabled != value) {
            enabled = value;
        }
    }

    public interface Listener {
        void onSelect(Integer cell);

        String prompt();
    }
}
