package multiemail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ManagerController {

	final static Logger logger = Logger.getLogger(ManagerController.class);

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="progressBar"
	private ProgressBar progressBar; // Value injected by FXMLLoader

	@FXML // fx:id="tableView"
	private TableView<ObservableList<String>> tableView;

	@FXML
	private TableColumn<ObservableList<String>, String> colEmail;

	@FXML // fx:id="colName"
	private TableColumn<ObservableList<String>, String> colName;

	@FXML // fx:id="colTelephone"
	private TableColumn<ObservableList<String>, String> colTelephone;

	@FXML // fx:id="toolBar"
	private ToolBar toolBar; // Value injected by FXMLLoader

	@FXML
	private HTMLEditor editor;

	@FXML
	private Label status;

	private Properties conf;

	// Handler for Button[Button[id=null, styleClass=button]] onAction
	@FXML
	void loadFile(ActionEvent event) {
		// handle the event here
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Excel files", Arrays.asList("*.xls", "*.xlsx")));
		chooser.setTitle("Open a file to load");
		File inputFile = chooser.showOpenDialog(null);
		if (inputFile != null) {
			logger.info("Loading file " + inputFile.getAbsolutePath());
			System.out.println("Loading file");
			ServiceLoaderExcel service = new ServiceLoaderExcel(inputFile);
			bindService(service);
			tableView.itemsProperty().bind(service.valueProperty());
			service.start();

		} else {
			logger.info("No file to be loaded");
			abortOperationAlert("No file to be loaded");
		}
	}

	// Handler for Button[Button[id=null, styleClass=button]] onAction
	@FXML
	void saveFile(ActionEvent event) {
		System.out.println("Saving File");
	}

	// Handler for Button[Button[id=null, styleClass=button]] onAction
	@FXML
	void sendEmails(ActionEvent event) {
		System.out.println("Sending Emails...");
		if (checkIfCanSend()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("You are about to send a lot of emails");
			alert.setContentText("Are you sure to send emails?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				ServiceEmailSender service = new ServiceEmailSender(conf,
						new EmailContent("Fixed subject", editor.getHtmlText(), editor.getAccessibleText()),
						tableView.getItems());
				bindService(service);
				service.start();
			} else {
				logger.info("Email sent aborted");
			}
		} else {
			String message = "No text to send in the message";
			logger.error(message);
			abortOperationAlert(message);
		}
	}

	private boolean checkIfCanSend() {
		String htmlText = editor.getHtmlText();
		String text = editor.getAccessibleText();
		logger.debug("Message to send: " + htmlText);
		logger.debug("Message to send text: " + text);
		return text != null;
	}

	private void abortOperationAlert(String message) {
		Alert alert;
		alert = new Alert(AlertType.WARNING);
		alert.setTitle("Operation Aborted");
		alert.setHeaderText("You aborted the last operation");
		alert.setContentText(message);

		alert.showAndWait();
	}

	private void bindService(Service<?> service) {
		toolBar.disableProperty().bind(service.runningProperty());
		tableView.disableProperty().bind(service.runningProperty());
		progressBar.progressProperty().bind(service.progressProperty());
		editor.disableProperty().bind(service.runningProperty());
		status.textProperty().bind(service.messageProperty());
		service.runningProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (!newValue) {
				progressBar.progressProperty().unbind();
				progressBar.setProgress(0);
			}
		});
	}

	@FXML // This method is called by the FXMLLoader when initialization is
			// complete
	void initialize() {
		assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'Manager.fxml'.";
		assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'Manager.fxml'.";
		assert toolBar != null : "fx:id=\"toolBar\" was not injected: check your FXML file 'Manager.fxml'.";

		// Initialize your logic here: all @FXML variables will have been
		// injected
		// Properties configuration = new Properties();
		// configuration.load();

		colName.setCellValueFactory(param -> {
			return new SimpleStringProperty(param.getValue().get(0));
		});
		colTelephone.setCellValueFactory(param -> {
			return new SimpleStringProperty(param.getValue().get(1));
		});
		colEmail.setCellValueFactory(param -> {
			return new SimpleStringProperty(param.getValue().get(2));
		});

		conf = new Properties();
		String userHome = System.getProperty("user.home");
		String confHome = userHome + System.getProperty("file.separator") + ".multiemail";		
		logger.debug("Configuartion directory is: " + confHome);
		File confDir = new File(confHome);
		if(!confDir.exists()){
			confDir.mkdirs();
		}	
		String confFile = confHome + System.getProperty("file.separator") + "multiemail.properties";
		try {			
			conf.load(new FileInputStream(new File(confFile)));
			logger.info("Properties " + confFile + " properly loaded");
		} catch (IOException e) {
			logger.warn("No configuration loaded", e);
			boolean ok = fillConfManually();
			if(ok){
				ok = storeProperties(confFile, "First one");
			}
			if (!ok) {
				logger.error("No configuration detected, application will shutdown. Restart the application and fill the configuration");
				System.exit(-1);
			}
		}
		String databaseFile = confHome + System.getProperty("file.separator") + "multiemail.db";
		conf.setProperty("db.file", databaseFile);
		storeProperties(confFile, "Added db.file");
	}

	private boolean storeProperties(String confFile, String comments) {
		boolean ok = true;
		try (FileWriter fw = new FileWriter(confFile)){
			conf.store(fw, comments);
			logger.info("Properties " + confFile + " properly stored");
		}catch(IOException ex) {
			logger.error("Error saving the properties", ex);
			ok = false;
		}
		return ok;
	}

	private boolean fillConfManually() {
		String[] keys = new String[]{"mail.smtp.host", "mail.smtp.user", "mail.smtp.pass", "mail.smtp.ssl", "mail.smtp.from"};
		String[] dflValues = new String[]{"smtp.gmail.com", "user@gmail.com", "pass", "true", "user@gmail.com"};
		boolean[] cypher = new boolean[]{false, false, true, false, false};
		showMessage();
		for (int i = 0; i < keys.length; i++) {
			boolean ok = fillProperty(keys[i], dflValues[i], cypher[i]);
			if(!ok){
				return false;
			}
		}
		return true;
	}
	
	private boolean fillProperty(String key, String defaultValue, boolean cypher) {
		TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle("Configuration");
		dialog.setHeaderText("Configuartion Property: " + key);
		dialog.setContentText("Please enter value:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()){
			String output = result.get();
			if(cypher){
				output = Cypher.encrypt(output);
			}
			conf.setProperty(key, output);
			return true;					
		}else {
			return false;
		}
	}

	private void showMessage(){
		Alert alert;
		alert = new Alert(AlertType.ERROR);
		alert.setTitle("No configuration detected");
		alert.setContentText("Please fill the next configurations to start the application");

		alert.showAndWait();
	}
}
