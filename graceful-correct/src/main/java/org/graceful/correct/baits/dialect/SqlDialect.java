package org.graceful.correct.baits.dialect;

public interface SqlDialect {
	/**
	 * 获取分页语句sql
	 * @param query  
	 * @param offset
	 * @param limit
	 * @return
	 * 
	 */
	String getLimitSql(String query,int offset, int limit);
}
