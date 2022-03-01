package com.assignment1.main;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JsonHandler extends MyReader{
    public String something;
    public Map<?, ?> readInner(String filename) throws IOException {
        Map<?, ?> map = null;
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("user.json"));

            // convert JSON file to map
            map = gson.fromJson(reader, Map.class);

            // print map entries
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }

            // close reader
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    @Override
    public void writeListToCSV(List<String[]> resultList) throws IOException {
        throw new UnsupportedOperationException("Cannot write to CSV if you are reading JSON");
    }
}
