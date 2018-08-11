package twitch.utils;

import java.util.LinkedList;

/*
 * A stack data structure with a limited size 
 */
public class ChatStack<T> extends LinkedList<T>{
	private static final long serialVersionUID = 1L;
	private int messagesToLog = 15;
	private int count = 0;
	
	public ChatStack(){
		super();
	}
	
	//Once an item is added, if the size extends more than the max length, the bottom item gets removed
	@Override
	public synchronized boolean add(T item){
		if(count==messagesToLog){
			removeLast();
		}
		else{
			count++;
		}
		super.addFirst(item);
		return true;
	}
	
	//Cycles through the stack
	public synchronized T nextCyle(){
		T item = super.removeFirst();
		super.addLast(item);
		return item;
	}
	
	@Override
	public synchronized T remove(){
		return super.remove();
	}
	
	public void setMessagesToLog(int amount){
		this.messagesToLog = amount;
	}

	public synchronized void clear(){
		super.clear();
	}
}
