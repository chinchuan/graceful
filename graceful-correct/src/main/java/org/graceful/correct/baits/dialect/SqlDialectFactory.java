package org.graceful.correct.baits.dialect;

public class SqlDialectFactory {

	/**
	 * 获取 SqlDialect
	 * 
	 * @param driver
	 * @return
	 */
	public static SqlDialect getDialect(String driver) {
		SqlDialect dialect = null;
		if (driver.equals("SQLSERVER"))
			dialect = new SqlServerDialect();
		else if (driver.equals("ORACLE"))
			dialect= new OracleDialect();
		else
			throw new RuntimeException(" no driver");
		return dialect;
	}
}
