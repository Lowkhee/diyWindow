package windowinterface;


import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;


public class WindowController implements Initializable 
{
	/**
     * windowAppPane 
     * @description Pane for user application, not custom window.
     * @type May be changed to any other pane.
     */
    public Pane windowAppPane = new Pane();
    
    @FXML
    protected StackPane windowRootStackPane;
    @FXML
    private BorderPane windowBorderPane;
    @FXML
    private AnchorPane headerAnchorPane;
    @FXML
    private VBox headerVBox;
    @FXML
    private Button buttonExitApp;
    @FXML
    private Button buttonMinimize;
    @FXML
    private ImageView buttonMenuImage;
    @FXML
    private Button buttonMenu;
    @FXML
    private Button buttonMaximize;
    @FXML
    private Button buttonResize;
    @FXML
    private Button buttonFullScreen;
    @FXML
    private Label labelTitle;
    @FXML
    private ContextMenu contextMenu;
    @FXML 
    private Line lineBottomResize;
    @FXML 
    private Line lineRightResize;
    @FXML 
    private Line lineLeftResize;
    @FXML 
    private Line lineTopResize;
    
    MenuItem maximizeMenuItem;
    MenuItem fullScreenMenuItem;
    
    protected SimpleBooleanProperty maximizeProperty;
    protected SimpleBooleanProperty minimizeProperty;
    protected SimpleBooleanProperty exitProperty;
    protected SimpleBooleanProperty fullScreenProperty;
   
    //EDIT: opening stage size, resize adjust 
	public static final int DEFAULT_WIDTH = 720;
	public static final int DEFAULT_HEIGHT = 550;
	
	/*EDIT: whether or not the window/app will fade in and out */
	protected static boolean STAGE_FADE_IN_OUT = false;
	
	private double mousePressedX, mousePressedY;
    private static double mousePressInitX = -1;
    private static double mousePressInitY = -1;
    private static double mousePressNewX;
    private static double mousePressNewY;
    public static Stage stage;
    private long currentClickTime = 0;
    private long lastClickTime = 0;
	private boolean isDoubleClick = false;
	private static EventHandler<KeyEvent> eventHandlerESConFullScreen;
    protected Font defaultFont;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
    	windowAppPane = new Pane();
    	stage.setWidth(DEFAULT_WIDTH);
    	stage.setHeight(DEFAULT_HEIGHT);
    	
        /*EDIT: font used for all other non EDIT inidcated fonts */    
    	defaultFont = Font.font("MV Boli", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 14);
    	
    	/*EDIT: Title/Name font */
    	Font labelTitelFont = Font.font("MV Boli", FontWeight.BOLD, FontPosture.REGULAR, 24);
        
    	labelTitle.setFont(labelTitelFont);
        labelTitle.getStyleClass().add("labelTitle");
        FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(labelTitelFont);
        
        /*EDIT: application Title/Name */
        String labelApplicationTitle = "diyWindow";
        
        labelTitle.setPrefWidth(fontMetrics.computeStringWidth(labelApplicationTitle));
        labelTitle.setText(labelApplicationTitle);
        
        /*EDIT: determines height of the Header at the top of the application; first and second number for above and below controls*/
    	int headerBarHeight = 20 + Math.round(fontMetrics.getLineHeight()) + 10;
        
    	/*EDIT: change/edit/delete context menu button background image
    	 * Also sets task bar image icon.
    	 */
        Image imageApp = Main.loadImage("imgTitelIcon.png");
        buttonMenuImage.setImage(imageApp);
    	stage.getIcons().add(imageApp);
    	
    	buttonMenu.getStyleClass().add("buttonMenu");
        contextMenu.getStyleClass().add("contextMenu");
        Button[] buttonWindowControls = {buttonExitApp, buttonMaximize, buttonMinimize, buttonResize, buttonFullScreen};
        for(Button button : buttonWindowControls)
        	button.getStyleClass().add("buttonWindowControl");
        
        windowBorderPane.setBackground(Background.EMPTY);
        windowRootStackPane.setBackground(Background.EMPTY);
        //windowBorderPane.setStyle("-fx-background-color: transparent;");
        //windowRootStackPane.setStyle("-fx-background-color: transparent;");
        
