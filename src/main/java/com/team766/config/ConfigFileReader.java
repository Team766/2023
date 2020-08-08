package com.team766.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

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
	
	// This is incremented each time the config file is reloaded to ensure that ConfigValues use the most recent setting.
	private int generation = 0;
	
	private String fileName;
	private HashMap<String, String> values;
	
	public static ConfigFileReader getInstance() {
		return instance;
	}
	
	public ConfigFileReader(String fileName) {
		this.fileName = fileName;
		
		reloadConstants();
	}
	
	public void reloadConstants() {
		System.out.println("Loading config file: " + fileName);
		HashMap<String, String> new_values = new HashMap<String, String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			reader.lines().forEach((currLine) -> {
				String cleanedLine = currLine;
				int commentBegin = currLine.indexOf('#');
				if (commentBegin >= 0) {
					cleanedLine = cleanedLine.substring(0, commentBegin);
				}
				cleanedLine = cleanedLine.trim();
				// Separate the key from the value
				String[] tokens = cleanedLine.split(",", 2);
				if (tokens.length < 2) {
					return;
				}
				new_values.put(tokens[0], tokens[1]);
			});
			reader.close();
		} catch (IOException e) {
			System.err.println("Failed to load config file!");
			e.printStackTrace();
		}
		values = new_values;
		++generation;
	}
	
	public int getGeneration() {
		return generation;
	}
	
	boolean containsKey(String key) {
		return values.containsKey(key);
	}
	
	public ValueProvider<Integer[]> getInts(String key) {
		return new IntegerConfigMultiValue(key);
	}
	
	public ValueProvider<Integer> getInt(String key) {
		return new IntegerConfigValue(key);
	}
	
	public ValueProvider<Double[]> getDoubles(String key) {
		return new DoubleConfigMultiValue(key);
	}
	
	public ValueProvider<Double> getDouble(String key) {
		return new DoubleConfigValue(key);
	}
	
	public ValueProvider<Boolean> getBoolean(String key) {
		return new BooleanConfigValue(key);
	}
	
	public ValueProvider<String> getString(String key) {
		return new StringConfigValue(key);
	}
	
	String getRawString(String key){
		return values.get(key);
	}
	
	public void setValues(Map<String, String> changedValues) {
		values.putAll(changedValues);
		++generation;
	}
	
	public void saveToFile() throws IOException {
		FileWriter writer = new FileWriter(fileName);
		try {
			for (Map.Entry<String, String> entry : values.entrySet()) {
				writer.write(entry.getKey());
				writer.write(',');
				writer.write(entry.getValue());
				writer.write('\n');
			}
		} finally {
			writer.close();
		}
		Logger.get(Category.CONFIGURATION).logRaw(Severity.INFO, "Config file written to " + fileName);
	}
}
