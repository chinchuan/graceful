package org.graceful.correct.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class HttpClient {

	private final static Log logger = LogFactory.getLog(HttpClient.class);
	
	private final static String CLIENT_AGREEMENT = "TLS";//使用协议  
	private final static String CHARSET = "utf-8";
	private final static String HTTPS = "https";
	private final static String PREFIX_PARAM = "?";
	private final static String AND_PARAM = "&";
	private final static String EQULAS = "=";
	
    /**连接超时*/
	private final static int TIMEOUT = 5000; 
	/**读取数据超时 */
	private final static int READTIMEOUT = 10000;
	
	private final static String BOUNDARY  = "----WebKitFormBoundary6R5wDpBEYhfORwl4";  //定义数据分隔线
	private final static String SORT_BOUNDARY = "--";
	private final static String BR = "\r\n"; //换行
	
	//属性
	private String charset=CHARSET;
	private String url;
	private HttpURLConnection  connection;
	private Map<String,String> params = new HashMap<String,String>();
	private Map<String,String> requestProperty = new HashMap<String,String>();
	private InputStream is = null;
	private String id; //唯一标识
	private boolean redirect = false;
	private List<HttpCookie> httpCookies ;
	
 
	/**
	 *  Create an class to trust all hosts
	 */
	private static HostnameVerifier hnv = new HostnameVerifier() {
		 public boolean verify(String hostname, SSLSession session) {
			 return true;
	     }
	};
	
	public HttpClient(String url) {
		this(url, null);
	}
	
	public HttpClient(String url,String id) {
		this.url = url;
		this.id = id;
		//初始化requestProperty 值 
		this.requestProperty.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.requestProperty.put("connection", "Keep-Alive");  
		this.requestProperty.put("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		
	}
 
	
	public HttpClient charset(String charset) {
		this.charset = charset;
		if(this.charset!=null && !this.charset.isEmpty()) {
			this.requestProperty.put("Accept-Charset",this.charset);
		}
		return this;
	}
	
	public HttpClient addParam(String key,String value) {
		this.params.put(key, value);
		return this;
	}
	
	public HttpClient addRequestProperty(String key,String value) {
		this.requestProperty.put(key, value);
		return this;
	}
	
	public HttpClient redirects(boolean value) {
		this.redirect = value;
		return this;
	}
	
	private void initConntection() {
		if(url == null || url.isEmpty()) {
			return;
		}
		URL requestUrl=null;
		try {
			requestUrl = new URL(this.url);
			//是否是https
			if(HTTPS.equalsIgnoreCase(url.substring(0,url.indexOf(":")))) {
				// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
				SSLContext ctx = SSLContext.getInstance(CLIENT_AGREEMENT);//("SSL", "SunJSSE");
				// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
				ctx.init(null, new TrustManager[] { new POSPX509TrustManager() }, new java.security.SecureRandom());
				 
				HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hnv);
				this.connection = (HttpsURLConnection)requestUrl.openConnection();
				
			} else {
				this.connection = (HttpURLConnection)requestUrl.openConnection();
			}
			
			this.connection.setDoInput(true);
			this.connection.setConnectTimeout(TIMEOUT);
			this.connection.setReadTimeout(READTIMEOUT);
			this.connection.setInstanceFollowRedirects(this.redirect);
	 
			this.requestProperty.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			this.requestProperty.put("connection", "Keep-Alive");  
			this.requestProperty.put("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
			// http  app id
			if(id == null || id.isEmpty()) {
				this.id = requestUrl.getHost();
			}
			
			if(this.httpCookies!=null && this.httpCookies.size()>0) {
				HttpCookieManager.putCookie(this.connection, httpCookies);  //设置cookie
			}
			
			//设置 requestProperty
			this.addRequestProperty();
			
		} catch (MalformedURLException e) {
			logger.error(e);
			throw new NetException(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e);
			throw new NetException(e.getMessage(),e);
		} catch (KeyManagementException e) {
			logger.error(e);
			throw new NetException(e.getMessage(),e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			throw new NetException(e.getMessage(),e);
		}
	}
	
	public HttpClient doGet() {
		HttpMethod method = HttpMethod.GET;
		//初始化url
		if(this.url != null  && !this.url.isEmpty()) {
			String params = getParams(method);
			if(params!=null && !params.isEmpty()) {
				if(this.url.indexOf(PREFIX_PARAM) >0) {
					this.url += AND_PARAM + params;
				} else {
					this.url += PREFIX_PARAM + params;
				}
			}
		}
		
		initConntection();
		
		if(this.connection !=null ) {
			try {
				this.connection.setRequestMethod(method.name());
				this.connection.setUseCaches(true);
				response(); //响应
			} catch (ProtocolException e) {
				logger.error(e);
			}
		}
		return this;
	}
	
	public HttpClient doPost() {
		return doPost(null);
	}
	
	public HttpClient doPost(String body) {
		HttpMethod method = HttpMethod.POST;
		initConntection();
		OutputStream os = null;
		if(this.connection !=null ) {
			//form post
			//this.requestProperty.put("Content-Type","application/x-www-form-urlencoded;");
			try {
				this.connection.setRequestMethod(method.name());
				this.connection.setUseCaches(false);
				this.connection.setDoOutput(true);
				//加入post 参数
				os = this.connection.getOutputStream();
				String requestParams = getParams(method);
				//加入参数
				if(requestParams!=null && !requestParams.isEmpty()){
					os.write(requestParams.getBytes(charset!=null ? charset  : CHARSET)); //发送数据
				}
				if(body!=null && !body.isEmpty()) {
					os.write(body.getBytes(charset!=null ? charset  : CHARSET));
				}
				os.flush();
				os.close();
				
				response(); //响应
			} catch (ProtocolException e) {
				logger.error(e);
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			} finally {
				if(os!=null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return this;
	}
	
	/**
	 * 获取返回数据
	 * @return
	 */
	public String getResponseAsString() {
		if(this.is == null) {
			return null;
		}
		//读取数据
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(is,this.charset!=null && !this.charset.isEmpty() ? this.charset : CHARSET));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = rd.readLine()) != null) { 
		        sb.append(line);
		    } 
			return sb.toString();
		   
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if(rd!=null) {
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getResponseJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (Map<String,Object>)mapper.readValue(this.getResponseAsString(), Map.class);
		} catch (JsonParseException e) {
			logger.error(e);
		} catch (JsonMappingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}
	
	public InputStream getResponse() {
		return this.is;
	}
	
	/**
	 * 释放
	 */
	public void release() {
		if(this.is!=null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.params.clear();
		this.params = null;
		this.requestProperty.clear();
		this.requestProperty = null;
		if(this.connection!=null) {
			this.connection.disconnect();
			this.connection = null;
		}
	}
	
	/**
	 * 设置 requestProperty
	 */
	private void addRequestProperty() {
		if(requestProperty!=null && this.connection!=null) {
			String key;
			for(Iterator<String> it = requestProperty.keySet().iterator();it.hasNext();){
				key = it.next();
				this.connection.setRequestProperty(key,requestProperty.get(key));
			}
		}
	}
	
	private void response() {
		
		try {
			//获取返回数据
			int code = this.connection.getResponseCode();
			if(logger.isDebugEnabled()) {
				logger.debug("response code:"+code);
			}
			if(code>=200 && code<300){  //返回成功
				this.is = this.connection.getInputStream();
				//获取cookie 并储存
				HttpCookieManager.getCookie(connection,this.id);
			}
			
		} catch (IOException e) {
			logger.error(e);
		} 
	}
	
	
	/**
	 * http 请求  带图片上传
	 * 
	 * @param postUrl  url地址
	 * @param params   参数内容
	 * @param formName 图片表单名称
	 * @param fileName 图片文件名
	 * @param jpg      要上传的图片(jpg格式)
	 * 
	 * @return  内容
	 * @throws ElecException
	 */
	public static String sendPostRequest(String postUrl,Map<String,String> params,String formName,String fileName,byte[] jpg){
		OutputStream os = null;
		InputStream is = null;
		String result = null;
		
		try {
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();//打开连接
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(READTIMEOUT);
			conn.setUseCaches(false);
			conn.setRequestProperty("connection", "Keep-Alive");  
	        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"); 
	        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
	        
			os = conn.getOutputStream();
			
			//要上传的参数域
			if(params!=null && params.size()>0){
				StringBuilder txtTag = new StringBuilder();   
				for(Iterator<String> it = params.keySet().iterator(); it.hasNext();){
					String key = it.next();
					String value = params.get(key);
					txtTag.append(SORT_BOUNDARY).append(BOUNDARY).append(BR);
					txtTag.append("Content-Disposition: form-data; name=\""+key+"\"").append(BR).append(BR); //二行
					txtTag.append(value).append(BR);
				}
				os.write(txtTag.toString().getBytes());//写入参数
			}
			//文件域
			if(jpg!=null && jpg.length>0){
				StringBuilder fileTag = new StringBuilder();  
				fileTag.append(SORT_BOUNDARY).append(BOUNDARY).append(BR);
				fileTag.append("Content-Disposition: form-data; name=\""+formName+"\"; filename=\""+fileName+"\"").append(BR);
				fileTag.append("Content-Type: image/pjpeg").append(BR).append(BR);
				os.write(fileTag.toString().getBytes());
				os.write(jpg);//写入文件
				os.write(BR.getBytes());//换行
			}
			
			//结束域
			os.write((SORT_BOUNDARY + BOUNDARY+SORT_BOUNDARY+BR).getBytes());
			 
			os.flush();
			os.close();
			
			is = conn.getInputStream();
			//读取数据
			StringBuilder sb = new StringBuilder();
			int code = conn.getResponseCode();
			if(code>=200 && code<300){  //返回成功
				BufferedReader rd = new BufferedReader(new InputStreamReader(is,CHARSET));
				String line = null;
				while ((line = rd.readLine()) != null) { 
			        sb.append(line);
			   } 
			}
			is.close();
			result =  sb.toString();
			
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (ProtocolException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(os!=null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
	/**
	 * 获取 post 参数
	 * @param params  参数集合
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private  String getParams(HttpMethod method){
		if(params!=null){
			StringBuilder sb = new StringBuilder();
			String key;
			String value;
			try {
				for(Iterator<String> it = params.keySet().iterator(); it.hasNext();){
					key = it.next();
					value = params.get(key);
					if(value!=null && !value.isEmpty()) {
						//get 请求应对url 编码
						if(method == HttpMethod.GET) {
							value =  URLEncoder.encode(value, this.charset);
						}
						sb.append(key).append(EQULAS).append(value).append(AND_PARAM);
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(sb.length()>0){
				sb.delete(sb.length()-1, sb.length());
			}
			
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * 证书信认管理 ，默认所有
	 * @author qin
	 * 
	 * 2012-11-16
	 */
	static class POSPX509TrustManager implements X509TrustManager {
		/*
		 * Delegate to the default trust manager.
		 */
		public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {
		}
		/*
		 * Delegate to the default trust manager.
		 */
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		/*
		 * Merely pass this through.
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
