package org.graceful.correct.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {

	private static final long serialVersionUID = -1511139613256197444L;
	private static final String ASC = "asc";
	private static final String DESC = "desc";

	private String[] desc;
	private String[] asc;
	
	private String sort = DESC;
	private String field;
	
	private Map<String, String[]> orderByMap = null;
	private List<OrderBy> orderByProperty = null; //mybatis 接收orderby
	private String orderByClause;

	public String[] getDesc() {
		return desc;
	}

	public void setDesc(String... desc) {
		this.desc = desc;
	}

	public String[] getAsc() {
		return asc;
	}

	public void setAsc(String... asc) {
		this.asc = asc;
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Map<String, String[]> getOrderByMap() {
		
		if (orderByMap != null) {
			return orderByMap;
		}
		
		if (desc != null || asc != null) {
			orderByMap = new HashMap<String, String[]>();
			orderByMap.put(DESC, desc);
			orderByMap.put(ASC, asc);
		}
		return null;
	}
	
	
	/**
	 * 返回排序字符串
	 * @return
	 */
	public String getOrderByClause() {
		
		if(this.orderByClause!=null && !this.orderByClause.isEmpty()) {
			return this.orderByClause;
		}
		
		//sort , field 优先
		if(sort!=null && !sort.isEmpty() && this.field!=null && !this.field.isEmpty()) {
			return " order by "+this.field +"  "+this.sort;
		}
		if( desc != null || asc != null) {
			StringBuilder sb = new StringBuilder(" order by ");
			if(desc!=null && desc.length >0) {
				for(String e : desc) {
					sb.append(e).append(",");
				}
				sb.deleteCharAt(sb.length()-1).append(" ").append(DESC);
			}
			if(asc!=null && asc.length > 0) {
				for(String e : asc) {
					sb.append(e).append(",");
				}
				sb.deleteCharAt(sb.length()-1).append(" ").append(ASC);
			}
			
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * 获取按 java bean 属性名称排序 
	 * <p> sql 语句中要转换成 数据库中字段名
	 * @return
	 */
	public List<OrderBy> getOrderByProperty() {
		
		if(this.orderByProperty!=null) {
			return this.orderByProperty;
		}
		
		OrderBy by;
		//sort , field 优先
		if(sort!=null && !sort.isEmpty() && this.field!=null && !this.field.isEmpty()) {
			this.orderByProperty = new ArrayList<OrderBy>(1);
			this.orderByProperty.add(new OrderBy(this.sort.equalsIgnoreCase(ASC)? ASC : DESC,this.field));
			return this.orderByProperty;
		}
		
		if(desc != null || asc != null) {
			this.orderByProperty = new ArrayList<OrderBy>(2);
			if(desc!=null && desc.length >0) {
				by = new OrderBy(DESC,this.desc);
				this.orderByProperty.add(by);
			}
			
			if(this.asc!=null && asc.length > 0) {
				by = new OrderBy(ASC,this.asc);
				this.orderByProperty.add(by);
			}
			
			return this.orderByProperty;
			
		}
		return null;
	}
	
	
	public  final static class OrderBy {
		private String sort;
		private String[] fields;
		
		
		public OrderBy(String sort, String... fields) {
			this.sort = sort;
			this.fields = fields;
		}
		
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public String[] getFields() {
			return fields;
		}
		public void setFields(String[] fields) {
			this.fields = fields;
		}
		
	}
}
