package com.ahhf.chen.java8.nashorn;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Nashorn3 {

	public static void main(String[] args) throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval("load('res/nashorn3.js')");
	}

}