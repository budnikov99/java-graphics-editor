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
package javashopfx.window;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javashopfx.JavaShop;

/**
 * FXML Controller class
 *
 * @author Anton Budnikov
 */
public class CreateWindowController implements Initializable {

    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private Label warningLabel;
    @FXML private Button createButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        widthField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                update();
            }
        });  
        
        heightField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                update();
            }
        });
    }    

    int width = 0;
    int height = 0;
    boolean correct = false;
    private void update(){
        boolean wcorrect = false;
        correct = false;
        try{
            wcorrect = false;
            width = Integer.parseInt(widthField.getText());
            wcorrect = true;
            height = Integer.parseInt(heightField.getText());
            warningLabel.setText("");
            createButton.setDisable(false);
        }catch(Exception ex){
            if(wcorrect){
                warningLabel.setText("Неверно задан параметр высоты.");
            }else{
                warningLabel.setText("Неверно задан параметр ширины.");
            }
            createButton.setDisable(true);
            return;
        }
        
        if(width <= 0 || height <= 0){
            warningLabel.setText("Ширина и высота должны быть больше нуля.");
            createButton.setDisable(true);
            return;
        }
        
        correct = true;
    }
    
    @FXML private void createButtonAction(ActionEvent event) {
        if(correct){
            if(JavaShop.windowController.saveBeforeExit()){
                JavaShop.imageController.resetImage(new WritableImage(width, height));
                JavaShop.windowController.creationStage.close();
            }
        }
    }
    
}
