package com.team766.web;

class HtmlElements {
	public static String buildDropDown(String valueName, String current, String... options){
		String id = valueName.replace(' ', '_');
		String out = "<select name=\"" + id + "\">";
		
		for(String s : options){
			if(s.equals(current))
				out += "<option value=\"" + s + "\" selected >" + s + "</option>";
			else
				out += "<option value=\"" + s + "\">" + s + "</option>";
		}
				
		return out + "</select>";
	}
	
	public static String buildForm(String valueName, double v){
		return buildForm(valueName, Double.toString(v));
	}
	
	public static String buildForm(String valueName, String v){
		String id = valueName.replace(' ', '_');
		return "<label for=\"" + id.toUpperCase() + "\">" + valueName + ":</label>"
				+ "<input name=\"" + id.toLowerCase() + "\" id=\"" + id.toUpperCase() + "\" type=\"text\" value=\"" + v + "\"/>";
	}
	
	public static String buildForm(String valueName){
		return buildForm(valueName, 0.0);
	}
}
