package com.team766.hal.mock;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.team766.hal.CameraReader;

public class MockCamera implements CameraReader{

	private String nextImage;
	
	@Override
	public Mat getImage() {
		if(nextImage == null) {
			return null;
		}
		
		return Imgcodecs.imread(nextImage);
	}
	
	public void setNextImage(String nextImage){
		this.nextImage = this.getClass().getClassLoader().getResource(nextImage).getPath();
	}

}
