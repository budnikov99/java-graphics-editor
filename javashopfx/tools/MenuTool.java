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
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javashopfx.JavaShop;

/**
 *
 * @author Anton Budnikov
 */
public abstract class MenuTool extends Tool {
    MenuItem menuItem;
    protected void initializeWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(settingsWindowPath));
        try {
            settingsWindow = loader.load();
        } catch (IOException ex) {
            JavaShop.errorMessage("An error occurred while loading "+toolName+" tool.", "Error");
            return;
        }
        windowController = (Initializable)loader.getController();
        
        menuItem = new MenuItem();
        menuItem.setText(toolName);
        
        
        Tool ref = this;
        menuItem.onActionProperty().set(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                JavaShop.toolController.selectTool(ref);
            }
        });

        JavaShop.windowController.addMenuTool(menuItem);
        
        specialInit();
        setWindowVars();
        initialized = true;
    }
    
    public Button getToolButton(){
        return null;
    }
    
    public MenuItem getMenuItem(){
        return initialized ? menuItem : null;
    }
}
