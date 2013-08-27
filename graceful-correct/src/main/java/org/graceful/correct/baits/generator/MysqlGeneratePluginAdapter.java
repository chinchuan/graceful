package org.graceful.correct.baits.generator;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class MysqlGeneratePluginAdapter extends GracefulGeneratePluginAdapter {

	@Override
	protected int generatePagingNode(int index, XmlElement root) {
		XmlElement pageEl = new XmlElement("sql"); 
		pageEl.addAttribute(new Attribute("id","paging"));
		pageEl.addElement(new TextElement("<if test=\"page!=null\">"));
		pageEl.addElement(new TextElement("	limit ${page.offset} ,${page.pageSize}"));
		pageEl.addElement(new TextElement("</if>"));
		root.addElement(index, pageEl); 
		return index;
	}

	@Override
	protected int generateQueryNode(int index, XmlElement root,IntrospectedTable introspectedTable) {
		XmlElement selectEl = new XmlElement("select");
        selectEl.addAttribute(new Attribute("id","query"));
        selectEl.addAttribute(new Attribute("resultMap",introspectedTable.getBaseResultMapId()));
        selectEl.addAttribute(new Attribute("parameterType","map"));
        
        selectEl.addElement(new TextElement("select"));
        selectEl.addElement(new TextElement("<include refid=\"Base_Column_List\"/>"));
        selectEl.addElement(new TextElement("from "+getTableName(introspectedTable)));
        selectEl.addElement(new TextElement("<include refid=\"filter\"/>"));
        selectEl.addElement(new TextElement("<include refid=\"orderByProperty\"/>"));
        selectEl.addElement(new TextElement("<include refid=\"paging\"/>"));

		root.addElement(index, selectEl); // 插入 select 节
		return index;
	}

}