        if(WindowController.STAGE_FADE_IN_OUT)
        	setFadeInTransition();
        
        /*EDIT: empty/transparent application/window pane */
        //windowAppPane.setStyle("-fx-background-color: transparent;");
        //windowAppPane.setBackground(Background.EMPTY);
    	windowAppPane.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, null, null)));
    	
    	/*EDIT: can add border */
    	/*windowAppPane.setBorder(new Border(
				 new BorderStroke(Color.HOTPINK, Color.HOTPINK, Color.HOTPINK, Color.HOTPINK, 
						 BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, 
				 null, new BorderWidths(15, 15, 15, 15), null)
				));*/
    	
     	windowBorderPane.setCenter(windowAppPane);
     	BorderPane.setAlignment(windowAppPane, Pos.CENTER);
     	windowAppPane.toBack();

     	initializeProperties();
    	initializeControls();
    	
    	//Stop[] stops1 = new Stop[] { new Stop(0, Color.web("#3c79b2")), new Stop(1, Color.web("#2d5d8b")) };
        //RadialGradient rg1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, stops1);
        //Stop[] stops2 = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
        //LinearGradient lg1 = new LinearGradient(125, 0, 225, 0, false, CycleMethod.NO_CYCLE, stops2);
        //Paint[] borderPaint = {Color.FIREBRICK, Color.FIREBRICK, Color.FIREBRICK, Color.FIREBRICK};
    	//Paint[] borderPaint = {rg1, Color.rgb(255, 255, 255, 0.01), lg1, Color.rgb(255, 255, 255, 0.01)};
    	lineTopResize.getStyleClass().add("lineTopResize");
    	lineLeftResize.getStyleClass().add("lineLeftResize");
    	lineBottomResize.getStyleClass().add("lineBottomResize");
    	lineRightResize.getStyleClass().add("lineRightResize");
    	
    	double[] borderWidth = {lineTopResize.getStrokeWidth(), lineLeftResize.getStrokeWidth(), lineBottomResize.getStrokeWidth(), lineRightResize.getStrokeWidth()}; 
    	initializeResizeBorder(borderWidth);
    	
    	setResizeEvents(lineTopResize, borderWidth[0]);
    	setResizeEvents(lineLeftResize, borderWidth[1]);
    	setResizeEvents(lineBottomResize, borderWidth[2]);
    	setResizeEvents(lineRightResize, borderWidth[3]);
    	
    	windowAppPane.resizeRelocate(borderWidth[1], headerBarHeight + borderWidth[0], stage.getWidth() - borderWidth[1] - borderWidth[3], stage.getHeight() - headerBarHeight - borderWidth[0] - borderWidth[2]);
    	setWindowAppPaneBounds(windowAppPane.getLayoutBounds());
		initializeHeader(borderWidth, headerBarHeight);
        stageResizeListeners(borderWidth, headerBarHeight);
    	
        //initialized here to allow remove event when not fullscreen
        //apparently keyevent is called after listeners since stage listeners are not called after esc key
        eventHandlerESConFullScreen = new EventHandler<KeyEvent>() 
    	{
    		@Override
            public void handle(KeyEvent ke) 
            {
    			if (KeyCode.ESCAPE == ke.getCode() && KeyEvent.KEY_PRESSED == ke.getEventType()) 
    			{
    				switchFullScreen();
    				windowAppPane.resizeRelocate(borderWidth[1], headerBarHeight + borderWidth[0], stage.getWidth() - borderWidth[1] - borderWidth[3], stage.getHeight() - headerBarHeight - borderWidth[0]);
            		setWindowAppPaneBounds(windowAppPane.getLayoutBounds());

            		headerAnchorPane.resizeRelocate(borderWidth[1], borderWidth[0], stage.getWidth() - borderWidth[1] - borderWidth[3], headerBarHeight);
    			}
            }
    	};
    	
    	//set the stage minimum size
    	stage.setMinWidth(borderWidth[1] + 5 + buttonMenu.getPrefWidth() + 5 + labelTitle.getPrefWidth() + 5 + borderWidth[3]);
    	stage.setMinHeight(borderWidth[0] + headerAnchorPane.getHeight() + borderWidth[2]);
    }
    
    /**
     * The bounding box an inclusive application should use
     * @return Bounds The rectangular bounds indeterminate of the window interface. 
     */
    public Bounds getWindowAppPaneBounds()
    {
    	return windowAppPane.getLayoutBounds();
    }
    
    /**
     * 
     */
    public void setWindowAppPaneBounds(Bounds bounds)
    {
    	windowAppPane.setPrefWidth(bounds.getWidth());
    	windowAppPane.setMaxWidth(bounds.getWidth());
    	windowAppPane.setMinWidth(bounds.getWidth());
    	windowAppPane.setPrefHeight(bounds.getHeight());
    	windowAppPane.setMaxHeight(bounds.getHeight());
    	windowAppPane.setMinHeight(bounds.getHeight());
    	//windowAppPane.prefHeightProperty().addListener(listener);
    }
    
    /**
     * 
     */
    private void initializeHeader(double[] borderWidth, int headerHeight)
    {
    	headerVBox.setManaged(false);
    	headerAnchorPane.resizeRelocate(borderWidth[1], borderWidth[0], DEFAULT_WIDTH - borderWidth[1] - borderWidth[3], headerHeight);
        
    	headerAnchorPane.setManaged(false);
    	headerAnchorPane.getStyleClass().add("headerAnchorPane");
    	
        buttonExitApp.translateYProperty().bind(lineTopResize.strokeWidthProperty().add(headerHeight / 8));
        buttonMinimize.translateYProperty().bind(lineTopResize.strokeWidthProperty().add(headerHeight / 8));
        buttonMaximize.translateYProperty().bind(lineTopResize.strokeWidthProperty().add(headerHeight / 8));
        buttonResize.translateYProperty().bind(lineTopResize.strokeWidthProperty().add(headerHeight / 8));
        buttonFullScreen.translateYProperty().bind(lineTopResize.strokeWidthProperty().add(headerHeight / 8));
        buttonMenu.translateYProperty().bind(lineTopResize.strokeWidthProperty().add(headerHeight / 8));
        labelTitle.translateYProperty().bind(lineTopResize.strokeWidthProperty());
        
    	headerAnchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() 
        {	
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
                mousePressedX = mouseEvent.getX();
                mousePressedY = mouseEvent.getY();
            }
        });
    	
    	headerAnchorPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() 
        {
    		//drag window with title/window bar
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
            	if(!fullScreenProperty.get() && !maximizeProperty.get())
            		if(mousePressedY > borderWidth[0] && mouseEvent.getTarget().getClass() == AnchorPane.class)
            		{
            			double crrX = mouseEvent.getScreenX();
            			double crrY = mouseEvent.getScreenY();
            			//Stage stage = (Stage) windowRootStackPane.getScene().getWindow();
            			stage.setX(crrX - mousePressedX);
            			stage.setY(crrY - mousePressedY);
            		}
            }
        });
    	
    	headerAnchorPane.setOnMouseClicked(new EventHandler<MouseEvent>() 
        {
            // Maximize on double click
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
            	long diff = 0;

            	currentClickTime = System.currentTimeMillis();

                if(lastClickTime != 0 && currentClickTime != 0)
                {
                    diff = currentClickTime - lastClickTime;
                    isDoubleClick = diff <= 215 ? true : false;
                }

                lastClickTime = currentClickTime;
                
                if (!stage.isFullScreen() && isDoubleClick)//mouseEvent.getClickCount() > 1) 
                {
                     maximizeProperty().set(!maximizeProperty().get());
                     mouseEvent.consume();
                }
            }
        });
    }
    
    /**
     * Handles sizing of application Pane, windowAppPane
     * 
     * @param
     */
    public void stageResizeListeners(double[] borderWidth, int headerHeight)
    {	
    	stage.widthProperty().addListener(new ChangeListener<Number>() 
        {
            @Override
            public void changed(ObservableValue<? extends Number> num1, Number oldValue, Number newValue) 
            {
            	if(!stage.isFullScreen())
            	{
            		windowAppPane.resizeRelocate(borderWidth[1], headerHeight + borderWidth[0], (double)newValue - borderWidth[1] - borderWidth[3], stage.getHeight() - headerHeight - borderWidth[0] - borderWidth[2]);
            		setWindowAppPaneBounds(windowAppPane.getLayoutBounds());
            		headerAnchorPane.resizeRelocate(borderWidth[1], borderWidth[0], (double)newValue - borderWidth[1] - borderWidth[3], headerHeight);
            	}
            }
        });
    	stage.heightProperty().addListener(new ChangeListener<Number>() 
        {
            @Override
            public void changed(ObservableValue<? extends Number> num1, Number oldValue, Number newValue) 
            {
            	if(!stage.isFullScreen())
            	{
            		windowAppPane.resizeRelocate(borderWidth[1], headerHeight + borderWidth[0], stage.getWidth() - borderWidth[1] - borderWidth[3], (double)newValue - headerHeight - borderWidth[0] - borderWidth[2]);
            		setWindowAppPaneBounds(windowAppPane.getLayoutBounds());
            	}
            }
        });
    }
    
    public SimpleBooleanProperty maximizeProperty() 
    {
        return maximizeProperty;
    }

    public SimpleBooleanProperty minimizeProperty() 
    {
        return minimizeProperty;
    }

    public SimpleBooleanProperty exitProperty() 
    {
        return exitProperty;
    }

    public SimpleBooleanProperty fullScreenProperty() 
    {
        return fullScreenProperty;
    }
    
    /**
     * Initialize Properties
     */
    protected void initializeProperties()
    {
    	// Properties 
        maximizeProperty = new SimpleBooleanProperty(false);
        maximizeProperty.addListener(new ChangeListener<Boolean>() 
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) 
            {
                setMaximized(newValue);
            }
        });
        minimizeProperty = new SimpleBooleanProperty(false);
        minimizeProperty.addListener(new ChangeListener<Boolean>() 
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) 
            {
                minimize();
            }
        });

        exitProperty = new SimpleBooleanProperty(false);
        exitProperty.addListener(new ChangeListener<Boolean>() 
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) 
            {
                exit();
            }
        });
        fullScreenProperty = new SimpleBooleanProperty(false);
        fullScreenProperty.addListener(new ChangeListener<Boolean>() 
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) 
            {
            	String toolTipText = newValue ? "Restore" : "FullScreen";
            	String menuItemText = newValue ? "Restore" : "FullScreen";
            	buttonFullScreen.getTooltip().setText(toolTipText);
            	buttonFullScreen.getTooltip().setFont(defaultFont);
        		fullScreenMenuItem.setText(menuItemText);
        		setFullScreen(newValue);
                //setFullScreen(!stage.isFullScreen());
            }
        });
    }
    
    /**
     *Initialize node:Line to create a Stage Border for resize purposes. 
     *
     * @see	#initializeResizeBorder(double[] borderWidth, Paint[] borderPaint)
     */
    public void initializeResizeBorder()
    {
    	double[] strokeWidth = {10, 10, 10, 10};
    	initializeResizeBorder(strokeWidth);
    }
    /**
     * @param borderWidth double[] of stroke widths: top, left, bottom, right
     * 
     * @see	#initializeResizeBorder()
     */
    public void initializeResizeBorder(double[] borderWidth)
    {
    	int padding = 10;
    	//double[] borderWidth = {10, 10, 10, 10};
        lineTopResize.setStrokeType(StrokeType.OUTSIDE);
        lineTopResize.setManaged(false);
        lineTopResize.endXProperty().bind(stage.widthProperty().subtract(borderWidth[3]));
        lineTopResize.setStartX(borderWidth[1]);
        lineTopResize.setStartY(0);
        lineTopResize.setEndY(0);
        lineTopResize.setStrokeWidth(borderWidth[0]);
        //lineTopResize.minHeight(padding);
        //lineTopResize.setStroke(borderPaint[0]);
        
        //left[1]
        lineLeftResize.setStrokeType(StrokeType.OUTSIDE);
        lineLeftResize.setManaged(false);
        lineLeftResize.setStartX(0);
        lineLeftResize.setEndX(0);
        lineLeftResize.setStartY(0);
        lineLeftResize.endYProperty().bind(stage.heightProperty());
        lineLeftResize.setStrokeWidth(borderWidth[1]);
        lineLeftResize.minWidth(padding);
        //lineLeftResize.setStroke(borderPaint[1]);
        
        //bottom[2]
        lineBottomResize.setStrokeType(StrokeType.OUTSIDE);
        lineBottomResize.setManaged(false);
        lineBottomResize.minHeight(padding);
        lineBottomResize.setStartX(borderWidth[1]);
        lineBottomResize.endXProperty().bind(stage.widthProperty().subtract(borderWidth[3]));
        lineBottomResize.startYProperty().bind(stage.heightProperty());
        lineBottomResize.endYProperty().bind(stage.heightProperty());
        //.setStroke(borderPaint[2]);
        lineBottomResize.setStrokeWidth(borderWidth[2]);
        
        //right[3]
        lineRightResize.setStrokeType(StrokeType.OUTSIDE);
        lineRightResize.setManaged(false);
        lineRightResize.endXProperty().bind(stage.widthProperty());
        lineRightResize.startXProperty().bind(stage.widthProperty());
        lineRightResize.setStartY(0);
        lineRightResize.endYProperty().bind(stage.heightProperty());
        lineRightResize.minWidth(padding);
        lineRightResize.setStrokeWidth(borderWidth[3]);
        //lineRightResize.setStroke(borderPaint[3]);
    }
    
    /**
     * Activate fade in transition on showing application
     */
    public void setFadeInTransition() 
    {
    	windowRootStackPane.setOpacity(0);
        stage.showingProperty().addListener(new ChangeListener<Boolean>() 
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue()) 
                {
                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), windowRootStackPane);
                    fadeTransition.setToValue(1);
                    fadeTransition.play();
                }
            }
        });
    }

    /**
     * Launch the fade out transition. Called from method exit.
     * @param EventHandler<ActionEvent>
     * 
     * @default stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
     */
    public void setFadeOutTransition(EventHandler<ActionEvent> event) 
    {
    	Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.seconds(2), new KeyValue (stage.getScene().getRoot().opacityProperty(), 0)); 
        timeline.getKeyFrames().add(key);   
        timeline.setOnFinished(event); 
        timeline.play();
    }

    /**
     * Manage buttons and menu items
     */
    public void initializeControls() 
    {
        MenuItem minimizeMenuItem = null;
        // Menu
//        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        if (buttonMinimize != null) 
        { // Utility Stage
            minimizeMenuItem = new MenuItem("Minimize");
            minimizeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN));

            minimizeMenuItem.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    switchMinimize();
                }
            });
            contextMenu.getItems().add(minimizeMenuItem);
        }
        
        if(buttonMaximize != null && stage.isResizable()) 
        { 
            maximizeMenuItem = new MenuItem("Maximize");
            maximizeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.ALT_DOWN));
            maximizeMenuItem.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    switchMaximize();
                    //contextMenu.hide();
                }
            });
            contextMenu.getItems().addAll(maximizeMenuItem, new SeparatorMenuItem());
        }
        
        //resize to default window
        if(stage.isResizable()) 
        {
        	MenuItem resizeMenuItem = new MenuItem("Resize");
        	resizeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN));
        	resizeMenuItem.setOnAction(new EventHandler<ActionEvent>() 
        		{
                	@Override
                	public void handle(ActionEvent e) 
                	{
                    	resizeDefaultWindow();
                	}
        		});
        	contextMenu.getItems().addAll(resizeMenuItem, new SeparatorMenuItem());
        }
        

        // Fullscreen
        if(buttonFullScreen != null & stage.isResizable()) 
        {
            fullScreenMenuItem = new MenuItem("FullScreen");
            fullScreenMenuItem.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent e) 
                {
                    switchFullScreen();
                }
            });
            fullScreenMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));

            contextMenu.getItems().addAll(fullScreenMenuItem, new SeparatorMenuItem());
        }

        // exit app
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent e) 
            {
                switchExit();
            }
        });
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.CONTROL_DOWN));
        contextMenu.getItems().add(exitMenuItem);

        buttonMenu.setOnMousePressed(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent t) 
            {
                if (contextMenu.isShowing()) 
                    contextMenu.hide();
                else 
                    contextMenu.show(buttonMenu, Side.RIGHT, 0, 0);
            }
        });

        // exit button
        buttonExitApp.setTooltip(new Tooltip("Exit"));
        buttonExitApp.getTooltip().setFont(defaultFont);
        buttonExitApp.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent t) 
            {
                switchExit();
            }
        });
        
        buttonResize.setTooltip(new Tooltip("Resize"));
        buttonResize.getTooltip().setFont(defaultFont);
        buttonResize.setOnMousePressed(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent t) 
            {
            	resizeDefaultWindow();
            }
        });

        //Maximize context menu
        maximizeProperty().addListener(new ChangeListener<Boolean>() 
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) 
            {
                String buttonText = newValue ? "Restore" : "Maximize";
                buttonMaximize.getTooltip().setText(buttonText);
                buttonMaximize.getTooltip().setFont(defaultFont);
                maximizeMenuItem.setText(buttonText);
            }
        });

        if (buttonMaximize != null) 
        {
            buttonMaximize.setTooltip(new Tooltip("Maximize"));
            buttonMaximize.getTooltip().setFont(defaultFont);
            buttonMaximize.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent t) 
                {
                    switchMaximize();
                }
            });
        }
        
        if (buttonFullScreen != null) 
        {
        	buttonFullScreen.setTooltip(new Tooltip("FullScreen"));
        	buttonFullScreen.getTooltip().setFont(defaultFont);
        	buttonFullScreen.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent t) 
                {
                    switchFullScreen();
                }
            });
        }

        if(buttonMinimize != null) 
        { 
            buttonMinimize.setTooltip(new Tooltip("Minimize"));
            buttonMinimize.getTooltip().setFont(defaultFont);
            buttonMinimize.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent t) 
                {
                    switchMinimize();
                }
            });
        }
    }

    public void switchFullScreen() 
    {
    	//fullScreenProperty.set(!fullScreenProperty().get());
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
            	fullScreenProperty.set(!fullScreenProperty().get());
            }
        });
    }

    public void switchMinimize() 
    {
        minimizeProperty().set(!minimizeProperty().get());
    }

    public void switchMaximize() 
    {
        maximizeProperty().set(!maximizeProperty().get());
    }

    public void switchExit() 
    {
        exitProperty().set(!exitProperty().get());
    }

    /**
     * Resize stage/window to default size and location
     * @return 
     */
    public void resizeDefaultWindow()
    {
    	stage.setWidth(DEFAULT_WIDTH);
    	stage.setHeight(DEFAULT_HEIGHT);
    	Rectangle2D visualBounds = getWindowVisualBounds();
        stage.setX((visualBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((visualBounds.getHeight() - stage.getHeight()) / 2);
        if(maximizeProperty.get())
        	maximizeProperty.set(false);
        if(fullScreenProperty.get())
        	fullScreenProperty.set(false);
        Button[] buttons = {buttonExitApp, buttonMaximize, buttonMinimize, buttonResize, buttonFullScreen};
        for(Button button : buttons)
        	button.setVisible(true);
    }
    
    /**
     * Essentially, identifies the application's monitor and the layout bounds.
     * @return visual bounds of the window
     */
    public static Rectangle2D getWindowVisualBounds()
    {
        ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Screen screen = screensForRectangle.get(0);
        return screen.getVisualBounds();
    }

    protected void setMaximized(boolean value) 
    {
    	if(value)
    	{
    		Rectangle2D visualBounds = getWindowVisualBounds();
    		stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
            Button[] buttons = {buttonExitApp, buttonMaximize, buttonMinimize, buttonResize, buttonFullScreen};
            for(Button button : buttons)
            	button.setVisible(true);
    	}
    	else
    	{
    		resizeDefaultWindow();
    	}
    }

    protected void setFullScreen(boolean value) 
    {
        stage.setFullScreen(value);
    	 
    	if(value)
    	{
    		stage.addEventHandler(KeyEvent.KEY_PRESSED, eventHandlerESConFullScreen);
        	Rectangle2D visualBounds = getWindowVisualBounds();
	    	windowAppPane.resizeRelocate(0, 0, visualBounds.getWidth(), visualBounds.getHeight());
	    	setWindowAppPaneBounds(windowAppPane.getLayoutBounds());
    		windowAppPane.toFront();
         } 
    	else
    	{
    		stage.removeEventHandler(KeyEvent.KEY_PRESSED, eventHandlerESConFullScreen); //remove esc fullscreen event
    		resizeDefaultWindow();
            windowAppPane.toBack();
    	}
    }

    public void exit() 
    {
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
            	if(!STAGE_FADE_IN_OUT)
            		stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            	
            	setFadeOutTransition(new EventHandler<ActionEvent>() 
                {
                    @Override
                    public void handle(ActionEvent ae) 
                    {
                        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                    }
                });
            }
        });
    }

    public void minimize() 
    {
    	//Check for correct thread, issue under Linux?
        if (!Platform.isFxApplicationThread()) 
        {
            Platform.runLater(new Runnable() 
            {
                @Override
                public void run() 
                {
                	minimizeStage();
                }
            });
        } 
        else 
        {
        	minimizeStage();
        }
    }

    private void minimizeStage() 
    {
        stage.setIconified(true);
    }

    /**
     * Stage resize management
     *
     * @param stage
     * @param node
     * @param padding
     */
    public void setResizeEvents(final Node node, double borderWidth) 
    {
    	node.setPickOnBounds(true);
        //RESIZE_PADDING = (int)padding;
    	Button[] buttons = {buttonExitApp, buttonMaximize, buttonMinimize, buttonResize, buttonFullScreen};
        
    	node.setOnMouseReleased(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
            	for(int i = 0; i < buttons.length; i++)
            		//if(labelTitle.getBoundsInParent().intersects(buttons[i].getBoundsInParent()))
            		if(stage.getWidth() > (i + 1) * (buttons[i].getPrefWidth() + 10) + stage.getMinWidth())
            			buttons[i].setVisible(true);
                mouseEvent.consume();
            }
        });
        node.setOnMousePressed(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
                if (mouseEvent.isPrimaryButtonDown()) 
                {
                	for(Button button : buttons)
                    	button.setVisible(false);
                    mousePressInitX = mouseEvent.getScreenX();
                    mousePressInitY = mouseEvent.getScreenY();
                    mouseEvent.consume();
                }
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
                if (!mouseEvent.isPrimaryButtonDown() || (mousePressInitX == -1 && mousePressInitY == -1))
                    return;
                if (stage.isFullScreen()) 
                {
                	setCursor(node, Cursor.DEFAULT);
                    return;
                }
                
                /*
                 * handles accidental micro clicks
                 */
                if (mouseEvent.isStillSincePress()) 
                    return;
                if (maximizeProperty.get()) 
                {
                    // Remove maximized state
                    maximizeProperty.set(false);
                    return;
                } 


                mousePressNewX = mouseEvent.getScreenX();
                mousePressNewY = mouseEvent.getScreenY();
                double deltax = mousePressNewX - mousePressInitX;
                double deltay = mousePressNewY - mousePressInitY;

                Cursor cursor = node.getCursor();
                if (Cursor.E_RESIZE.equals(cursor)) 
                {
                    setStageWidth(stage.getWidth() + deltax);
                    mouseEvent.consume();
                } 
                else if (Cursor.NE_RESIZE.equals(cursor)) 
                {
                    if (setStageHeight(stage.getHeight() - deltay)) 
                    {
                        setStageY(stage.getY() + deltay);
                    }
                    setStageWidth(stage.getWidth() + deltax);
                    mouseEvent.consume();
                } 
                else if (Cursor.SE_RESIZE.equals(cursor)) 
                {
                    setStageWidth(stage.getWidth() + deltax);
                    setStageHeight(stage.getHeight() + deltay);
                    mouseEvent.consume();
                } 
                else if (Cursor.S_RESIZE.equals(cursor)) 
                {
                    setStageHeight(stage.getHeight() + deltay);
                    mouseEvent.consume();
                } 
                else if (Cursor.W_RESIZE.equals(cursor)) 
                {
                    if (setStageWidth(stage.getWidth() - deltax)) 
                    {
                        stage.setX(stage.getX() + deltax);
                    }
                    mouseEvent.consume();
                } 
                else if (Cursor.SW_RESIZE.equals(cursor)) 
                {
                    if (setStageWidth(stage.getWidth() - deltax)) 
                    {
                        stage.setX(stage.getX() + deltax);
                    }
                    setStageHeight(stage.getHeight() + deltay);
                    mouseEvent.consume();
                } 
                else if (Cursor.NW_RESIZE.equals(cursor)) 
                {
                    if (setStageWidth(stage.getWidth() - deltax)) 
                    {
                        stage.setX(stage.getX() + deltax);
                    }
                    if (setStageHeight(stage.getHeight() - deltay)) 
                    {
                        setStageY(stage.getY() + deltay);
                    }
                    mouseEvent.consume();
                } 
                else if (Cursor.N_RESIZE.equals(cursor)) 
                {
                    if (setStageHeight(stage.getHeight() - deltay)) 
                    {
                        setStageY(stage.getY() + deltay);
                    }
                    mouseEvent.consume();
                }

            }
        });
        node.setOnMouseMoved(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent mouseEvent) 
            {
                if (maximizeProperty.get()) 
                {
                    setCursor(node, Cursor.DEFAULT);
                    return; // maximized mode does not support resize
                }
                if (stage.isFullScreen()) 
                {
                	setCursor(node, Cursor.DEFAULT);
                    return;
                }
                if (!stage.isResizable()) 
                {
                    return;
                }
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                Parent parent = node.getParent();
                //Bounds boundsInParent = node.getBoundsInParent();
                if(parent.getClass() == VBox.class)
                {
                    if(x < borderWidth * 2)
                    	setCursor(node, Cursor.NW_RESIZE);
                    else if(x > stage.getWidth() - borderWidth * 2)
                    	setCursor(node, Cursor.NE_RESIZE);
                    else
                    	setCursor(node, Cursor.N_RESIZE);
                    return;
                } 
                if(parent.getClass() != BorderPane.class)
                	return;
                BorderPane borderPane = (BorderPane)parent;
                if(borderPane.getRight() == node) 
                {
                	if(y < borderWidth * 2)
                    	setCursor(node, Cursor.NE_RESIZE);
                    else if(y > stage.getHeight() - borderWidth * 2)
                    	setCursor(node, Cursor.SE_RESIZE);
                    else
                    	setCursor(node, Cursor.E_RESIZE);
                    return;

                } 
                else if(borderPane.getLeft() == node)
                {
                	if(y < borderWidth * 2)
                    	setCursor(node, Cursor.NW_RESIZE);
                    else if(y > stage.getHeight() - borderWidth * 2)
                    	setCursor(node, Cursor.SW_RESIZE);
                    else
                    	setCursor(node, Cursor.W_RESIZE);
                    return;
                } 
                
                else if(borderPane.getBottom() == node) 
                {
                	if(x < borderWidth * 2)
                    	setCursor(node, Cursor.SW_RESIZE);
                    else if(x > stage.getWidth() - borderWidth * 2)
                    	setCursor(node, Cursor.SE_RESIZE);
                    else
                    	setCursor(node, Cursor.S_RESIZE);
                    return;
                } 
                else 
                {
                    setCursor(node, Cursor.DEFAULT);
                }
            }
        });
    }
    

    /**
     * Under Windows, the Stage could be been dragged below the Task
     * bar and then no way to grab it again. Mac, do not drag above the
     * menu bar
     *
     * @param y
     */
    void setStageY(double y) 
    {
        try 
        {
        	Rectangle2D visualBounds = getWindowVisualBounds();
            //ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            //if(screensForRectangle.size() > 0) 
            //{
                if(y < visualBounds.getHeight() - 30 && y >= visualBounds.getMinY())
                    stage.setY(y);
            //}
        } 
        catch (Exception e) 
        {
        	System.out.println("Controller: 971: setStageY issue.");
        }
    }

    boolean setStageWidth(double width) 
    {
        if (width >= stage.getMinWidth()) 
        {
            stage.setWidth(width);
            mousePressInitX = mousePressNewX;
            return true;
        }
        return false;
    }

    boolean setStageHeight(double height) 
    {
        if (height >= stage.getMinHeight()) 
        {
            stage.setHeight(height);
            mousePressInitY = mousePressNewY;
            return true;
        }
        return false;
    }

    public void setCursor(Node n, Cursor c) 
    {
        n.setCursor(c);
    }
}


