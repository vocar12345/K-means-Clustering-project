package scene;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
//custom is same as default, but it can change color and there is a isCentroid boolean
public class CustomWaypoint extends DefaultWaypoint {
    private final Color color;
    private final boolean isCentroid;

    public CustomWaypoint(GeoPosition coordinates, Color color, boolean isCentroid) {
        super(coordinates);
        this.color = color;
        this.isCentroid = isCentroid;
    }

    public Color getColor() {
        return color;
    }

    public boolean isCentroid() {
        return isCentroid;
    }
}