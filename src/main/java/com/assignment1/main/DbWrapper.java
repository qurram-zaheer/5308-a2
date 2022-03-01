package com.assignment1.main;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DbWrapper {

    public static Properties getConfig(ConfigHandler handler) {
        return handler.readData("config.properties");
    }

    public static List<String[]> fileHandler(String filename) {
        CSVHandler handler = new CSVHandler();
        List<String[]> data = null;
        try {
            data = handler.readInner(filename + ".csv");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        assert data != null;
        for (String[] record : data) {
            System.out.println(Arrays.toString(record));
        }
        return data;
    }

    public static void databaseOps(String filename, Properties props, DatabaseHandler db, List<String[]> data) {
        String[] headers = data.get(0);
        data.remove(0);

        db.createDatabase(props.getProperty("databasename"));
        db.createTable(filename, headers);
        db.populateTable(filename, data);
    }

    public static void init(int flag, String filename, Properties props, DatabaseHandler db, ConfigHandler handler, List<String[]> data, String queryString) {
        Properties loginProps = handler.readData("config.properties");
        if (flag == 0) {
            CSVHandler CsvHandler = new CSVHandler();
            MyReader myReader = new MyReader();
            myReader.readData(filename + ".csv");
            data = myReader.dataCsv;
            assert data != null;
            for (String[] record : data) {
                System.out.println(Arrays.toString(record));
            }
            String[] headers = data.get(0);
            data.remove(0);

            db.createDatabase(props.getProperty("databasename"));
            db.createTable(filename, headers);
            db.populateTable(filename, data);
        } else if (flag == 1) {
            try {
                System.out.println(queryString);
                db.queryDriver(queryString);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws SQLException {
        ConfigHandler handler = new ConfigHandler();
        Properties properties = getConfig(handler);

        DatabaseHandler db = new DatabaseHandler();
        String connStr;

        String command = args[0];
        switch (command) {
            case "init" -> {
                db.generateConnString(properties);
                connStr = db.connString;
                db.getConnection(connStr);
                List<String[]> data = new ArrayList<>();
                init(0, args[1], properties, db, handler, data, "");
            }
            case "query" -> {
                db.generateConnString(properties, args[1]);
                connStr = db.connString;
                db.getConnection(connStr);
                init(1, "", properties, db, handler, null, args[2]);
            }
            default -> {
                System.out.println("Please enter appropriate command");
                System.exit(0);
            }
        }
    }




}
