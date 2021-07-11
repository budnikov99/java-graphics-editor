/*
 * Copyright (C) 2019 Anton Budnikov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package javashopfx;

/**
 *
 * @author Anton Budnikov
 */
public class Config {
    public static double minZoom = 0.01;
    public static double maxZoom = 10.0;
    public static double availableZooms[] = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1.0, 
        1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 5.0, 6.0, 8.0, 12.0};
    public static double imageCanvasMargin = 25.0;
    public static double imageCanvasMargin2 = imageCanvasMargin*2.0;
    
    public static int backgroundSquaresSize = 7;
    public static int historyDepth = 30;
    public static int brushCircleDistanceDivisor = 8;
    
    public static String programName = "JavaShop 1.0";
}
