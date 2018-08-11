package twitch.message;

import java.util.concurrent.PriorityBlockingQueue;
import twitch.connection.ChatConnection;
import twitch.message.types.Message;
import twitch.user.User;

/*
 * Message processor for a chat channel
 */
public class MessageProcessor {

	//List of messages needed to process (global)
	private static PriorityBlockingQueue<Message> processList;
	//Checks for 3rd party chat badges (FFZ donator) (global)
	private static PriorityBlockingQueue<User> customBadgeList;
	private static boolean process = true;
	private ChatConnection connection;
	
	//Initiates all global threads and data structures
	static{
		customBadgeList = new PriorityBlockingQueue<User>();
		processList = new PriorityBlockingQueue<Message>();
		Thread badgeThread = new Thread(new CustomBadgeThread(), "CustomBadgesThread");
		badgeThread.start();
		Thread messageThread = new Thread(new MessageProcessingThread(), "MessageProcessingThread");
		messageThread.start();
	}
	
	public MessageProcessor(ChatConnection connection){
		this.connection = connection;
	}
	
	//Processes a new message
	public void process(final Message message) {
		if (process) 
			forceProcess(message);
		else
			message.callListeners();
	}
	
	//Bypasses process block and forces the message to process
	public void forceProcess(final Message message){
		//If sender of message has not sent a message yet, check for custom badges
		if (!message.getSender().equals(connection.getLogger()) && !message.getSender().hasCheckedCustomBadges())
			customBadgeList.add(message.getSender());
		//Process message
		processList.add(message);
		//Log the message for future reference
		connection.logMessage(message);
		//Call message listeners
		message.callListeners();
	}
	
	public static void enableProcess(boolean val){
		process = val;
	}
	
	//Global message processing thread
	private static class MessageProcessingThread implements Runnable{
		@Override
		public void run() {
			while(true){
				//If process list is not empty, process the messages
				if(!processList.isEmpty()){
					for(Message mes : processList){
						//Process message
						mes.appendMessage();
						//Remove lines if needed
						try {
							mes.getChatConnection().getStyledChat().removeLines();
						} catch (Exception e) {}
					}
					//Scroll down to the bottom after processing
					for(Message mes : processList)
						mes.getChatConnection().scroll();
					//Clear the processing list
					processList.clear();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	//Global badge processing thread
	private static class CustomBadgeThread implements Runnable{
		@Override
		public void run() {
			while(true){
				//If custom badge list is not empty, process the users
				if(!customBadgeList.isEmpty()){
					for(User user : customBadgeList)
						user.checkCustomBadges();
					customBadgeList.clear();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
		}
	}
}
