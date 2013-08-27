package org.graceful.correct.baits.generator;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.TableConfiguration;

public abstract class GracefulGeneratePluginAdapter extends PluginAdapter {

	private final static String SQL_NODE = "sql";
	
	public boolean validate(List<String> warnings) {
		return true;
	}
	
    //mapper 接口
    @Override
    public boolean clientGenerated(Interface interfaze,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
    
    	interfaze.getMethods().clear();
    	
    	interfaze.addImportedType(new FullyQualifiedJavaType("org.graceful.correct.baits.StandardMapper"));
    	interfaze.addSuperInterface(new FullyQualifiedJavaType("org.graceful.correct.baits.StandardMapper<"+introspectedTable.getBaseRecordType()+">"));
    	
        return true;
    }
    
    /**
     * 生成 分页查询sql
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document,IntrospectedTable introspectedTable) {
       
    	XmlElement root = document.getRootElement();
        
    	int lastSqlNode = 0;
    	
    	List<Element> elements = document.getRootElement().getElements();
    	for(int i=0;i<elements.size() ; i++) {
    		XmlElement xmlNode =(XmlElement) elements.get(i);
    		if(xmlNode != null) {
	    		if(xmlNode.getName().equalsIgnoreCase(SQL_NODE)) {
	    			lastSqlNode = i;
	    		}
    		}
    	}
    	lastSqlNode = lastSqlNode> 0  ? ++lastSqlNode :  lastSqlNode;
    	//生成分页 node
    	lastSqlNode = generatePagingNode(lastSqlNode,root);
        
        //生成 order By Property 条件 
    	XmlElement node = generateOrderByPropertyNode(introspectedTable);
    	if(node !=null) {
    		root.addElement(++lastSqlNode, node);
    	}
        
        // order By Clause 条件  <sql id="orderByClause"></sql>
    	node = generateOrderByClauseNode();
    	if(node !=null) {
    		root.addElement(++lastSqlNode, node);
    	}
    	
        //filter <sql id="filter"></sql>
    	node = generateFilterNode();
    	if(node !=null) {
    		root.addElement(++lastSqlNode, node);
    	} 
        
        
        //分页 sql 语句 <select id="query" ></select>
    	lastSqlNode = generateQueryNode(++lastSqlNode, root, introspectedTable);
     
        
        //生成 getQueryCount 节
    	node = generateGetQueryCount(introspectedTable);
        if(node !=null) {
    		root.addElement(++lastSqlNode, node);
    	} 
        return true;
    }
    
    /**
     * 分页  sql id   <sql id="paging" >   </sql>
     * @param index
     * @return
     */
    protected abstract int generatePagingNode(int index,XmlElement root);
    
    /**
     * 生成 query 查询节点
     * @param index
     * @param root
     * @param introspectedTable
     */
    protected abstract int generateQueryNode(int index,XmlElement root,IntrospectedTable introspectedTable);
    
    /**
     * order By Clause 条件  <sql id="orderByClause"></sql>
     * @return
     */
    private XmlElement generateOrderByClauseNode() {
    	XmlElement orderEl = new XmlElement("sql");
        orderEl.addAttribute(new Attribute("id","orderByClause"));
        orderEl.addElement(new TextElement("<if test=\"order!=null and order.orderByClause!=null\">${order.orderByClause}</if>"));
        return orderEl;
    }
    
