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

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javashopfx.JavaShop;
import javashopfx.drawcommands.DrawCommand;


/**
 *
 * @author Anton Budnikov
 */
public abstract class Tool {
    public String imageURL = "";
    public String toolName = "";
    protected Button toolButton;
    protected boolean initialized = false;
    
    
    protected String settingsWindowPath = "";
    protected Pane settingsWindow;
    protected Initializable windowController;
            
    protected void initializeWindow(){
        if(settingsWindowPath != ""){
            FXMLLoader loader = new FXMLLoader(getClass().getResource(settingsWindowPath));
            try {
                settingsWindow = loader.load();
            } catch (IOException ex) {
                JavaShop.errorMessage("An error occurred while loading "+toolName+" tool.", "Error");
                return;
            }
            windowController = (Initializable)loader.getController();
        }
        
        toolButton = new Button();
        toolButton.setPrefHeight(24);
        toolButton.setPrefWidth(24);
        toolButton.setTooltip(new Tooltip(toolName));
        toolButton.setStyle("-fx-background-image: url("+imageURL+"); " +
                            "-fx-background-repeat: no-repeat;");
        Tool ref = this;
        toolButton.onActionProperty().set(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                JavaShop.toolController.selectTool(ref);
            }
        });

        JavaShop.windowController.addToolButton(toolButton);
        
        specialInit();
        setWindowVars();
        initialized = true;
    } 
    
    protected abstract void setWindowVars();
    
    protected void specialInit(){}
    
    public Pane getSettingsWindow(){
        return settingsWindowPath != "" ? settingsWindow : null;
    }
    
    public Button getToolButton(){
        return initialized ? toolButton : null;
    }
       
    public abstract DrawCommand initialize(int x, int y);
    
    public abstract void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom);
    
    public abstract void drawFirst(int x, int y);
    public abstract void draw(int x, int y, int lastx, int lasty);
    public abstract void drawLast(int x, int y);
    
    public void onSelected(){}
    public void onDeselected(){}
}
