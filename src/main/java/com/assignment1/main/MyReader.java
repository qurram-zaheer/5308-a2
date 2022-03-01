package com.assignment1.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class MyReader {
    public List<String[]> dataCsv;
    public Map<?,?> dataJson;
    public void writeListToCSV(List<String[]> resultList) throws IOException {
        FileWriter file = new FileWriter("results.csv");
        PrintWriter write = new PrintWriter(file);
        StringBuilder sb = new StringBuilder();
        for (String[] record : resultList) {
            StringJoiner sj = new StringJoiner(",");
            for (String r : record) {
                sj.add(r);
            }
            sb.append(sj.toString()).append("\n");
        }
        write.write(sb.toString());
        write.close();
        file.close();
    }

    public void readData(String fileName) {
        if (fileName.split("\\.")[1].equals("csv")){
            try {
                CSVHandler csvHandler = new CSVHandler();
                dataCsv = new ArrayList<>();
                dataCsv = csvHandler.readInner(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JsonHandler jsonHandler = new JsonHandler();
            try {
                dataJson = jsonHandler.readInner(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
