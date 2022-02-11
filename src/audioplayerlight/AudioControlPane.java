package audioplayerlight;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import windowinterface.Main;

public class AudioControlPane extends HBox
{ 

    Slider timeSlider = new Slider(); 
    Slider volumeSlider = new Slider(0, 100, 20); 
    Button playButton = new Button(">"); 
    Label volumeLabel = new Label("Volume: ");
    MediaPlayer mediaPlayer;
    
    public AudioControlPane()
    {
    	this.setBackground(new Background(new BackgroundFill(Color.DARKSEAGREEN, new CornerRadii(50, 100, 0, 0, false), new Insets(0))));
    	
    	setAlignment(Pos.CENTER); 
        setPadding(new Insets(5, 5, 5, 5));
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        HBox.setMargin(playButton, new Insets(0, 5, 0, 0));
        HBox.setMargin(timeSlider, new Insets(0, 5, 0, 0));
        HBox.setMargin(volumeLabel, new Insets(0, 5, 0, 0));
        HBox.setMargin(volumeSlider, new Insets(0, 0, 0, 0));
        

        volumeSlider.setPrefWidth(70);
        volumeSlider.setMinWidth(30);
        
        Circle imgCircle = new Circle(0,0,1);
        playButton.setShape(imgCircle);
        
        Image image = Main.loadImage("playbutton.png");
        ImageView img = new ImageView(image);
        
        img.setStyle("-fx-background-color:transparent");
        img.setPreserveRatio(true);
        //img
        playButton.prefHeightProperty().bind(this.heightProperty().subtract(10));
        playButton.prefWidthProperty().bind(playButton.prefHeightProperty());
        img.fitWidthProperty().bind(playButton.widthProperty());
        img.fitHeightProperty().bind(playButton.heightProperty());
        getChildren().addAll(playButton, timeSlider, volumeLabel, volumeSlider);
        
        playButton.setDisable(true);
        volumeSlider.setDisable(true);
        timeSlider.setDisable(true);
    }
    
    public void enableControls()
    {
    	playButton.setDisable(false);
		volumeSlider.setDisable(false);
        timeSlider.setDisable(false);
    }
    
    public void disableControls()
    {
    	playButton.setDisable(true);
		volumeSlider.setDisable(true);
        timeSlider.setDisable(true);
    }

    public boolean loadMedia(MediaPlayer player)
    {
    	if(player == null)
    		return false;
    	mediaPlayer = player;
    	
        playButton.setOnAction(new EventHandler<ActionEvent>() 
        {	
            public void handle(ActionEvent e)
            {
            		Status status = mediaPlayer.getStatus(); // To get the status of Player
            		if (status == status.PLAYING) 
            		{
            			if (mediaPlayer.getCurrentTime().greaterThanOrEqualTo(mediaPlayer.getTotalDuration())) 
            			{
            				mediaPlayer.seek(mediaPlayer.getStartTime()); // Restart the video
            				mediaPlayer.play();
            			}
            			else 
            			{
            				mediaPlayer.pause();
            				playButton.setText(">");
            			}
            		} 
            		if (status == Status.READY || status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) 
            		{
            			mediaPlayer.play(); // Start the video
            			playButton.setText("||");
            		}
            }
        });

        
        
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() 
        {
        	public void invalidated(Observable ov)
        	{
        		updatesValues();
        	}
        });

        
        timeSlider.valueProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov)
            {
                if (timeSlider.isPressed()) 
                { // It would set the time
                    // as specified by user by pressing
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
                }
            }
        });

        
        volumeSlider.valueProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov)
            {
                if (volumeSlider.isPressed()) 
                {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100); // It would set the volume
                    // as specified by user by pressing
                }
            }
        });
        return true;
    }

    
    protected void updatesValues()
    {
        Platform.runLater(new Runnable() 
        {
            public void run()
            {
                // Updating to the new time value
                // This will move the slider while running your video
                timeSlider.setValue((mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis()) * 100);
            }
        });
    }
}
