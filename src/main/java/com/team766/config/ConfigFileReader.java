package com.team766.config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private JSONObject m_values = new JSONObject();
	
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
			LoggerExceptionUtils.logException(new IOException("Failed to load config file!", e));
		}
	}
	
	public void reloadFromFile() throws IOException {
		System.out.println("Loading config file: " + m_fileName);
		String jsonString = Files.readString(Paths.get(m_fileName));
		reloadFromJson(jsonString);
	}

	public void reloadFromJson(String jsonString) {
		JSONObject newValues;
		try (StringReader reader = new StringReader(jsonString)) {
			newValues = new JSONObject(new JSONTokener(reader));
		}
		for (AbstractConfigValue<?> param : AbstractConfigValue.accessedValues()) {
			var rawValue = getRawValue(newValues, param.getKey());
			if (rawValue == null) {
				continue;
			}
			try {
				param.parseJsonValue(rawValue);
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
		return getRawValue(key) != null;
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

	public <E extends Enum<E>> ValueProvider<E> getEnum(Class<E> enumClass, String key) {
		return new EnumConfigValue<E>(enumClass, key);
	}
	
	Object getRawValue(String key) {
		return getRawValue(m_values, key);
	}

	private static Object getRawValue(JSONObject obj, String key) {
		String[] keyParts = key.split(Pattern.quote(KEY_DELIMITER));
		for (int i = 0; i < keyParts.length - 1; ++i) {
			JSONObject subObj;
			try {
				subObj = (JSONObject)obj.opt(keyParts[i]);
			} catch (ClassCastException ex) {
				throw new IllegalArgumentException(
					"The config file cannot store both a single config " + 
					"setting and a group of config settings with the name " +
					key + " Please pick a different name for one of them.");
			}
			if (subObj == null) {
				subObj = new JSONObject();
				obj.put(keyParts[i], subObj);
			}
			obj = subObj;
		}
		var rawValue = obj.opt(keyParts[keyParts.length - 1]);
		if (rawValue instanceof JSONObject) {
			throw new IllegalArgumentException(
				"The config file cannot store both a single config " + 
				"setting and a group of config settings with the name " +
				key + " Please pick a different name");
		}
		if (rawValue == null) {
			obj.put(keyParts[keyParts.length - 1], JSONObject.NULL);
		}
		if (rawValue == JSONObject.NULL) {
			rawValue = null;
		}
		return rawValue;
	}

	public String getJsonString() {
		return m_values.toString(2);
	}
	
	public void saveFile(String jsonString) throws IOException {
		try(FileWriter writer = new FileWriter(m_fileName)) {
			writer.write(jsonString);
		}
		Logger.get(Category.CONFIGURATION).logRaw(Severity.INFO, "Config file written to " + m_fileName);
	}
}
