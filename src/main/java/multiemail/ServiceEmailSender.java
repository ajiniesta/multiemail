package multiemail;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceEmailSender extends Service<Boolean>{

	
	
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

}
