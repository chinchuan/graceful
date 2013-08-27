package org.graceful.correct.baits;
import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.graceful.correct.core.Filter;
import org.graceful.correct.core.Order;
import org.graceful.correct.core.Page;


public interface StandardMapper<T extends Serializable> {

	/**
	 * 按关键字删除
	 * @param id
	 * @return
	 */
	<Key extends Serializable> int  deleteByPrimaryKey(Key id);
	
	/**
	 * 新增
	 * @param record
	 * @return
	 */
	int insert(T record);
	
	/**
	 * 更新
	 * @param t  要更新的参数
	 * @return
	 */
	 int updateByPrimaryKey(T t);
	
	/**
	 * 带判断更新
	 * @param t
	 * @return
	 */
	int updateByPrimaryKeySelective(T t);
	
	/**
	 * 按关键字查询
	 * @param id
	 * @return
	 */
	<Key extends Serializable,Result extends Serializable> Result selectByPrimaryKey(Key id);
	
	/**
	 * 分页查询
	 * @param t
	 * @param bounds
	 * @return
	 */
	<Result extends Serializable>  List<Result> query(@Param("filter") Filter filter,RowBounds bounds);
	
	/**
	 * 分页查询 （mybatis 插件机制分页）
	 * @param <Result>  
	 * @param filter    查询条件
	 * @param order     排序
	 * @param bounds    分页信息
	 * @return
	 */
	<Result extends Serializable>  List<Result> query(@Param("filter") Filter filter,@Param("order") Order order,RowBounds bounds);
	
	/**
	 * 分页查询  (sqlMap 标准分页)
	 * @param filter  查询条件
	 * @param order   排序
	 * @param page    分页信息
	 * @return
	 */
	<Result extends Serializable>  List<Result> query(@Param("filter") Filter filter,@Param("order") Order order,@Param("page") Page<Result> page);
	
	/**
	 * 无分页
	 * @param filter
	 * @return
	 */
	<Result extends Serializable>  List<Result> query(@Param("filter") Filter filter);
	
	/**
	 * 无分页
	 * @param filter
	 * @param order  排序
	 * @return
	 */
	<Result extends Serializable>  List<Result> query(@Param("filter") Filter filter,@Param("order") Order order);
	
	/**
	 * 获取查询总数
	 * @param filter
	 * @return
	 */
	Long getQueryCount(Filter filter);
	
}
