package com.team766.hal.wpilib;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;

import org.opencv.core.Mat;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class CameraInterface implements com.team766.hal.CameraInterface {

	private CvSource vidSource;

	@Override
	public void startAutomaticCapture() {
		try{
			CameraServer.getInstance().startAutomaticCapture(VideoSource.enumerateSources()[0]);
		} catch(Exception e){
			Logger.get(Category.CAMERA).logRaw(Severity.ERROR, e.toString());
		}
	}

	@Override
	public void getFrame(Mat img) {
		CameraServer.getInstance().getVideo().grabFrame(img);
	}

	@Override
	public void putFrame(Mat img){
		if(vidSource == null){
			vidSource = CameraServer.getInstance().putVideo("VisionTracking", img.width(), img.height());
		}

		vidSource.putFrame(img);
	}
}
