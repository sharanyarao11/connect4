package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
    public static final int COLUMNS=7;
    public static final int ROWS=6;
    public static final int CIRCLE_DIAMETER=80;
    public static final String DISCCOLOUR_1="#24303E";
    public static final String DISCCOLOUR_2="#4CAA88";

    public  String PLAYER1;
    public  String PLAYER2;

    private boolean isplayerOne=true;

    private Disc[][] insertDiscArray=new Disc[ROWS][COLUMNS]; //For structural changes for the developers
    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane DiscInsert;
    @FXML
    public Label playernameLabel;
    @FXML
    public Label playerTurn;
    @FXML
    public TextField playerOneTextField,playerTwoTextField;
    @FXML
    public Button setNamesButton;


    private boolean isAllowedToInsert =true;

    public void create_playground()
    {
        Shape rectwithholes=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

        for(int row=0;row<ROWS;row++)
        {
            for(int cols=0;cols<COLUMNS;cols++)
            {
                Circle circle=new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);
                circle.setTranslateX(cols*(CIRCLE_DIAMETER+5)+(CIRCLE_DIAMETER/4));
                circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+(CIRCLE_DIAMETER/4));
                rectwithholes=Shape.subtract(rectwithholes,circle);
            }
        }
        rectwithholes.setFill(Color.WHITE);
        rootGridPane.add(rectwithholes,0,1);

        List<Rectangle> rectangleList=createclickabecolumn();
        for (Rectangle rectangle:rectangleList) {
            rootGridPane.add(rectangle,0,1);


        setNamesButton.setOnAction(event -> {
           PLAYER1=playerOneTextField.getText();
           PLAYER2=playerTwoTextField.getText();
        });
        }
    }
    private List<Rectangle> createclickabecolumn()
    {
        List<Rectangle> rectanglelist=new ArrayList<>();
        for (int i = 0; i < COLUMNS ; i++) {
            Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(i*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column=i;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert){
                    isAllowedToInsert=false;
                    insertDisc(new Disc(isplayerOne),column);
                }

            });
            rectanglelist.add(rectangle);
        }
        return rectanglelist;
    }
    private  void insertDisc(Disc disc, int column){
        int rows=ROWS-1;
        while(rows>=0)
        {
            if(getDiscIfPresent(rows,column)== null)
               break;
            rows--;
        }
        if(rows<0)
            return;

        insertDiscArray[rows][column]=disc;
        DiscInsert.getChildren().addAll(disc);
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+(CIRCLE_DIAMETER/4));
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(rows*(CIRCLE_DIAMETER+5)+(CIRCLE_DIAMETER/4));
        int finalRows = rows;
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert=true;
            if(gameEnded(finalRows,column)){
                gameover();
                return;
            }
            isplayerOne=!isplayerOne;
            playernameLabel.setText(isplayerOne? PLAYER1:PLAYER2);
        });
        translateTransition.play();
    }
    private boolean gameEnded(int row,int cols)
    {
       List<Point2D> verticalPoints = (List<Point2D>) IntStream.rangeClosed(row-3,row+3). //range of row values-0,1,2,3,4,5
               mapToObj(r -> new Point2D(r,cols)) // 0,3 1,3 2,3 3,3 4,3 5,3 --->Point2D x,y
               .collect(Collectors.toList());
        List<Point2D>  horizontalPoints = (List<Point2D>) IntStream.rangeClosed(cols-3,cols+3). //range of row values-0,1,2,3,4,5
                mapToObj(c -> new Point2D(row,c)) // 0,3 1,3 2,3 3,3 4,3 5,3 --->Point2D x,y
                .collect(Collectors.toList());
        Point2D startPoint1= new Point2D(row-3,cols+3);
        List<Point2D> diagonalPoints = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i,-i))
                .collect(Collectors.toList());
        Point2D startPoint2= new Point2D(row-3,cols-3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i,i))
                .collect(Collectors.toList());

        boolean isEnded= checkCombo(verticalPoints) || checkCombo(horizontalPoints)
                || checkCombo(diagonalPoints) || checkCombo(diagonal2Points);

        return isEnded;
    }
    private boolean checkCombo(List<Point2D> points)
    { int check=0;
     for(Point2D point:points)
     {
         int rowIndexOfArray= (int) point.getX();
         int columnIndexofArray= (int) point.getY();
         Disc disc=getDiscIfPresent(rowIndexOfArray,columnIndexofArray);
         if(disc != null && disc.isplayeroneMove == isplayerOne)
         {
             check++;
             if(check == 4 ){
                 return true;
             }
         }
         else
         {
             check =0;
         }
     }
     return false;
    }
    private Disc getDiscIfPresent(int row,int column)// to prevent array bound index
     {
        if(row >= ROWS || row <0 || column>= COLUMNS|| column <0) //If row or column is invalid
            return null;
        return insertDiscArray[row][column];
    }

    private void gameover() {
        String winner=isplayerOne? PLAYER1:PLAYER2;
        System.out.println("Winner is:"+winner);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The winner is "+winner);
        alert.setContentText("Want to play again? ");
        
        ButtonType yesBtn=new ButtonType("Yes");
        ButtonType noBtn=new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn,noBtn);
        Platform.runLater(()->{
            Optional<ButtonType> btnClicked=alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get()== yesBtn)
            {
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void resetGame() {
        DiscInsert.getChildren().clear();
        for (int row=0;row < insertDiscArray.length;row ++) {
            for(int col=0;col<insertDiscArray[row].length; col++){
                insertDiscArray[row][col]=null;
            }
        }
        isplayerOne=true;
        playernameLabel.setText(PLAYER1);
        create_playground();
    }


    private static class Disc extends Circle{

        private final boolean isplayeroneMove;

        public Disc(boolean isplayeroneMove){
            this.isplayeroneMove=isplayeroneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isplayeroneMove? Color.valueOf(DISCCOLOUR_1) : Color.valueOf(DISCCOLOUR_2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
