package multiemail;

import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.HTMLEditor;

public class ServiceEmailSender extends Service<Boolean>{

	final static Logger logger = Logger.getLogger(ServiceEmailSender.class);
	
	private ObservableList<ObservableList<String>> data;
	private Properties conf;
	private EmailContent content;

	public ServiceEmailSender(Properties conf, EmailContent content, ObservableList<ObservableList<String>> data) {
		this.conf = conf;
		this.content = content;

		this.data = data;
	}

	@Override
	protected Task<Boolean> createTask() {
		// TODO Auto-generated method stub
		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				int total = 100;
				updateProgress(0, total);
				for (int i = 0; i < total; i++) {
					Thread.sleep(100);
					if(i==50){
						updateMessage("Everything going smoothly");
					}
					updateProgress(i, total);
				}
				updateMessage("Finishd OK");
				return true;
			}
		};
	}

	private void sendEmail(String emailTo) throws EmailException{
		HtmlEmail email = new HtmlEmail();
		email.setHostName(conf.getProperty("mail.smtp.host"));
		email.setSmtpPort(Integer.parseInt(conf.getProperty("mail.smtp.port", "465")));
		String username = conf.getProperty("mail.smtp.user");
		String password = conf.getProperty("mail.smtp.pass");
		email.setAuthenticator(new DefaultAuthenticator(username, password));
		boolean ssl = true;
		try{
		ssl = Boolean.parseBoolean(conf.getProperty("mail.smtp.ssl"));
		}catch(Exception ex){}
		email.setSSLOnConnect(ssl);
		email.addTo(emailTo);
		email.setFrom(conf.getProperty("mail.from"));
		email.setSubject(content.getSubject());
		email.setHtmlMsg(content.getHtmlText());
		email.setTextMsg(content.getAccessibleText());
		email.send();
	}
}
