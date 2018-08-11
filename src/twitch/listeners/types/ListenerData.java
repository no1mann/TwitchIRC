package twitch.listeners.types;

public class ListenerData{
	private ListenerType type;
	private Object data;
	
	public ListenerData(ListenerType type, Object data){
		this.type = type;
		this.data = data;
	}
	
	public Object getData(){
		return data;
	}
	
	public ListenerType getType(){
		return type;
	}
}
