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
package javashopfx.tools;

import com.sun.javafx.geom.Rectangle;
import java.util.ArrayList;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.Controller;
import javashopfx.JavaShop;
import javashopfx.tools.brush.BrushTool;
import javashopfx.tools.brush.LineTool;
import javashopfx.tools.canvasresizer.ResizeTool;
import javashopfx.tools.eraser.EraserTool;
import javashopfx.tools.fill.FillTool;
import javashopfx.tools.selection.SelectionTool;
import javashopfx.tools.shapes.EllipseTool;
import javashopfx.tools.shapes.RectangleTool;

/**
 *
 * @author Anton Budnikov
 */
public class ToolController implements Controller {
    protected ArrayList<Tool> tools = new ArrayList<>();
    protected Tool activeTool;
    protected int lastX = -1;
    protected int lastY = -1;
    public Rectangle selection = null;
    
    public ToolController(){
        initTools();
    }
    
    public void initTools(){
        
        addTool(new SelectionTool());
        
        addTool(new BrushTool());
        addTool(new EraserTool());
        
        addTool(new LineTool());
        addTool(new RectangleTool());
        addTool(new EllipseTool());
        
        addTool(new FillTool());
        
        addTool(new PickerTool());
        
        
        addTool(new ResizeTool());
    }
    
    public void addTool(Tool t){
        t.initializeWindow();
        tools.add(t);
    }
    
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom){
        if(activeTool != null){
            activeTool.drawOverlay(reader, writer, x, y, zoom);
        }
    }
    
    public void selectTool(Tool tool){
        if(activeTool != null){
            activeTool.onDeselected();
        }
        
        activeTool = tool;
        JavaShop.windowController.setSettingsPane(tool.getSettingsWindow());
        JavaShop.windowController.setToolName(tool.toolName);
        
        activeTool.onSelected();
        
    }
    
    public void processMousePress(int x, int y){
        if(activeTool == null) return;
        JavaShop.imageController.setCurrentCommand(activeTool.initialize(x, y));
        
        activeTool.drawFirst(x, y);
        
        lastX = x;
        lastY = y;
        
    }
    
    public void processMouseRelease(int x, int y){
        if(activeTool == null) return;
        
        activeTool.drawLast(x, y);
        
        lastX = -1;
        lastY = -1;
    }
    
    public void apply(){
        JavaShop.imageController.submitCommand();
    }
    
    public void processMouseDrag(int x, int y){
        if(activeTool == null) return;
        
        activeTool.draw(x, y, lastX, lastY);
        
        lastX = x;
        lastY = y;
    }
    
}
