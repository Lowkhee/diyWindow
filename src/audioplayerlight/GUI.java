package audioplayerlight;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import windowinterface.Main;

public class GUI extends BorderPane // Player class extend BorderPane
// in order to divide the media
// player into regions
{
    MediaView mediaView;
    StackPane stackPane;
    BorderPane bottomPane;
    Label labelAudioFile = new Label();
    Image imageBG;
    ImageView imageViewBG;
    AudioControlPane audioPane;
    
    public GUI() 
 	{ 
 		super();
 		this.getStylesheets().add(AudioPlayer.styleSheet);
 		
 		AudioPlayer.windowAppPane.prefWidthProperty().addListener(new ChangeListener<Number>() 
        {
            @Override
            public void changed(ObservableValue<? extends Number> num1, Number oldValue, Number newValue) 
            {
            	setPrefWidth((double)newValue);
            	imageViewBG.setFitWidth(AudioPlayer.windowAppPane.getPrefWidth() * .7);
            }
        });
 		AudioPlayer.windowAppPane.prefHeightProperty().addListener(new ChangeListener<Number>() 
        {
            @Override
            public void changed(ObservableValue<? extends Number> num1, Number oldValue, Number newValue) 
            {
            	setPrefHeight((double)newValue);
            	imageViewBG.setFitHeight(AudioPlayer.windowAppPane.getPrefHeight() * .7);
            }
        });
 		
 		stackPane = new StackPane();
 		audioPane = new AudioControlPane();
        mediaView = new MediaView();
        setImage();
        setPrefWidth(AudioPlayer.windowAppPane.getPrefWidth());
 		imageViewBG.setFitWidth(AudioPlayer.windowAppPane.getPrefWidth() * .7);
 		setPrefHeight(AudioPlayer.windowAppPane.getPrefHeight());
 		
        Group imageGroup = new Group(stackPane);
        stackPane.getChildren().addAll(mediaView, imageViewBG, labelAudioFile);
        GUI.setAlignment(stackPane, Pos.CENTER);
        StackPane.setAlignment(imageViewBG, Pos.CENTER);
        StackPane.setAlignment(labelAudioFile, Pos.BOTTOM_LEFT);
        StackPane.setMargin(labelAudioFile, new Insets(imageViewBG.getFitHeight()*.80,0,8,8));
        
        this.setStyle("-fx-background-color:#000000");
        setCenter(stackPane);
        GUI.setAlignment(audioPane, Pos.BOTTOM_CENTER);
		setBottom(audioPane);;
	    
        labelAudioFile.setText("waiting...");
        labelAudioFile.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), new Insets(0))));
        labelAudioFile.setTextAlignment(TextAlignment.LEFT);
        labelAudioFile.setFont(Font.font("MV Boli", FontWeight.NORMAL, FontPosture.REGULAR, getHeight()*.03));
        labelAudioFile.setTextFill(Color.YELLOWGREEN);
        widthProperty().addListener((widthProperty, widthOld, widthNew) -> 
        {
        	labelAudioFile.setFont(Font.font("Segoe UI", FontWeight.NORMAL, FontPosture.REGULAR, getHeight()*.03));
        });
        heightProperty().addListener((heightProperty, heightOld, heightNew) -> 
        {
        	labelAudioFile.setFont(Font.font("Segoe UI", FontWeight.NORMAL, FontPosture.REGULAR, getHeight()*.03));
        });
 	}
    
    /**
     * Load image and place in imageview
     * 
     */
    private void setImage()
    {
        imageBG = Main.loadImage("bgImage.jpeg");
        imageViewBG = new ImageView(imageBG);
        imageViewBG.setPreserveRatio(true);
        imageViewBG.setSmooth(true);
        imageViewBG.setCache(true);
        imageViewBG.setManaged(true);
        imageViewBG.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.DARKSEAGREEN, 10, 0.2, 3, 3));
    }
}
