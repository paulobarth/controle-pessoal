package com.cp.load;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.cp.fwk.util.GeneralFunctions;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LoadCSV {

    public static JsonArray readFileToJson(String file, String separator) {

        // create an array called datasets
        JsonArray datasets = new JsonArray();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(file), "ISO-8859-1")))  {
            String line;
            boolean flag = true; 
            List<String> columns = null; 
            while ((line = br.readLine()) != null) {
               if (flag) {
                   flag = false; 
                   //process header 
                   columns = Arrays.asList(line.split(separator));
               } else {

                   //to store the object temporarily
                   JsonObject obj = new JsonObject(); 
                   List<String> chunks = Arrays.asList(line.split(separator));

                   for(int i = 0; i < columns.size(); i++) {
                       obj.addProperty(columns.get(i), chunks.get(i));
                   }
                   datasets.add(obj); 
               } 
            }
        } catch(FileNotFoundException fnfe) {
            System.out.println("File not found.");
        } catch(IOException io) {
            System.out.println("Cannot read file.");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        GeneralFunctions.showLog(gson.toJson(datasets));
        
        return datasets;
    }
}
