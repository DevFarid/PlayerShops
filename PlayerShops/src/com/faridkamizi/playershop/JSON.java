package com.faridkamizi.playershop;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.fusesource.jansi.Ansi;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSON
{
	public static String pluginFolder;
	
	
	public void sendDataFolder(String s)
	{
		pluginFolder = s;
		System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString() + "Data Folder Received as " + s + Ansi.ansi().reset());
	}
	
	/* Here we implement a method to write json */
	@SuppressWarnings("unchecked")
	public void writeJSON(String subPath, String fileName, String object, String value)
	{
		JSONObject main = new JSONObject();
		
		main.put(object, value);
		
		try {
			File file = new File(pluginFolder + File.separator + subPath + File.separator + fileName + ".json");
			File filePath = new File(pluginFolder + File.separator + subPath);
			
				if(!filePath.exists())
				{
					filePath.mkdirs();
				}
			
				if(!file.exists()) {
					file.createNewFile();
				}
				
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(main.toJSONString());
			fileWriter.flush();
			fileWriter.close();
			
		} catch (Exception e) { System.out.print(Ansi.ansi().fg(Ansi.Color.RED).bold().toString() + "Errors from writing JSON >>" + Ansi.ansi().reset()); e.printStackTrace(); }
	}

	/* Here we implement a method to read json objects */
	public String readJSON(String subPath, String fileName, String object)
	{
		String var = null;
		try {
			JSONParser parser = new JSONParser();
			
			
			File file = new File(pluginFolder  + File.separator + subPath + File.separator + fileName + ".json");
			Object obj = parser.parse(new FileReader(file));
			
			JSONObject jsonObject = (JSONObject) obj;
			var = jsonObject.get(object).toString();
			
		} catch (Exception e) { System.out.print(Ansi.ansi().fg(Ansi.Color.RED).bold().toString() + "Errors from reading JSON >>" + Ansi.ansi().reset()); e.printStackTrace(); }
		return var;
	}
}
