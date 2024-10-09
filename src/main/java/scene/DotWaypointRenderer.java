package scene;


import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

public class DotWaypointRenderer implements WaypointRenderer<Waypoint> {
    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
        CustomWaypoint colorWaypoint = (CustomWaypoint) wp;
        Point2D point = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
        //take x and y
        int x = (int) point.getX();
        int y = (int) point.getY();
        //if center make it bigger and black, if not take given color and make it smaller
        if (colorWaypoint.isCentroid()) {
            g.setColor(Color.BLACK);
            g.fillOval(x - 5, y - 5, 10, 10); // Three times smaller
        } else {
            g.setColor(colorWaypoint.getColor());
            g.fillOval(x - 5 / 2, y - 5 / 2, 10 / 2, 10 / 2); // Three times smaller
        }
    }
}

