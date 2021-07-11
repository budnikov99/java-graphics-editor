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

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

public class QuickFill
{
    private PixelReader _reader;
    private PixelWriter _writer;
    private int _oldColor;
    private int _newColor;
    private int _bmpWidth;
    private int _bmpHeight;

    public QuickFill(PixelReader reader, PixelWriter writer, int width, int height, int oldColor, int newColor)
    {
        _reader = reader;
        _writer = writer;
        _oldColor = oldColor;
        _newColor = newColor;
        _bmpWidth = width;
        _bmpHeight = height;
    }

    public void fill(int x, int y)
    {
        Queue<Point> queue = new LinkedList<Point>();
        int[] scanLine = new int[_bmpWidth];
        
        if(_oldColor == _newColor){
            return;
        }

        if (_reader.getArgb(x, y) == _oldColor)
        {
            queue.add(new Point(x, y));

            while (!queue.isEmpty())
            {
                //System.out.println("Q="+queue.size());
                Point n = queue.poll();
                //System.out.println("N="+n.x+" "+n.y);
                if (_reader.getArgb(n.x, n.y) == _oldColor)
                {
                    int wx = n.x;
                    int ex = n.x + 1;

                    _reader.getPixels(0, n.y, _bmpWidth, 1, WritablePixelFormat.getIntArgbInstance(), scanLine, 0, _bmpWidth);
                    while (wx >= 0 && scanLine[wx] == _oldColor)
                    {
                        scanLine[wx] = _newColor;
                        wx--;
                    }
                    
                    while (ex <= _bmpWidth - 1 && scanLine[ex] == _oldColor)
                    {
                        scanLine[ex] = _newColor;
                        ex++;
                    }

                    int length = ex - wx - 1;
                    if (length > 0)
                    {
                        _writer.setPixels(wx + 1, n.y, length, 1, WritablePixelFormat.getIntArgbInstance(), scanLine, wx + 1, _bmpWidth);
                    }
                    
                    boolean n1 = false;
                    boolean n2 = false;
                    for (int ix = wx + 1; ix < ex; ix++)
                    {
                        if (n.y - 1 >= 0 && _reader.getArgb(ix, n.y - 1) == _oldColor)
                        {
                            if(!n1){
                                queue.add(new Point(ix, n.y - 1));
                                //System.out.println("/\\ "+(n.y-1)+" - "+ix);
                                n1 = true;
                            }
                        }else{
                            n1 = false;
                        }
                        
                        if (n.y + 1 < _bmpHeight && _reader.getArgb(ix, n.y + 1) == _oldColor)
                        {
                            if(!n2){
                                queue.add(new Point(ix, n.y + 1));
                                //System.out.println("\\/ "+(n.y+1)+" - "+ix);
                                n2 = true;
                            }
                        }else{
                            n2 = false;
                        }
                    }
                }
                //System.out.println("QE="+queue.size()+" "+queue.isEmpty());
            }
        }
    }
}
