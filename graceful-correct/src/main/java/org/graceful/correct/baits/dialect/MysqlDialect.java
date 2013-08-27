package org.graceful.correct.baits.dialect;

public class MysqlDialect implements SqlDialect {

	public String getLimitSql(String query, int offset, int limit) {
		return query + "limit "+offset+","+limit;
	}

}
