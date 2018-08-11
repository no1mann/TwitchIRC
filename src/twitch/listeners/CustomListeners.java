package twitch.listeners;

import com.esotericsoftware.minlog.Log;

import twitch.connection.ChatConnection;
import twitch.gui.AttributeList;
import twitch.gui.windows.Window;
import twitch.listeners.types.*;
import twitch.message.types.*;
import twitch.user.User;

/*
 * Custom listeners for debugging purposes
 */
public class CustomListeners {

	private static long timeLastMessage = 0;
	private static final long timeToWait = 30 * 1000; //1 Minute
	
	static {
		Log.info("Starting Custom Listeners");
		
		GlobalListeners.addMessageListener(new MessageListener(){
			@Override
			public void onChatMessage(final ChatMessage message) {
				System.out.println(message);
				if(timeLastMessage + timeToWait <= System.currentTimeMillis()){
					timeLastMessage = System.currentTimeMillis();
					//message.getChatConnection().sendMessage("@" + message.getSender().getDisplayName() + " said: " + message.getMessage());
				}
			}

			@Override
			public void onBroadcastMessage(final BroadcastMessage message) {

			}		
		});
		
		GlobalListeners.addChannelListener(new ChannelListener(){
			@Override
			public void onChannelJoin(ChatConnection channel) {
				GlobalListeners.enableListeners(false);
				return;
				/*if(Dialogs.confirmation("Keep Listeners Enabled?", "Keep Listeners Enabled?"))
					GlobalListeners.enableListeners(true);
				else
					GlobalListeners.enableListeners(false);
				*/
			}

			@Override
			public void onUserListReload() {
				
			}

			@Override
			public void onChannelLeave(String channel) {
				
			}
		});
		
		GlobalListeners.addWindowListener(new WindowListener(){
			@Override
			public void onStartup() {
				GlobalListeners.enableListeners(false);
			}

			@Override
			public void onShutdown() {

			}	
		});
		
		GlobalListeners.addCommandListener(new CommandListener(){

			@Override
			public void onCommand(CommandEvent command) {
				@SuppressWarnings("unused")
				String[] split = command.getMessage().split(" ");
				if(command.getMessage().equalsIgnoreCase("enable")){
					print(command.getSender(), "Listeners have been enabled");
					GlobalListeners.enableListeners(true);
				}
				else if(command.getMessage().equalsIgnoreCase("disable")){
					print(command.getSender(), "Listeners have been disabled");
					GlobalListeners.enableListeners(false);
				}
			}
			
			private void print(User sender, String s){
				print(s + " by " + sender.getUsername());
			}
			
			private void print(String s){
				Window.getCurrentConnection().getStyledChat().appendText(s, AttributeList.getLogAttribute(), true);
			}
		});
	}

	
}
