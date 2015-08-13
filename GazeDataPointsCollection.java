package edu.tamu.civilSketch.eyeTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.theeyetribe.client.data.GazeData;

public class GazeDataPointsCollection implements Iterable<GazeData> {

	private List<GazeData> list;
	
	public GazeDataPointsCollection(int size){
		list = new ArrayList<GazeData>(size);
	}
	
	public void set(List<GazeData> l){
		list=l;
	}
	public List<GazeData> get(){
		return this.list;
	}
	public void addGazePoint(GazeData gd){
		list.add(gd);
	}

	@Override
	public Iterator<GazeData> iterator() {
		// TODO Auto-generated method stub
		return list.iterator() ;
	}
}
