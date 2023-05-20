package com.iris.tfAmadeus.logic;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

public class ScriptTemplate {
    ArrayList<String> modelNames;
    HashMap<String,Integer> modelSteps;
    HashMap<String, Integer> modelInputFunction;
    HashMap<String, Integer> modelEvalFunction;
    ArrayList<String> inputFunctions;
    ArrayList<String> evalFunctions;

    public ScriptTemplate(){
        this.modelNames = new ArrayList<>();
        this.inputFunctions = new ArrayList<>();
        this.evalFunctions = new ArrayList<>();
    }

    public ArrayList<String> getModelNames() {
        return modelNames;
    }

    public ArrayList<String> getInputFunctions() {
        return inputFunctions;
    }

    public ArrayList<String> getEvalFunctions() {
        return evalFunctions;
    }

    public HashMap<String, Integer> getModelSteps() {
        return modelSteps;
    }

    public HashMap<String, Integer> getModelInputFunction() {
        return modelInputFunction;
    }

    public HashMap<String, Integer> getModelEvalFunction() {
        return modelEvalFunction;
    }
}
