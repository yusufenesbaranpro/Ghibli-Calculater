package calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CalculatorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("calculator.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            if (fxmlUrl == null) {
                System.err.println("ERROR: calculator.fxml not found in classpath!");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);

            Scene scene = new Scene(root, 370, 720);

            primaryStage.setTitle("Ghibli Hesap Makinesi");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(370);
            primaryStage.setMinHeight(700);
            primaryStage.setResizable(true);

            // Uygulama ikonu (Abakus)
            try {
                javafx.scene.image.Image icon = new javafx.scene.image.Image(
                        getClass().getResourceAsStream("icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Icon yuklenemedi: " + e.getMessage());
            }

            primaryStage.show();

            System.out.println("Application started successfully!");

        } catch (Exception e) {
            System.err.println("=== APPLICATION ERROR ===");
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
