package org.graceful.correct.export;


public  class GridHeader {

	private String field;
	private String name;
	private ValueFormat format=null;
	private String stringFormat;
	private int minWidth =0;
	
	
	public GridHeader(String name,String field){
		this(name,field,0,null,null);
	}
	
	public GridHeader(String name,String field,int minWidth){
		this(name,field,minWidth,null,null);
	}
	
	public GridHeader(String name,ValueFormat format){
		this(name,null,0,null,format);
	}
	
	public GridHeader(String name,ValueFormat format,int minWidth){
		this(name,null,minWidth,null,format);
	}
	
	public GridHeader(String name,String field,ValueFormat format){
		this(name,field,0,null,format);
	}
	
	public GridHeader(String name,String field,int minWidth,ValueFormat format){
		this(name,field,minWidth,null,format);
	}
	
	public GridHeader(String name,String field,String format){
		this(name,field,0,format,null);
	}
	
	public GridHeader(String name,String field,int minWidth,String format){
		this(name,field,minWidth,format,null);
	}
	
	public GridHeader(String name,String field,String stringFormat,ValueFormat format){
		this(name,field,0,stringFormat,format);
	}
	
	public GridHeader(String name,String field,Integer minWidth,String stringFormat,ValueFormat format){
		this.field = field;
		this.name = name;
		this.stringFormat = stringFormat;
		this.format = format;
		this.setMinWidth(minWidth);
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ValueFormat getFormat() {
		return format;
	}

	public void setFormat(ValueFormat format) {
		this.format = format;
	}

	public String getStringFormat() {
		return stringFormat;
	}

	public void setStringFormat(String stringFormat) {
		this.stringFormat = stringFormat;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public int getMinWidth() {
		return minWidth;
	}
}