package tech.zoomidsoon.pickme_restful_api.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBContext {
	private static final String hostname = Utils.getEnv("DB_HOSTNAME", "localhost");
	private static final int port = Integer.parseInt(Utils.getEnv("DB_PORT", "3306"));
	private static final String dbName = Utils.getEnv("DB_NAME", "DATABASE");
	private static final String username = Utils.getEnv("DB_USERNAME", "root");
	private static final String password = System.getenv("DB_PASSWORD");
	private static final String driver = "com.mysql.cj.jdbc.Driver";

	private static final String connectionStr = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName;

	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource dataSource;

	static {
		config.setJdbcUrl(connectionStr);
		config.setUsername(username);
		config.setPassword(password);
		config.setDriverClassName(driver);
		config.setConnectionTimeout(3000);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}