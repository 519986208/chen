package com.ahhf.chen.java8.nashorn;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Date;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.ahhf.chen.java8.lambda.Person;

public class Nashorn1 {

	public static void main(String[] args) throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval(new FileReader("res/nashorn1.js"));

		Invocable invocable = (Invocable) engine;
		Object result = invocable.invokeFunction("fun1", "Peter Parker");
		System.out.println(result);
		System.out.println(result.getClass());

		invocable.invokeFunction("fun2", new Date());
		invocable.invokeFunction("fun2", LocalDateTime.now());
		invocable.invokeFunction("fun2", new Person());
	}

}