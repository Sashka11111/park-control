package com.parkcontrol.persistence.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConnection {

  private static final String JDBC_URL = "jdbc:sqlite:db/ParkControl.sqlite";
  private static DatabaseConnection instance;
  private static HikariDataSource dataSource;

  public DatabaseConnection() {
  }

  public static synchronized DatabaseConnection getInstance() {
    if (instance == null) {
      instance = new DatabaseConnection();
    }
    return instance;
  }


  public static void initializeDataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(JDBC_URL);
    dataSource = new HikariDataSource(config);
  }

  public DataSource getDataSource() {
    if (dataSource == null) {
      initializeDataSource(); // Ініціалізація джерела даних
    }
    return dataSource;
  }

  public void closePool() {
    if (dataSource != null) {
      dataSource.close();
    }
  }
}