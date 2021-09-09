import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = new FileInputStream(new File("config.json"));
        ConfigF configF = objectMapper.readValue(new String(inputStream.readAllBytes()), ConfigF.class);

        long startTime = System.nanoTime();

        downloadToBase(configF.destination, rowList(configF), 1000);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("\nTime: " + duration * 0.000000001);
    }

    public static Connection connection(DBConfig dbConfig) throws SQLException {
        return DriverManager.getConnection(dbConfig.geturl(),
                dbConfig.getUser(),
                dbConfig.getPassword());
    }

    public static void tablGener(ConfigF configF, long data) throws SQLException {
        Statement statementSource = connection(configF.source).createStatement();
        for (int i = 1; i < data; i++) {
            if (i % 1000 == 0) System.out.println(i);
            String randStrName = RandomStringUtils.randomAlphanumeric(10);
            String randStrProff = RandomStringUtils.randomAlphanumeric(10);
            String sqlInsert = String.format("INSERT INTO %s(id,name,profession) VALUES (%s,'%s','%s');", configF.getSource().getTable(), i, randStrName, randStrProff);
            statementSource.executeUpdate(sqlInsert);
        }
    }

    public static List<Row> rowList(ConfigF configF) throws SQLException {
        Statement statementSource = connection(configF.source).createStatement();
        ResultSet resultSetSource = statementSource.executeQuery("SELECT * FROM " + configF.getSource().getTable());
        List<Row> tableSecond = new ArrayList<>();
        while (resultSetSource.next()) {
            Row row = new Row();
            row.setId(resultSetSource.getInt("id"));
            row.setName(resultSetSource.getString("name"));
            row.setProfession(resultSetSource.getString("profession"));
            tableSecond.add(row);
        }
        return tableSecond;
    }

    public static void downloadToBase(DBConfig dbconf, List<Row> rowList, int batch) throws SQLException {
        Statement statementDest = connection(dbconf).createStatement();
        for (int n = -1; n < rowList.size(); n = n + batch) {
            String packet = "";
            for (int j = n + 1; j < n + batch; j++) {
                packet += rowList.get(j).asString() + ",";
            }
            String sqlDownload = String.format("INSERT INTO %s(id,name,profession) VALUES %s;",
                    dbconf.getTable(),
                    packet.substring(0, packet.length() - 1));
            statementDest.executeUpdate(sqlDownload);
        }
    }

    public static void downloadToBaseNew(ConfigF configF, List<Row> rowList, int batch) throws SQLException {
        Statement statementDest = connection(configF.destination).createStatement();
        int counter = 0;
        String query = "";
        for (Row row : rowList) {
            counter += 1;
        }
    }

    public static List<String> rowListNew(List<Row> list) {
        List<String> rowListNew = new ArrayList<>();
        for (Row row : list) {
            rowListNew.add(row.asString());
        }
        return rowListNew;
    }
}
