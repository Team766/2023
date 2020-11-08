package com.team766.config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

/**
 * Class for loading in config from the Config file.
 * Constants that need to be tuned / changed
 * 
 * Data is read from a file in JSON format
 * 
 * @author Brett Levenson
 */
public class ConfigFileReader {
	// this.getClass().getClassLoader().getResource(fileName).getPath()
	
	public static ConfigFileReader instance;

	private static final String KEY_DELIMITER = ".";
	
	// This is incremented each time the config file is reloaded to ensure that ConfigValues use the most recent setting.
	private int m_generation = 0;
	
	private String m_fileName;
	private HashMap<String, Object> m_values = new HashMap<String, Object>();
	
	public static ConfigFileReader getInstance() {
		return instance;
	}
	
	public ConfigFileReader(String fileName) {
		m_fileName = fileName;
		
		try {
			reloadFromFile();
		} catch (Exception e) {
			System.err.println("Failed to load config file!");
			e.printStackTrace();
			LoggerExceptionUtils.logException(e);
		}
	}

	private static void loadJson(String keyPrefix, JSONObject json, HashMap<String, Object> values) {
		for (String key : json.keySet()) {
			if (json.isNull(key)) {
				continue;
			}
			JSONObject obj = json.optJSONObject(key);
			String fullKey = keyPrefix + key;
			if (obj == null) {
				values.put(fullKey, json.get(key));
			} else {
				loadJson(fullKey + KEY_DELIMITER, obj, values);
			}
		}
	}
	
	public void reloadFromFile() throws IOException {
		System.out.println("Loading config file: " + m_fileName);
		String jsonString = Files.readString(Paths.get(m_fileName));
		reloadFromJson(jsonString);
	}

	public void reloadFromJson(String jsonString) {
		HashMap<String, Object> newValues = new HashMap<String, Object>();
		
		JSONObject newJson;
		try (StringReader reader = new StringReader(jsonString)) {
			newJson = new JSONObject(new JSONTokener(reader));
			loadJson("", newJson, newValues);
		}
		for (Map.Entry<String, AbstractConfigValue<?>> param : AbstractConfigValue.accessedValues().entrySet()) {
			if (!newValues.containsKey(param.getKey())) {
				continue;
			}
			try {
				param.getValue().parseJsonValue(newValues.get(param.getKey()));
			} catch (Exception ex) {
				throw new ConfigValueParseException("Could not parse config value for " + param.getKey(), ex);
			}
		}
		m_values = newValues;
		++m_generation;
	}
	
	public int getGeneration() {
		return m_generation;
	}
	
	public boolean containsKey(String key) {
		return m_values.containsKey(key);
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
	
	Object getRawValue(String key){
		return m_values.get(key);
	}

	public String getJsonString() {
		HashMap<String, Object> values = new HashMap<String, Object>(m_values);
		for (String key : AbstractConfigValue.accessedValues().keySet()) {
			if (!values.containsKey(key)) {
				values.put(key, JSONObject.NULL);
			}
		}

		JSONObject root = new JSONObject();
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String[] keyParts = entry.getKey().split(Pattern.quote(KEY_DELIMITER));
			JSONObject obj = root;
			for (int i = 0; i < keyParts.length - 1; ++i) {
				JSONObject subObj = obj.optJSONObject(keyParts[i]);
				if (subObj == null) {
					subObj = new JSONObject();
					obj.put(keyParts[i], subObj);
				}
				obj = subObj;
			}
			obj.put(keyParts[keyParts.length - 1], entry.getValue());
		}

		StringWriter writer = new StringWriter();
		root.write(writer, 2, 0);
		return writer.toString(); 
	}
	
	public void saveFile(String jsonString) throws IOException {
		try(FileWriter writer = new FileWriter(m_fileName)) {
			writer.write(jsonString);
		}
		Logger.get(Category.CONFIGURATION).logRaw(Severity.INFO, "Config file written to " + m_fileName);
	}
}