    /**
     * order By Property 条件  <sql id="orderByProperty"></sql>
     * @return
     */
    private XmlElement generateOrderByPropertyNode(IntrospectedTable introspectedTable) {
    	boolean hasDifferent = false;
    	String alias = introspectedTable.getTableConfiguration().getAlias();
        for(IntrospectedColumn col :introspectedTable.getPrimaryKeyColumns()) {
        	if(!col.getJavaProperty().equalsIgnoreCase(col.getActualColumnName())) {
        		hasDifferent = true;
        		break;
        	}
        }
        XmlElement orderEl = new XmlElement("sql");
        orderEl.addAttribute(new Attribute("id","orderByProperty"));
        orderEl.addElement(new TextElement("<if test=\"order!=null and order.orderByProperty!=null\">"));
        orderEl.addElement(new TextElement("    order by "));
        orderEl.addElement(new TextElement("	<foreach collection=\"order.orderByProperty\" item=\"item\" separator=\" \" >"));
        orderEl.addElement(new TextElement("		<foreach collection=\"item.fields\" item=\"field\" separator=\",\" >"));
        
        if( hasDifferent ) {
	        orderEl.addElement(new TextElement("			<choose>"));
	        for(IntrospectedColumn col :introspectedTable.getPrimaryKeyColumns()) {
	        	if(!col.getJavaProperty().equalsIgnoreCase(col.getActualColumnName())) {
	        		orderEl.addElement(new TextElement("				<when test=\"field=='"+col.getJavaProperty()+"'\">"+getColName(col.getTableAlias(),col.getActualColumnName())+"</when>"));
	        	}
	        }
	        orderEl.addElement(new TextElement("				<otherwise>"+(alias!=null && !alias.isEmpty() ? alias+"." : "")+"${field}</otherwise>"));
	        orderEl.addElement(new TextElement("			</choose>"));
        } else {
        	orderEl.addElement(new TextElement("			"+(alias!=null && !alias.isEmpty() ? alias+"." : "")+"${field}"));
        }
        
        
        orderEl.addElement(new TextElement("		</foreach>"));
        orderEl.addElement(new TextElement("		${item.sort}"));
        orderEl.addElement(new TextElement("	</foreach>"));
        orderEl.addElement(new TextElement("</if>"));
        return orderEl;
    }
    
    /**
     * filter <sql id="filter"></sql>
     * @return
     */
    private XmlElement generateFilterNode() {
        XmlElement filterEl = new XmlElement("sql");
        filterEl.addAttribute(new Attribute("id","filter"));
        filterEl.addElement(new TextElement("<where>"));
        filterEl.addElement(new TextElement("	<if test=\"filter!=null\">"));
        filterEl.addElement(new TextElement("	</if>"));
        filterEl.addElement(new TextElement("</where>"));
        return filterEl;
    }
    
    /**
     * 生成 getQueryCount 节点
     * @param introspectedTable
     * @return
     */
    private XmlElement generateGetQueryCount(IntrospectedTable introspectedTable) {
    	 XmlElement selectEl = new XmlElement("select");
         selectEl.addAttribute(new Attribute("id","getQueryCount"));
         selectEl.addAttribute(new Attribute("resultType","java.lang.Long"));
         selectEl.addAttribute(new Attribute("parameterType","org.graceful.correct.core.Filter"));
         
         String countCol;
         List<IntrospectedColumn> cols =  introspectedTable.getPrimaryKeyColumns();
         if(cols==null || cols.size() ==0) {
         	countCol = "*";
         } else {
         	IntrospectedColumn col = cols.get(0);
         	countCol = getColName(col.getTableAlias(),col.getActualColumnName());
         }
         
         selectEl.addElement(new TextElement("select count("+countCol+") from "+getTableName(introspectedTable)));
         selectEl.addElement(new TextElement("<include refid=\"filter\"/>"));
         
         return selectEl;
    }
    
    
    
    protected String getColName(String alias,String name) {
    	if(alias!=null && !alias.isEmpty())  {
    		return alias +"."+name;
    	} else {
    		return name;
    	}
    }
    
    protected String getTableName(IntrospectedTable introspectedTable) {
    	TableConfiguration config = introspectedTable.getTableConfiguration();
    	String schema = config.getSchema();
    	String catelog = config.getCatalog();
    	String alias = config.getAlias();
    	String query = config.getTableName();
    	
    	if(alias !=null && !alias.isEmpty()) {
    		query += " "+alias;
    	}
    	if(schema!=null && !schema.isEmpty()) {
    		query = schema +"." + query;
    	}
    	if(catelog!=null && !catelog.isEmpty()) {
    		query = catelog +"." + query;
    	}
    	
    	return query;
    }

}
