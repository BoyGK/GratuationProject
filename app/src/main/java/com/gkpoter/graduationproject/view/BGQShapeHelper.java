package com.gkpoter.graduationproject.view;

import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineShape;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by "nullpointexception0" on 2019/1/16.
 */
public class BGQShapeHelper {

    private String filePath;

    private double minX = 0, minY = 0, maxY = 0, maxX = 0;

    private static Vector<PointData[]> mPoints;

    public BGQShapeHelper(String filePath) throws IOException, InvalidShapeFileException {
        this.filePath = filePath;
        mPoints = new Vector<>();
        openShpFile(this.filePath);
    }

    private void openShpFile(String path) throws IOException, InvalidShapeFileException {
        if (!(path != null && path.length() != 0)) {
            return;
        }
        FileInputStream is = new FileInputStream(path);
        ValidationPreferences prefs = new ValidationPreferences();
        prefs.setMaxNumberOfPointsPerShape(16650);
        ShapeFileReader r = new ShapeFileReader(is, prefs);

        minX = r.getHeader().getBoxMinX();
        minY = r.getHeader().getBoxMinY();
        maxY = r.getHeader().getBoxMaxY();
        maxX = r.getHeader().getBoxMaxX();

        AbstractShape s;

        while ((s = r.next()) != null) {
            switch (s.getShapeType()) {
                case POLYLINE:
                    PolylineShape aPolyline = (PolylineShape) s;
                    for (int i = 0; i < aPolyline.getNumberOfParts(); i++) {
                        PointData[] points = aPolyline.getPointsOfPart(i);
                        mPoints.add(points);
                    }
                    break;
                default:
            }
        }
        is.close();
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxX() {
        return maxX;
    }
}
