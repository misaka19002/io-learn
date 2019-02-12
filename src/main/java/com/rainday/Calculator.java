package com.rainday;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by wyd on 2019/2/12 13:24:09.
 * 调用javascript引擎，直接计算公式，并返回结果。
 */
public final class Calculator {
    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    
    public static Object cal(String expression) {
        try {
            return jse.eval(expression);
        } catch (ScriptException e) {
            return "";
        }
    }
}
