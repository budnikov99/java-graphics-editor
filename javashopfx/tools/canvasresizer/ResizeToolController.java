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
package javashopfx.tools.canvasresizer;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javashopfx.JavaShop;

/**
 * FXML Controller class
 *
 * @author Anton Budnikov
 */
public class ResizeToolController implements Initializable {
    public ResizeTool tool;
    int width = 0, height = 0;
    
    int oiwidth, oiheight;
    
    @FXML private CheckBox proportionCheckBox;
    @FXML private RadioButton pixelRadioButton;
    @FXML private ToggleGroup sl;
    @FXML private RadioButton percentRadioButton;
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private Label widthLabel;
    @FXML private Label heightLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        widthField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    updateWidth();
                }
            }
        });
        
        heightField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    updateHeight();
                }
            }
        });
    }    
        
    public void initValues(){
        oiwidth = (int)JavaShop.imageController.cachedImage.getWidth();
        oiheight = (int)JavaShop.imageController.cachedImage.getHeight();
        
        width = oiwidth;
        height = oiheight;
        
        updateValues();
    }
    
    protected void updateValues(){
        oiwidth = (int)JavaShop.imageController.cachedImage.getWidth();
        oiheight = (int)JavaShop.imageController.cachedImage.getHeight();
        
        if(sl.getSelectedToggle() == percentRadioButton){
            widthField.setText(""+widthToPercent(width));
            heightField.setText(""+heightToPercent(height));
        }else if(sl.getSelectedToggle() == pixelRadioButton){
            widthField.setText(""+width);
            heightField.setText(""+height);
        }
        
        widthLabel.setText(""+width);
        heightLabel.setText(""+height);
        
        applyValues();
    }
    
    protected void applyValues(){
        tool.width = width;
        tool.height = height;
    }

    protected void fieldOk(TextField tf){
        tf.setBackground(new Background(new BackgroundFill(Color.WHITE,CornerRadii.EMPTY,Insets.EMPTY)));
    }
    
    protected void fieldError(TextField tf){
        tf.setBackground(new Background(new BackgroundFill(Color.PINK,CornerRadii.EMPTY,Insets.EMPTY)));
    }
    
    protected int widthToPercent(int w){
        return (int)((double)w/(double)oiwidth*100.0);
    }
    
    protected int heightToPercent(int h){
        return (int)((double)h/(double)oiheight*100.0);
    }
    
    protected int percentToWidth(int p){
        return (int)((double)p/100.0*(double)oiwidth);
    }
    
    protected int percentToHeight(int p){
        return (int)((double)p/100.0*(double)oiheight);
    }
    
    @FXML
    private void widthTyped(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER){
            return;
        }
        
        updateWidth();
    }
    
    protected void updateWidth(){
        try{
            int w = Integer.parseInt(widthField.getText());
            
            if(w < 1){
                throw new NumberFormatException("width should be > 0");
            }
                        
            if(proportionCheckBox.isSelected()){
                fieldOk(heightField);
                double ratio = height/width;
                if(sl.getSelectedToggle() == percentRadioButton){
                    height = percentToHeight((int)((double)w*ratio));
                }else{
                    height = (int)((double)w*ratio);
                }
            }
            
            if(sl.getSelectedToggle() == percentRadioButton){
                width = percentToWidth(w);
            }else{
                width = w;
            }
            
            fieldOk(widthField);
        }catch(NumberFormatException ex){
            fieldError(widthField);
            System.out.println(""+ex);
        }
        
        updateValues();
    }

    @FXML
    private void heightTyped(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER){
            return;
        }
        
        updateHeight();
    }
    
    protected void updateHeight(){
        try{
            int h = Integer.parseInt(heightField.getText());
            
            if(h < 1){
                throw new NumberFormatException("height should be > 0");
            }
                        
            if(proportionCheckBox.isSelected()){
                fieldOk(widthField);
                double ratio = width/height;
                if(sl.getSelectedToggle() == percentRadioButton){
                    width = percentToWidth((int)((double)h*ratio));
                }else{
                    width = (int)((double)h*ratio);
                }
            }
            
            if(sl.getSelectedToggle() == percentRadioButton){
                height = percentToHeight(h);
            }else{
                height = h;
            }
            
            fieldOk(heightField);
        }catch(NumberFormatException ex){
            fieldError(heightField);
        }
        
        updateValues();
    }

    @FXML
    private void proportionsClicked(MouseEvent event) {
        updateValues();
    }

    @FXML
    private void modeRBClicked(MouseEvent event) {
        updateValues();
    }
    
}
