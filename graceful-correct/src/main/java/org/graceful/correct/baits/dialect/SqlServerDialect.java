package org.graceful.correct.baits.dialect;

public class SqlServerDialect implements SqlDialect {
	
	/**
	 * 生成分页查询语句 
	 * @param querySelect
	 * @param offset
	 * @param limit
	 * @return
	 */
	private  String getLimitStringV2(String querySelect, int offset, int limit ){
		String sqlWithOutSelect = querySelect.substring(6);//去掉 select  的语句
		//获取top 值 
		long topNum = offset + limit;
		
		//生成查询sql
		//原型
		//select * from (select row_number() over (order by tempRow) as rn,* from 
		// (select 0 as tempRow,* from table order by ...) temp )temp1 where temp1.rn>offset
		//
		StringBuffer sql = new StringBuffer(querySelect.length()+100);
		String querySql = "select top "+topNum+" 0 as _t_,"+sqlWithOutSelect; //拼接查询sql
		sql.append("select * from ( select row_number() over (order by _t_) _tn_,* from (")
		.append(querySql)
		.append(") temp1 ) temp2 where temp2._tn_>")
		.append(offset);
		return sql.toString();
	}

	public String getLimitSql(String query, int offset, int limit) {
		return this.getLimitStringV2(query, offset, limit);
	}
	
}
