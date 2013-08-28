package org.graceful.correct.web.tags;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

public abstract class AbstractHtmlTag extends RequestContextAwareTag {

	private static final long serialVersionUID = 1764560460400579266L;

	public static final String CLASS_ATTRIBUTE = "class";

	public static final String STYLE_ATTRIBUTE = "style";

	public static final String LANG_ATTRIBUTE = "lang";

	public static final String TITLE_ATTRIBUTE = "title";

	public static final String DIR_ATTRIBUTE = "dir";

	public static final String TABINDEX_ATTRIBUTE = "tabindex";

	public static final String ONCLICK_ATTRIBUTE = "onclick";

	public static final String ONDBLCLICK_ATTRIBUTE = "ondblclick";

	public static final String ONMOUSEDOWN_ATTRIBUTE = "onmousedown";

	public static final String ONMOUSEUP_ATTRIBUTE = "onmouseup";

	public static final String ONMOUSEOVER_ATTRIBUTE = "onmouseover";

	public static final String ONMOUSEMOVE_ATTRIBUTE = "onmousemove";

	public static final String ONMOUSEOUT_ATTRIBUTE = "onmouseout";

	public static final String ONKEYPRESS_ATTRIBUTE = "onkeypress";

	public static final String ONKEYUP_ATTRIBUTE = "onkeyup";

	public static final String ONKEYDOWN_ATTRIBUTE = "onkeydown";

	private String cssClass;

	private String cssErrorClass;

	private String cssStyle;

	private String lang;

	private String title;

	private String dir;

	private String tabindex;

	private String onclick;

	private String ondblclick;

	private String onmousedown;

	private String onmouseup;

	private String onmouseover;

	private String onmousemove;

	private String onmouseout;

	private String onkeypress;

	private String onkeyup;

	private String onkeydown;

	@Override
	protected int doStartTagInternal() throws Exception {
		return writeTagContent(pageContext.getOut());
	}

	/**
	 * 写入属性
	 * 
	 * @param tagWriter
	 * @param attributeName
	 * @param attributeValue
	 */
	protected final void writeOptionalAttribute(Writer tagWriter,
			String attributeName, String attributeValue) throws JspException {
		if (attributeValue != null && !attributeValue.isEmpty()) {
			try {
				tagWriter.write(" ");
				tagWriter.write(attributeName);
				tagWriter.write("=\"");
				tagWriter.write(attributeValue);
				tagWriter.write("\"");
			} catch (IOException e) {
				throw new JspException("Unable to write to JspWriter",e);
			}
		}
	}

	/**
	 * 写入所有属性
	 * 
	 * @param tagWriter
	 * @throws JspException
	 * @throws IOException
	 */
	protected void writeOptionalAttributes(Writer tagWriter)
			throws JspException {

		writeOptionalAttribute(tagWriter, CLASS_ATTRIBUTE, getCssClass());
		writeOptionalAttribute(tagWriter, STYLE_ATTRIBUTE, getCssStyle());
		writeOptionalAttribute(tagWriter, LANG_ATTRIBUTE, getLang());
		writeOptionalAttribute(tagWriter, TITLE_ATTRIBUTE, getTitle());
		writeOptionalAttribute(tagWriter, DIR_ATTRIBUTE, getDir());
		writeOptionalAttribute(tagWriter, TABINDEX_ATTRIBUTE, getTabindex());
		writeOptionalAttribute(tagWriter, ONCLICK_ATTRIBUTE, getOnclick());
		writeOptionalAttribute(tagWriter, ONDBLCLICK_ATTRIBUTE, getOndblclick());
		writeOptionalAttribute(tagWriter, ONMOUSEDOWN_ATTRIBUTE,getOnmousedown());
		writeOptionalAttribute(tagWriter, ONMOUSEUP_ATTRIBUTE, getOnmouseup());
		writeOptionalAttribute(tagWriter, ONMOUSEOVER_ATTRIBUTE,getOnmouseover());
		writeOptionalAttribute(tagWriter, ONMOUSEMOVE_ATTRIBUTE,getOnmousemove());
		writeOptionalAttribute(tagWriter, ONMOUSEOUT_ATTRIBUTE, getOnmouseout());
		writeOptionalAttribute(tagWriter, ONKEYPRESS_ATTRIBUTE, getOnkeypress());
		writeOptionalAttribute(tagWriter, ONKEYUP_ATTRIBUTE, getOnkeyup());
		writeOptionalAttribute(tagWriter, ONKEYDOWN_ATTRIBUTE, getOnkeydown());

	}

	/**
	 * Subclasses should implement this method to perform tag content rendering.
	 * 
	 * @return valid tag render instruction as per
	 *         {@link javax.servlet.jsp.tagext.Tag#doStartTag()}.
	 */
	protected abstract int writeTagContent(Writer tagWriter)
			throws JspException;

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCssErrorClass() {
		return cssErrorClass;
	}

	public void setCssErrorClass(String cssErrorClass) {
		this.cssErrorClass = cssErrorClass;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getTabindex() {
		return tabindex;
	}

	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getOndblclick() {
		return ondblclick;
	}

	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	public String getOnmousedown() {
		return onmousedown;
	}

	public void setOnmousedown(String onmousedown) {
		this.onmousedown = onmousedown;
	}

	public String getOnmouseup() {
		return onmouseup;
	}

	public void setOnmouseup(String onmouseup) {
		this.onmouseup = onmouseup;
	}

	public String getOnmouseover() {
		return onmouseover;
	}

	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}

	public String getOnmousemove() {
		return onmousemove;
	}

	public void setOnmousemove(String onmousemove) {
		this.onmousemove = onmousemove;
	}

	public String getOnmouseout() {
		return onmouseout;
	}

	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}

	public String getOnkeypress() {
		return onkeypress;
	}

	public void setOnkeypress(String onkeypress) {
		this.onkeypress = onkeypress;
	}

	public String getOnkeyup() {
		return onkeyup;
	}

	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	public String getOnkeydown() {
		return onkeydown;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

}
