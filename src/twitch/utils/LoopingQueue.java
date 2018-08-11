package twitch.utils;

import java.util.Iterator;
import java.util.LinkedList;

/*
 * Queue data structure except when an item is dequeued, it gets automatically enqueued.
 * Thread safe.
 */
public class LoopingQueue<T> implements Iterable<T>{

	private LinkedList<T> queue;
	private LinkedList<T> backup;
	private int count = 0;
	
	public LoopingQueue(){
		initialize();
	}
	
	//Initializes data structure
	private synchronized void initialize(){
		queue = new LinkedList<T>();
		backup = new LinkedList<T>();
	}
	
	//Creates a backup of the queue
	@SuppressWarnings("unchecked")
	public synchronized void backup(){
		backup = (LinkedList<T>) queue.clone();
	}
	
	//Adds item to the queue
	public synchronized void add(T item){
		queue.addLast(item);
	}
	
	//Removes first item from queue, adds it to the end, then returns the new first item
	public synchronized T get(){
		T item = queue.removeFirst();
		this.add(item);
		count--;
		return queue.getFirst();
	}
	
	//Removes last item from queue, adds it to the beginning, then returns the new first item
	public synchronized T getReverse(){
		T item = queue.removeLast();
		queue.addFirst(item);
		count++;
		return queue.getFirst();
	}
	
	//Gets the first item without changing the queue
	public synchronized T preview(){
		if(queue.size()==0)
			return null;
		
		return queue.getFirst();
	}
	
	//Gets the last item without changing the queue
	public synchronized T previewReverse(){
		if(queue.size()==0)
			return null;
		
		return queue.getLast();
	}

	@Override
	public synchronized Iterator<T> iterator() {
		return queue.iterator();
	}
	
	//Clears the queue
	public synchronized void clear(){
		queue.clear();
		initialize();
	}
	
	public synchronized int getSize(){
		return count;
	}
	
	public synchronized String toString(){
		return queue.toString();
	}
	
	//Resets the queue to its original state
	@SuppressWarnings("unchecked")
	public synchronized void resetLoop(){
		queue = (LinkedList<T>) backup.clone();
	}
}
