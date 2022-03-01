package com.assignment1.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CSVHandler extends MyReader {

    public String something;
    public List<String[]> readInner(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + filename);
        }

        return readIntoList(inputStream);
    }

    private static List<String[]> readIntoList(InputStream is) throws IOException {
        List<String[]> data = new ArrayList<>();
        InputStreamReader streamReader =
                new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(""))
                continue;

            String[] lineArr = line.strip().split(",");

            for (int j = 0; j < lineArr.length; j++) {
                lineArr[j] = lineArr[j].strip();
            }
            data.add(lineArr);
        }
        if (data.size() <= 1) {
            throw new IndexOutOfBoundsException("Input CSV needs at least one header row and one data row");
        }
        for (String headerRecord : data.get(0)) {
            if (headerRecord.equals(""))
                throw new IOException("Blank headers not allowed in CSV");
        }
        return data;
    }

    public void writeTableToCsv(String tableName) {
        String query = "SELECT * from " + tableName;
        DatabaseHandler db = new DatabaseHandler();
        ConfigHandler configHandler = new ConfigHandler();
        Properties props = DbWrapper.getConfig(configHandler);
        db.generateConnString(props, props.getProperty("databasename"));
        String connStr = db.connString;
        try {
            db.getConnection(connStr);
            db.queryDriver(query);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
