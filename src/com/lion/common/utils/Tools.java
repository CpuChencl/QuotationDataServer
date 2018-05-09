package com.lion.common.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.lion.frame.logger.Log4jManager;

public class Tools {
	private static final Logger log = Log4jManager.get();
	private static ObjectMapper objectMapper = new ObjectMapper();
	public static final String GBK = "GBK";
	public static final String UTF8 = "UTF-8";
	public static final String NOMAL_POST = "nomal";
	public static final String BYTE_POST = "byte";

    public static ObjectMapper getJsonInstance(){
    	return objectMapper;
    }
    
    public static <T> String toJson(T t){
    	try {
			return objectMapper.writeValueAsString(t);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			log.error("json转换异常");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			log.error("json转换异常");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("json转换异常");
		}
		return null;
    }
    public static <T> T jsonToObj(String json, Class<T> clazz){
    	try {
			return (T)objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("json转换异常");
		}
    	return null;
    }

	public static String isNull(String str) {
		if (str == null)
			return "";
		return str;
	}

	public static boolean checkNull(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String && "".equals(obj.toString())) {
			return true;
		}
		return false;
	}
	

    /**
     * @throws IOException 
     * 
     * @Title: httpPost 
     * @Description: httpPost请求工具类
     * @param url
     * @param params
     * @param charset
     * @param socketTimeOut
     * @param connectTimeOut
     * @param connReqTimeOut
     * @return    设定文件
     * @return String    返回类型
     * @author 白金鹏
     * @date 2015年7月10日 上午11:55:39 
     * @throws
     */
    public static String httpPost(String url, Map<String, Object> params, String charset, String postType, int socketTimeOut, int connectTimeOut, int connReqTimeOut ) throws IOException{
    	if (Tools.checkNull(charset)) {
    		charset = UTF8;
    	}
    	if (Tools.checkNull(postType)) {
			postType = NOMAL_POST;
		}
    	HttpPost httppost = new HttpPost(url);
    	RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(connReqTimeOut)
    			.build();//设置请求和传输超时时间
    	httppost.setConfig(requestConfig);
    	String result = null;
    	CloseableHttpClient httpclient = HttpClients.custom().build();
    	CloseableHttpResponse response = null;
    	try{
    		
    		//定义传递参数
    		if (BYTE_POST.equals(postType)) {
//    			String encodeStr = URLEncoder.encode(Tools.toJson(params), charset);
    			httppost.setEntity(new ByteArrayEntity(Tools.toJson(params).getBytes(charset)));
			} else {
				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				if (params != null) {
					for (Entry<String,Object> entry : params.entrySet()) {
						formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()==null?"":entry.getValue().toString()));
					}
				}
				httppost.setEntity(new UrlEncodedFormEntity(formparams, charset));
			}
    		response = httpclient.execute(httppost);
    		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			HttpEntity entity = response.getEntity();
    			if (entity != null) {
    				return EntityUtils.toString(entity, charset);
    			}
    		} else {
    			return result;
    		}
    	} finally{
    		if (response != null) {
    			try {
    				response.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		try {
    			httpclient.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return result;
    }
    
    /**
    * @throws IOException 
    * 
    * @Title: httpPost 
    * @Description: httpPost请求工具类
    * @param url
    * @param params
    * @param postType
    * @param timeOut
    * @param charset
    * @return    设定文件
    * @return String    返回类型
    * @author 白金鹏
    * @date 2015年7月2日 下午4:51:55 
    * @throws
    */
    public static String httpPost(String url, Map<String, Object> params, String charset, String postType, int timeOut) throws IOException{
    	return httpPost(url, params, charset, postType, timeOut, timeOut, timeOut);
    }
    
    /**
     * @throws IOException 
     * 
     * @Title: httpGet 
     * @Description: httpPost请求工具类
     * @param url
     * @param params
     * @param charset
     * @param socketTimeOut
     * @param connectTimeOut
     * @param connReqTimeOut
     * @return    设定文件
     * @return String    返回类型
     * @author 白金鹏
     * @date 2015年7月10日 上午11:55:39 
     * @throws
     */
    public static String httpGet(String url, Map<String, Object> params, String charset, int socketTimeOut, int connectTimeOut, int connReqTimeOut ) throws IOException{
    	if (Tools.checkNull(charset)) {
    		charset = UTF8;
    	}
    	String result = null;
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	CloseableHttpResponse response = null;
    	try{
    		//定义传递参数
    		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    		for (Entry<String,Object> entry : params.entrySet()) {
				formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()==null?"":entry.getValue().toString()));
    		}
    		url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(formparams), charset);
    		
    		HttpGet httpget = new HttpGet(url);
    		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(connReqTimeOut)
    				.build();//设置请求和传输超时时间
    		httpget.setConfig(requestConfig);
    		response = httpclient.execute(httpget); 
    		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			HttpEntity entity = response.getEntity();
    			if (entity != null) {
    				return EntityUtils.toString(entity, charset);
    			}
    		} else {
    			return result;
    		}
    	} finally{
    		if (response != null) {
    			try {
    				response.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		try {
    			httpclient.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return result;
    }
    
    /**
     * @throws IOException 
     * 
     * @Title: httpGet 
     * @Description: httpGet请求工具类
     * @param url
     * @param params
     * @param charset
     * @param timeOut
     * @return    设定文件
     * @return String    返回类型
     * @author 白金鹏
     * @date 2015年7月15日 上午10:34:41 
     * @throws
     */
    public static String httpGet(String url, Map<String, Object> params, String charset, int timeOut) throws IOException{
    	return httpGet(url, params, charset, timeOut, timeOut, timeOut);
    }

}