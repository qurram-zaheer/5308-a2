package com.assignment1.main;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public abstract class God {
    public String string1;
    public String string2;

    public String concatenate(){
        return string1+string2;
    }

    public List<String[]> readInner1(String filename) throws IOException {
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

    public void writeListToCSV(List<String[]> resultList) throws IOException {
        throw new UnsupportedOperationException("Cannot write to CSV if you are reading JSON");
    }

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
    Connection instanceConn = null;
    String connString;
    String instanceDbName;
    CSVHandler csvHandler;

    public Properties readProps(String filename) {
        Properties props = new Properties();
        try {
            String configPath = filename;
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream is = classLoader.getResourceAsStream(configPath);
            props.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

    public boolean handlerMain(int cmdFlag, String inpConnString, Properties props, String databaseName, String inpName, String tableName, String[] headers, List<String[]> data, String query){
        if (cmdFlag == 0){
            System.out.println("Connection String: " + inpConnString);
            try {
                instanceConn = DriverManager.getConnection(inpConnString);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connString = inpConnString;
        } else if (cmdFlag == 1){
            connString = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", props.getProperty("host"), props.getProperty("port"), "", props.getProperty("user"), props.getProperty("password"));
        } else if (cmdFlag == 2) {
            instanceDbName = databaseName;
            connString = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", props.getProperty("host"), props.getProperty("port"), databaseName, props.getProperty("user"), props.getProperty("password"));
        } else if (cmdFlag == 3) {
            try {
                if (null != instanceConn) {
                    instanceConn.close();
                    instanceConn = null;
                    System.out.println("Success: Connection closed");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (cmdFlag == 4) {
            ResultSet rs;
            try {
                rs = instanceConn.getMetaData().getCatalogs();
                while (rs.next()) {
                    String foundName = rs.getString(1);
                    if (foundName.equals(databaseName.toLowerCase())) {
                        return true;
                    }
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } else if (cmdFlag == 5) {
            String delQuery = "DROP DATABASE " + databaseName;
            try {
                Statement stmt = instanceConn.createStatement();
                stmt.executeUpdate(delQuery);
                System.out.println("Warning: Existing database overwritten");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (cmdFlag == 6) {
            if (checkDatabaseExistence(inpName)) {
                dropDatabase(inpName);
            }

            try {
                Statement stmt = instanceConn.createStatement();
                String creationQuery = "CREATE DATABASE " + inpName;
                stmt.executeUpdate(creationQuery);
                instanceDbName = inpName;
                System.out.println("Success: Database creation");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (cmdFlag == 7) {
            try {
                DatabaseMetaData md = instanceConn.getMetaData();
                ResultSet rs = md.getTables(null, instanceDbName, null, new String[]{"TABLE"});
                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    if (name.equals(tableName.toLowerCase())) {
                        return true;
                    }
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } else if (cmdFlag == 8) {
            String deleteQuery = "DROP TABLE " + tableName;
            try {
                Statement stmt = instanceConn.createStatement();
                stmt.executeUpdate(deleteQuery);
                System.out.println("Warning: Existing table overwritten");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (cmdFlag == 9) {
            if (checkTableExistence(tableName)) {
                dropTable(tableName);
            }
            StringBuilder sb = new StringBuilder("CREATE TABLE " + instanceDbName + "." + tableName + " (" +
                    "id INTEGER NOT NULL AUTO_INCREMENT,");

            for (String header : headers) {
                sb.append(header).append(" varchar(40), ");
            }
            // Since MySQL usage has been mentioned under assumptions
            sb.append("PRIMARY KEY (id))");
            String innerQuery = sb.toString();
            try {
                Statement stmt = instanceConn.createStatement();
                stmt.executeUpdate(innerQuery);
                System.out.println("Success: Table " + tableName + " creation");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (cmdFlag == 10) {
            try {
                instanceConn.setAutoCommit(false);
                StringBuilder sql = new StringBuilder("INSERT INTO " + instanceDbName + "." + tableName + " VALUES ");
                StringJoiner sj = new StringJoiner(",", "(", ")");
                for (int i = 0; i < data.get(0).length + 1; i++) {
                    sj.add("?");
                }
                sql.append(sj);
                String templateString = sql.toString();

                PreparedStatement pstmt = instanceConn.prepareStatement(templateString);

                for (String[] record : data) {
                    pstmt.setString(1, null);
                    for (int i = 0; i < record.length; i++) {
                        pstmt.setString(i + 2, record[i]);
                    }
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                instanceConn.commit();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (cmdFlag == 11) {
            csvHandler = new CSVHandler();
            List<String[]> resultList = null;
            try {
                resultList = queryExecution(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                assert resultList != null;
                csvHandler.writeListToCSV(resultList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void getConnection(String inpConnString) throws SQLException {
        System.out.println("Connection String: " + inpConnString);
        instanceConn = DriverManager.getConnection(inpConnString);
        connString = inpConnString;
    }

    public void generateConnString(Properties props) {
        generateConnString(props, "");
    }

    public void generateConnString(Properties props, String databasename) {
        instanceDbName = databasename;
        connString = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", props.getProperty("host"), props.getProperty("port"), databasename, props.getProperty("user"), props.getProperty("password"));
    }


    public void closeConnection() {
        try {
            if (null != instanceConn) {
                instanceConn.close();
                instanceConn = null;
                System.out.println("Success: Connection closed");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean checkDatabaseExistence(String dbName) {
        ResultSet rs;
        try {
            rs = instanceConn.getMetaData().getCatalogs();
            while (rs.next()) {
                String foundName = rs.getString(1);
                if (foundName.equals(dbName.toLowerCase())) {
                    return true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dropDatabase(String dbName) {
        String query = "DROP DATABASE " + dbName;
        try {
            Statement stmt = instanceConn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Warning: Existing database overwritten");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createDatabase(String inpName) {
        if (checkDatabaseExistence(inpName)) {
            dropDatabase(inpName);
        }

        try {
            Statement stmt = instanceConn.createStatement();
            String creationQuery = "CREATE DATABASE " + inpName;
            stmt.executeUpdate(creationQuery);
            instanceDbName = inpName;
            System.out.println("Success: Database creation");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkTableExistence(String tableName) {
        try {
            DatabaseMetaData md = instanceConn.getMetaData();
            ResultSet rs = md.getTables(null, instanceDbName, null, new String[]{"TABLE"});
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                if (name.equals(tableName.toLowerCase())) {
                    return true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dropTable(String tableName) {
        String query = "DROP TABLE " + tableName;
        try {
            Statement stmt = instanceConn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Warning: Existing table overwritten");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName, String[] headers) {
        if (checkTableExistence(tableName)) {
            dropTable(tableName);
        }
        StringBuilder sb = new StringBuilder("CREATE TABLE " + instanceDbName + "." + tableName + " (" +
                "id INTEGER NOT NULL AUTO_INCREMENT,");

        for (String header : headers) {
            sb.append(header).append(" varchar(40), ");
        }
        // Since MySQL usage has been mentioned under assumptions
        sb.append("PRIMARY KEY (id))");
        String query = sb.toString();
        try {
            Statement stmt = instanceConn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Success: Table " + tableName + " creation");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void populateTable(String tableName, List<String[]> data) {
        try {
            instanceConn.setAutoCommit(false);
            StringBuilder sql = new StringBuilder("INSERT INTO " + instanceDbName + "." + tableName + " VALUES ");
            StringJoiner sj = new StringJoiner(",", "(", ")");
            for (int i = 0; i < data.get(0).length + 1; i++) {
                sj.add("?");
            }
            sql.append(sj);
            String templateString = sql.toString();

            PreparedStatement pstmt = instanceConn.prepareStatement(templateString);

            for (String[] record : data) {
                pstmt.setString(1, null);
                for (int i = 0; i < record.length; i++) {
                    pstmt.setString(i + 2, record[i]);
                }
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            instanceConn.commit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<String[]> queryExecution(String query) throws SQLException {
        List<String[]> resultList = new ArrayList<>();

        Statement stmt = instanceConn.createStatement();
        boolean isResultSet = stmt.execute(query);
        if (isResultSet) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] row = new String[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = metaData.getColumnName(i);
            }
            resultList.add(row);

            while (rs.next()) {
                String[] temp = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    temp[i - 1] = rs.getString(i);
                }
                resultList.add(temp);
                temp = null;
            }
        }
        return resultList;
    }

    public void queryDriver(String query) throws SQLException, IOException {
        csvHandler = new CSVHandler();
        List<String[]> resultList = queryExecution(query);
        csvHandler.writeListToCSV(resultList);
    }
}
