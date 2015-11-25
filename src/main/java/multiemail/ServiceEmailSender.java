package multiemail;

import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceEmailSender extends Service<Boolean>{

	final static Logger logger = Logger.getLogger(ServiceEmailSender.class);
	
	private ObservableList<Person> data;
	private Properties conf;
	private EmailContent content;

	public ServiceEmailSender(Properties conf, EmailContent content, ObservableList<Person> data) {
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
				boolean anyFalse = false;
				int total = data.size(), count = 0;
				updateProgress(count, total);
				for (Person person : data) {
					
					updateMessage("Sending email to " + person.getEmail());
					try{
						sendEmail(person.getEmail());
						updateMessage("Email sent properly to " + person.getEmail());
					}catch(Exception emx){
						updateMessage("Wrong sent to " + person.getEmail());
						logger.error("Error sending email", emx);
						anyFalse = true;
					}

					Thread.sleep(Integer.parseInt(conf.getProperty("mail.delay", "100")));

					updateProgress(++count, total);
				}
				if(!anyFalse){
					updateMessage("Finished OK");
				}else {
					updateMessage("There were some errors, please check logs");
				}
				return !anyFalse;
			}
		};
	}

	private void sendEmail(String emailTo) throws EmailException{
		HtmlEmail email = new HtmlEmail();
		email.setHostName(conf.getProperty("mail.smtp.host"));
		email.setSmtpPort(Integer.parseInt(conf.getProperty("mail.smtp.port", "465")));
		String username = conf.getProperty("mail.smtp.user");
		String password = Cypher.decrypt(conf.getProperty("mail.smtp.pass"));
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
//		email.setTextMsg(content.getAccessibleText());
		email.send();
	}
}
