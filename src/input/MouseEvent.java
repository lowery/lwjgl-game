package input;

/**
 * Created by lowery on 10/23/2016.
 */
public class MouseEvent {
    public int action;
    public double xOffset;
    public double yOffest;

    public MouseEvent(int action, double xOffset, double yOffest) {
        this.action = action;
        this.xOffset = xOffset;
        this.yOffest = yOffest;
    }

    public static final int EVT_MOUSE_SCROLL = 0;
}
