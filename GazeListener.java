package edu.tamu.civilSketch.eyeTracker;

/*GazeListener listens to the Eye-tracker and the method onGazeUpdate is run every time
 * a GazeData packet is received.
 * */

import java.util.ArrayList;

import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.IGazeListener;

import edu.tamu.civilSketch.datacollectors.csvFileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javafx.scene.paint.Color;

import javax.swing.JPanel;

public class GazeListener implements IGazeListener {
	
	private static GazeDataPointsCollection incoming;
	private static GazeDataPointsCollection outgoing;
	private static String outgoingLock = "outgoingLock";
	private static String incomingLock = "incomingLock";
	private static csvFileWriter csvWriter;
	private static String csvFileHeader = "State,StateToString,TimeStamp,SmoothX,SmoothY,IsFixated,LeftEyePupil,RightEyePupil" ;
	private static int countOfPointsInCollection;
	private static final int GazeDataPointsCollectionSize=2000;
	public int total;
	private Graphics g;
	private JPanel p;
	private static Logger logger = LoggerFactory.getLogger(GazeListener.class);
	
	/*
	 * Getters for 'Incoming' and 'outgoing' GazeDataPointsCollection
	 */
	
	public int countOfPointsInCollection(){
		return countOfPointsInCollection;
	}
	public static GazeDataPointsCollection getIncoming(){
		return GazeListener.incoming;
	}
	public static GazeDataPointsCollection getOutgoing() {
		return GazeListener.outgoing;
	}
	
	/*
	 * Setters for 'Incoming' and 'outgoing' GazeDataPointsCollection
	 */
	public static void setIncoming(GazeDataPointsCollection c) {
		synchronized(GazeListener.incomingLock){
		GazeListener.incoming = c;
		}
	}
	public static void setOutgoing(GazeDataPointsCollection c) {
		synchronized(GazeListener.outgoingLock){
			GazeListener.outgoing = c;
			}
	}
	
	
	public GazeListener(String outputfilename){
		csvWriter = new csvFileWriter(outputfilename,csvFileHeader);
		incoming = new GazeDataPointsCollection(GazeDataPointsCollectionSize);
		outgoing = null;
		countOfPointsInCollection=0;
		total=0;
		p=new JPanel();

	}
	
	@Override
    public void onGazeUpdate(GazeData gazePoint)
    {
		total++;
		//Add the GazePoint and increment the count
		incoming.addGazePoint(gazePoint);
		
		//gazePoint.smoothedCoordinates
		countOfPointsInCollection++;
		
		if(countOfPointsInCollection == GazeDataPointsCollectionSize){
			setOutgoing(incoming);
			setIncoming(new GazeDataPointsCollection(GazeDataPointsCollectionSize));
			countOfPointsInCollection=0;
			Thread t = new Thread(new Runnable() {
							public void run() 
							{
								writeToFile();
								}
							}
			                                                       );
			t.start();
		}
    }
	
	
	private void writeToFile() {
		ArrayList<String> GazePointVariableTuple;
		synchronized(outgoingLock){
			for (GazeData datapoint : outgoing)
			{
				GazePointVariableTuple = new ArrayList<String>();
				GazePointVariableTuple.add((datapoint.state).toString());
				GazePointVariableTuple.add((datapoint.stateToString()));
				GazePointVariableTuple.add(Long.toString(datapoint.timeStamp));
				GazePointVariableTuple.add(Double.toString(datapoint.smoothedCoordinates.x));
				GazePointVariableTuple.add(Double.toString(datapoint.smoothedCoordinates.y));
				GazePointVariableTuple.add((datapoint.isFixated).toString());
				GazePointVariableTuple.add(Double.toString(datapoint.leftEye.pupilSize));
				GazePointVariableTuple.add(Double.toString(datapoint.rightEye.pupilSize));
				csvWriter.writeTuple(GazePointVariableTuple);
			}
		
		}
		
	}
	
	private void writeToFilee() {
		ArrayList<String> GazePointVariableTuple;
		
			for (GazeData datapoint : outgoing)
			{
				GazePointVariableTuple = new ArrayList<String>();
				GazePointVariableTuple.add((datapoint.state).toString());
				GazePointVariableTuple.add((datapoint.stateToString()));
				GazePointVariableTuple.add(datapoint.timeStampString);
				GazePointVariableTuple.add(Double.toString(datapoint.smoothedCoordinates.x));
				GazePointVariableTuple.add(Double.toString(datapoint.smoothedCoordinates.y));
				GazePointVariableTuple.add((datapoint.isFixated).toString());
				GazePointVariableTuple.add(Double.toString(datapoint.leftEye.pupilSize));
				GazePointVariableTuple.add(Double.toString(datapoint.rightEye.pupilSize));
				csvWriter.writeTuple(GazePointVariableTuple);
			}
	}
	
	public void finalwrite(){
		setOutgoing(incoming);
		writeToFilee();
		logger.info("Total Obsvations:"+Integer.toString(total));
		csvWriter.FlushAndClose();
	}
	
}
