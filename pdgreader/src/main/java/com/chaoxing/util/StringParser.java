/*
 * 功能描述：解析URL获取参数等信息
 * Author：liujun
 */
package com.chaoxing.util;

import java.util.HashMap;
import java.util.Map;

public class StringParser {
	private Map<String, String[]> parameterMap;
	private String pathUrl;
	
	public int parser(String content) {
		    if (content == null || content.length() == 0) {   
			      return -1;   
			    }   

			String queryString = content;
			
		 if (queryString == null || queryString.length() == 0) {   
			      return -3;   
			    } 
			
			parameterMap = new HashMap<String, String[]>();
			
		      int ampersandIndex, lastAmpersandIndex = 0;   
		      String subStr, param, value;
		      String[] paramPair, values, newValues;
		      do {   
		        ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;   
		        if (ampersandIndex > 0) {   
		          subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);   
		          lastAmpersandIndex = ampersandIndex;   
		        } else {   
		          subStr = queryString.substring(lastAmpersandIndex);   
		        }   
		        paramPair = subStr.split("=");   
		        param = paramPair[0];   
		        value = paramPair.length == 1 ? "" : paramPair[1];     
		        if (parameterMap.containsKey(param)) {   
		          values = parameterMap.get(param);   
		          int len = values.length;   
		          newValues = new String[len + 1];
		          System.arraycopy(values, 0, newValues, 0, len);
		          newValues[len] = value;   
		        } else {   
		          newValues = new String[] { value };
		        }   
		        parameterMap.put(param, newValues);   
		      } while (ampersandIndex > 0);    
		    return 0;   
	}
	
	/**  
	* 获得指定名称的参数  
	* @param name  
	* @return  
	*/  
	public String getParameter(String name) {
		String[] values = parameterMap.get(name);
		if (values != null && values.length > 0) {   
			return values[0];   
		}   
		return null;   
	} 
}
