package com.team766.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class for loading in config from the Config file.
 * Constants that need to be tuned / changed
 * Returns zero if config setting not found.
 * 
 * Data is read from CSV files in the format:
 * 
 * kDriveDistance, 100
 * kPDrive,1
 * kIDrive,0
 * 
 * @author Brett Levenson
 */
public class ConfigFileReader {
	// this.getClass().getClassLoader().getResource(fileName).getPath()
	
	public static ConfigFileReader instance;
	
	private String fileName;
	private HashMap<String, String> values;
	
	public static ConfigFileReader getInstance(){
		return instance;
	}
	
	public ConfigFileReader(String fileName){
		this.fileName = fileName;
		
		reloadConstants();
	}
	
	public void reloadConstants(){
		System.out.println("Loading config file: " + fileName);
		HashMap<String, String> new_values = new HashMap<String, String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			reader.lines().forEach((currLine) -> {
				// Separate the key from the value
				String[] tokens = currLine.split(",", 2);
				new_values.put(tokens[0], tokens[1]);
			});
			reader.close();
		} catch (IOException e) {
			System.err.println("Failed to load config file!");
			e.printStackTrace();
		}
		values = new_values;
	}
	
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}
	
	private int[] stringToInts(String in){
		String[] inArr = in.split(",");
		int[] out = new int[inArr.length];
		
		for(int i = 0; i < inArr.length; i++){
			out[i] = Integer.parseInt(inArr[i]);
		}
		return out;
	}

	private double[] stringToDoubles(String in){
		String[] inArr = in.split(",");
		double[] out = new double[inArr.length];
		
		for(int i = 0; i < inArr.length; i++){
			out[i] = Double.parseDouble(inArr[i]);
		}
		return out;
	}
	
	public int[] getInts(String key){
		return stringToInts(getString(key));
	}
	
	public int getInt(String key){
		int[] value = getInts(key);
		if (value.length != 1) {
			throw new IllegalArgumentException(key + " has " + value.length + " config values, but expected 1");
		}
		return value[0];
	}
	
	public double[] getDoubles(String key){
		return stringToDoubles(getString(key));
	}
	
	public double getDouble(String key){
		double[] value = getDoubles(key);
		if (value.length != 1) {
			throw new IllegalArgumentException(key + " has " + value.length + " config values, but expected 1");
		}
		return value[0];
	}
	
	public boolean getBoolean(String key){
		String in = getString(key);
		return Boolean.parseBoolean(in);
	}
	
	public String getString(String key){
		return values.get(key);
	}
	
}
