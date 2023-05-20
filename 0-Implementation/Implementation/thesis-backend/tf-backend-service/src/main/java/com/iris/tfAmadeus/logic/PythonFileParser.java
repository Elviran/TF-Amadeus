package com.iris.tfAmadeus.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

public class PythonFileParser {

    public ScriptTemplate ParseFile(Path filepath) throws Exception{
        ScriptTemplate template = parseFunctionNames(filepath);
        template.modelSteps = parsePythonDictionary(filepath, "steps");
//        template.modelEvalFunction = parsePythonDictionary(filepath, "modelEval");
//        template.modelInputFunction = parsePythonDictionary(filepath, "modelInput");
        return template;
    }

    public ScriptTemplate parseFunctionNames(Path filepath) throws Exception {

        File file = new File(String.valueOf(filepath));

        if(file == null)
            throw new Exception("File cannot be null");

        ScriptTemplate template = new ScriptTemplate();

        try{

            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                parse_line(data, template);
            }

            myReader.close();
            return template;

        }catch (IOException ex){
            throw new Exception("Error reading file");
        }catch (Exception ex){
            throw new Exception("Error parsing file");
        }
    }

    //TODO: Later on just parse the file and retrieve all the dictionaries..
    private HashMap<String, Integer> parsePythonDictionary(Path filepath, String variableName) throws Exception{

        File file = new File(String.valueOf(filepath));
        HashMap<String,Integer> map = new HashMap<>();

        if(file == null)
            throw new Exception("File cannot be null");

        try{
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.contains(variableName)){
                    data = myReader.nextLine();
                    while(!data.contains("})")){
                        String[] kv = data.split(":");
                        String modelName = kv[0].substring(kv[0].indexOf('\'') + 1, kv[0].lastIndexOf('\''));
                        if(kv[1].contains(","))
                            map.put(modelName, Integer.valueOf(kv[1].substring(0, kv[1].lastIndexOf(',')).trim()));
                        else
                            map.put(modelName, Integer.valueOf(kv[1].trim()));

                        data = myReader.nextLine();
                    }
                    break;
                }
            }

            myReader.close();
            return map;

        }catch (Exception ex){
            throw new Exception("Error reading file or parsing file..");
        }
    }

    private static void parse_line(String line, ScriptTemplate template){

        if(line.contains("def")){
            if(line.contains("model")){
                line = line.trim().substring(line.indexOf("d"), line.indexOf("("));
                template.getModelNames().add(line.substring(line.indexOf(" ")).trim());
            }else if (line.contains("input_fn")){
                line = line.trim().substring(line.indexOf("d"), line.indexOf("("));
                template.getInputFunctions().add(line.substring(line.indexOf(" ")).trim());
            }else if(line.contains("eval_fn")){
                line = line.trim().substring(line.indexOf("d"), line.indexOf("("));
                template.getEvalFunctions().add(line.substring(line.indexOf(" ")).trim());
            }
        }


    }
}
