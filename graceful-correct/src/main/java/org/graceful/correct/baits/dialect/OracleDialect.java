package org.graceful.correct.baits.dialect;

public class OracleDialect implements SqlDialect {

	public String getLimitSql(String query, int offset, int limit) {
		//生成分页语句
		StringBuilder sql  = new StringBuilder(query.length() + 100);
		sql.append("select * from ( select A.*,rownum rn from (")
		.append(query)
		.append(" ) A where rownum<=").append(offset + limit-1)
		.append(") B where B.rn >= ")
		.append(offset);
		 
		return sql.toString();
	}

}
