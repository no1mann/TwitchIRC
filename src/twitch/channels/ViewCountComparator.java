package twitch.channels;

import java.util.Comparator;

public class ViewCountComparator implements Comparator<StreamInfo>{

	//Compares viewer counts
	@Override
	public int compare(StreamInfo arg0, StreamInfo arg1) {
		return Long.compare(arg0.getViewers(), arg1.getViewers());
	}

}
