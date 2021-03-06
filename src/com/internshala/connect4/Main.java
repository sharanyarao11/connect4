package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connect4.fxml"));
        GridPane rootgridPane = loader.load();

        controller = loader.getController();
        controller.create_playground();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menupane = (Pane) rootgridPane.getChildren().get(0);
         menupane.getChildren().addAll(menuBar);
        Scene scene = new Scene(rootgridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four Game");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public MenuBar createMenu() {
        //create menu
        Menu file = new Menu("File");
        MenuItem newgame = new MenuItem("New Game");
        newgame.setOnAction(event -> controller.resetGame() );

        MenuItem resetgame = new MenuItem("Reset Game");
        resetgame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");

        exitGame.setOnAction(event -> {exitGame();});
        file.getItems().addAll(newgame, resetgame, separatorMenuItem, exitGame);

        Menu help = new Menu("Help");
        MenuItem aboutgame = new MenuItem("About Game");
        aboutgame.setOnAction(event -> { aboutConnect4();});
        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        MenuItem aboutme = new MenuItem("About Me");
        aboutme.setOnAction(event -> { AboutMe();});
        help.getItems().addAll(aboutgame, separatorMenuItem1, aboutme);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(file, help);
        return menuBar;

    }

    private void AboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developers");
        alert.setHeaderText("Sharanya Rao");
        alert.setContentText("You know me :)");
        alert.show();

    }
    private void exitGame(){
        Platform.exit();
        System.exit(0);
    }
    private void resetGame() {
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How to Play");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid." +
                " The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. " +
                "Connect Four is a solved game. The first player can always win by playing the right moves.");
         alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}