package jp.atr.dni.brainliner.desktop.timeline;

import java.awt.geom.Point2D;

/**
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <i>ATR - [株式会社・國際電氣通信基礎技術研究所]</i>
 *
 * @version 2011/02/02
 */
public class Grid {
/** the default spacing of the grid */
	public static double GRID_WIDTH = 100;

	/** the distance between different grid lines */
	private double spacing;

	/**
	 * Constructs a grid with the specified spacing.
	 *
	 * @param spacing - the spacing between grid line.
	 */
	public Grid(double spacing) {
		this.spacing = spacing;
	}

	/**
	 * Gets the distance between grid lines.
	 */
	public double getSpacing() {
		return spacing;
	}

	/**
	 * Calculates the nearest grid point for the given coordinate.
	 *
	 * @param x - the x value of a coordinate
	 * @param y - the y value of a coordinate
	 * @return - the location of the nearest grid point
	 */
	public Point2D getGridCoordinate(double x, double y) {
		x += GRID_WIDTH/2;
		y += GRID_WIDTH/2;

		double rx = x - GRID_WIDTH*((int)(x/GRID_WIDTH));
		double ry = y - GRID_WIDTH*((int)(y/GRID_WIDTH));

		if (rx < 0) {
			rx = GRID_WIDTH + rx;
		}
		if (ry < 0) {
			ry = GRID_WIDTH + ry;
		}

		return new Point2D.Double(x - rx, y - ry);
	}
}
