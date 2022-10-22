package com.team766.config;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class ConfigFileReaderTest {
	@Before
	public void setup() {
		AbstractConfigValue.resetStatics();
	}

	@Test
	public void getJsonStringFromEmptyConfigFile() throws IOException {
		File testConfigFile = File.createTempFile("config_file_test", ".json");
		try (FileWriter fos = new FileWriter(testConfigFile)) {
			fos.append("{}");
		}

		ConfigFileReader.instance = new ConfigFileReader(testConfigFile.getPath());
		ConfigFileReader.getInstance().getString("test.sub.key");
		assertEquals("{\"test\": {\"sub\": {\"key\": null}}}", ConfigFileReader.getInstance().getJsonString());
	}

	@Test
	public void getJsonStringFromPartialConfigFile() throws IOException {
		File testConfigFile = File.createTempFile("config_file_test", ".json");
		try (FileWriter fos = new FileWriter(testConfigFile)) {
			fos.append("{\"test\": {\"sub\": {\"key\": \"pi\", \"value\": 3.14159}}}");
		}

		ConfigFileReader.instance = new ConfigFileReader(testConfigFile.getPath());
		assertEquals("pi", ConfigFileReader.getInstance().getString("test.sub.key").get());
		assertEquals(3.14159, ConfigFileReader.getInstance().getDouble("test.sub.value").get().doubleValue(), 1e-6);
		assertFalse(ConfigFileReader.getInstance().getInts("test.other.value").hasValue());
		assertEquals(
				"{\"test\": {\n  \"sub\": {\n    \"value\": 3.14159,\n    \"key\": \"pi\"\n  },\n  \"other\": {\"value\": null}\n}}",
				ConfigFileReader.getInstance().getJsonString());
	}
}