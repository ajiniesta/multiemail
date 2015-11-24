package multiemail;

import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Manager extends Application {

	final static Logger logger = Logger.getLogger(Manager.class);
	
	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		logger.info("Starting the application");
		logger.debug("Loading the fxml");
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Manager.fxml"));
		Parent parent = loader.load();
		logger.debug("fxml loaded");
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		logger.info("Showing stage");
		stage.show();
		
	}

}
