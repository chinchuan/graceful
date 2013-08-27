package org.graceful.correct.baits;

import java.sql.Connection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;
import org.graceful.correct.baits.dialect.SqlDialect;
import org.graceful.correct.baits.dialect.SqlDialectFactory;



/**
 * mybaits 分页拦截
 * @author jmac
 *
 */
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class})})
public class StatementInterceptor implements Interceptor {

	private static final Log log = LogFactory.getLog(StatementInterceptor.class);
	private  Properties properties;
	
 
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		
		MetaObject meta = MetaObject.forObject(statementHandler);
		 
		RowBounds rowBounds = (RowBounds)meta.getValue("delegate.rowBounds"); //获取分页信息
		//不存在分页时直接返回
		if(rowBounds==null  || rowBounds == RowBounds.DEFAULT
				|| rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT)
			return invocation.proceed();
		
	    //获取数据库驱动
		//Connection connection = (Connection) invocation.getArgs()[0];  
	 
		String driver=properties.getProperty("dialect").toUpperCase(); //获取数据库驱动
	    String sql = boundSql.getSql(); //获取sql语句
	    
	    //生成分页语句
	    SqlDialect dialect = SqlDialectFactory.getDialect(driver);
	    sql = dialect.getLimitSql(sql, rowBounds.getOffset(), rowBounds.getLimit());
	   
		meta.setValue("delegate.boundSql.sql", sql);//重新设置值
		//去掉游标
		meta.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
		meta.setValue("delegate.rowBounds.limit",RowBounds.NO_ROW_LIMIT);
		if(log.isDebugEnabled())
			log.debug("===>"+sql);
		
		return invocation.proceed();
	}

	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties arg0) {
		 this.properties = arg0;
	}

}
