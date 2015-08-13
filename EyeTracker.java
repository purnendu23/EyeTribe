package edu.tamu.civilSketch.eyeTracker;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;



public class EyeTracker {
	
	private static GazeManager gm;
	private static GazeListener gazeListener;
	private static String outputfile;
	
	public static void setOutputFile(String s){
		
		outputfile = s;
	}
	public static void startTracking() {
		
		gazeListener = new GazeListener(outputfile);
		gm = GazeManager.getInstance();
		gm.addGazeListener(gazeListener);
        
		/*
		*Activating the GazeManager to actually start the Eye-Tracker
		*/
		boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
		if(success != true)
			System.out.println("EyeTracker has not started");
		
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            /*
    		*Following run() method is executed when all threads of the application finish executing
    		*thereby stopping the eye-tracking.
    		*/
            public void run()
            {
                gazeListener.finalwrite();
            	gm.removeGazeListener(gazeListener);
                gm.deactivate();
            }
        });
		
	}
	
	
	private static void stopTracking() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
	
	
}
