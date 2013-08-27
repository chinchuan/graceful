package org.graceful.correct.net;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * cookie 管理
 * @author qin
 * 
 * @date 2013-6-27
 */
public class HttpCookieManager {
	private static final String SET_COOKIE = "set-cookie";
	
	private static final Map<String,List<HttpCookie>> store = new HashMap<String,List<HttpCookie>>();
	private static final Lock lock = new ReentrantLock();
	
	
	/**
	 * 设置cookie
	 * @param http
	 * @param cookies
	 */
	public static void putCookie(HttpURLConnection http,List<HttpCookie> cookies) {
		String cookieString = null;
		
		if(cookies!=null) {
			StringBuilder sb = new StringBuilder();
			for(HttpCookie cookie :  cookies) {
				// && HttpCookie.domainMatches(cookie.getDomain(), host) 
				if(!cookie.hasExpired()) {
					sb.append(cookie.toString()).append(";");
				}
			}
			int count = sb.length();
			if(count> 0 ) {
				sb.deleteCharAt(count-1);
				cookieString = sb.toString();
			}
		} 
		if(cookieString!=null && !cookieString.isEmpty()) {
			http.setRequestProperty("Cookie", cookieString);
		}
	}
	
	/**
	 * 设置cookie 串
	 * @param domain 
	 * @return
	 */
	public static void setCookie(HttpURLConnection http,String id) {
		//String host = http.getURL().getHost();
		List<HttpCookie> cookies = store.get(id);
		putCookie(http,cookies);
	}
	
	/**
	 * 存入cookie
	 * @param http 
	 */
	public static void getCookie(HttpURLConnection http,String id){
		List<HttpCookie> cookies = store.get(id);
		if(cookies == null) {
			cookies = new ArrayList<HttpCookie>();
			lock.lock();
			try{
				store.put(id, cookies);
			}finally{
				lock.unlock();
			}
		}
		getCookie(http,cookies);
	}
	
	public static List<HttpCookie> getCookie(HttpURLConnection http,List<HttpCookie> cookies){
		//取出 http  cookie
		String headerName = null,header = null;  
		List<HttpCookie> target = null;
		for (int i = 1; (headerName = http.getHeaderFieldKey(i)) != null; i++) {
			 if (headerName.equalsIgnoreCase(SET_COOKIE)) {  
				 header = http.getHeaderField(i);
				 target = HttpCookie.parse(header);
				 //保存cookie
				 if(target.size() > 0) {
					if(cookies.size() > 0) { //替换旧值
						for(HttpCookie compare : target) { 
							for (int j = cookies.size()-1; j >= 0; j--) {
								if(cookies.get(j).equals(compare)) {
									 cookies.remove(j); //移除旧值
								 }
							}
						}
					} 
					cookies.addAll(target);
				 }
			 }
		}
		return cookies;
	}
  
}
