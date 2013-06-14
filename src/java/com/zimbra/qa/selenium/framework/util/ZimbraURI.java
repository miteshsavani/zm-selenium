/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;

public class ZimbraURI {
	private static final Logger logger = LogManager.getLogger(ZimbraURI.class);
	
	private URI myURI = null;
	
	public ZimbraURI() {
	}
	
	public ZimbraURI(String uri) {
		setURI(uri);
	}

	public ZimbraURI(URI uri) {
		setURI(uri);
	}
	
	/**
	 * Check if the current URL does not match the 'default' URL.  For instance,
	 * if the test case adds query parameters, then the URL needs to be reloaded.
	 * @return true if a reload is required
	 */
	public static boolean needsReload() {
		logger.debug("needsReload?");

		ZimbraURI base = new ZimbraURI(ZimbraURI.getBaseURI());
		ZimbraURI current = new ZimbraURI(ZimbraURI.getCurrentURI());
		
		
		logger.debug("base: "+ base.getURL().toString());
		logger.debug("current: "+ current.getURL().toString());
		
		// If the scheme, host, and query parameters are equal, then
		// no reload required
		//
		
		
		// Check the scheme
		if ( !base.getURL().getScheme().equals(current.getURL().getScheme()) ) {
			logger.info("Scheme: base("+ base.getURL().getScheme() +") != current("+ current.getURL().getScheme() +")");
			return (true);
		}
		
		// Check the host
		if ( !base.getURL().getHost().equals(current.getURL().getHost()) ) {
			logger.info("Host: base("+ base.getURL().getHost() +") != current("+ current.getURL().getHost() +")");
			return (true);
		}

		// Check the query parameters
		ZimbraQuery baseQuery = new ZimbraQuery(base.getURL().getQuery());
		ZimbraQuery currentQuery = new ZimbraQuery(current.getURL().getQuery());
		if ( !baseQuery.equals(currentQuery) ) {
			logger.info("Host: baseQuery("+ baseQuery.toString() +") != currentQuery("+ currentQuery.toString() +")");
			return (true);
		}
		
				
		logger.debug("needsReload? No");
		return (false);
		
	}
	
	/**
	 * Set the URL value for this ZimbraURL (for instance, to edit later)
	 * @param url
	 * @throws URLSyntaxException
	 */
	public void setURI(URI uri) {
		myURI = uri;
	}
	
	/**
	 * Set the URL value for this ZimbraURL (for instance, to edit later)
	 * @param URL
	 * @throws URLSyntaxException
	 */
	public void setURI(String uri) {
		try {
			myURI = new URI(uri);
		} catch (URISyntaxException e) {
			logger.error("Unable to parse uri: " + uri, e);
			myURI = ZimbraURI.defaultURI();
		}
	}
	
	/**
	 * Set the URL value for this ZimbraURL (for instance, to edit later)
	 * @param URL
	 * @throws URLSyntaxException
	 */
	public void setURL(String scheme, String userInfo, String host, int port, String path, String query, String fragment) {
		try {
			setURI(new URI(scheme, userInfo, host, port, path, query, fragment));
		} catch (URISyntaxException e) {
			logger.error("Unable to parse uri", e);
			myURI = ZimbraURI.defaultURI();
		}
	}
	
	/**
	 * Get the current URL value
	 * @param URL
	 * @throws URLSyntaxException
	 */
	public URI getURL() {
		return (myURI);
	}
	
	/**
	 * Get the current URL value as a string
	 * @param key
	 * @param value
	 * @return
	 */
	public String toString() {
		return (myURI.toString());
	}
	
	public URI addQuery(String key, String value) {
		
		// Get the current query
		ZimbraQuery query = new ZimbraQuery(myURI.getQuery());
		
		// Add the new value
		query.addQuery(key, value);
		
		// Convert the query into the URL
		setURL(
				myURI.getScheme(), 
				myURI.getUserInfo(),
				myURI.getHost(),
				myURI.getPort(),
				myURI.getPath(),
				query.toString(),
				myURI.getFragment());

		return (myURI);
		
	}
	
	public URI addQuery(String attributes) {
		
		// Get the current query
		ZimbraQuery query = new ZimbraQuery(myURI.getQuery());
		
		// Add the new value
		query.addQuery(attributes);
		
		// Convert the query into the URL
		setURL(
				myURI.getScheme(), 
				myURI.getUserInfo(),
				myURI.getHost(),
				myURI.getPort(),
				myURI.getPath(),
				query.toString(),
				myURI.getFragment());

		return (myURI);

	}
	
	/**
	 * Get the current browser location
	 * @return
	 * @throws URLSyntaxException
	 */
	public static URI getCurrentURI() {
		String uri;
		if (ZimbraSeleniumProperties.isWebDriver()){
		    uri = ClientSessionFactory.session().webDriver().getCurrentUrl();
		}else{
		    uri = ClientSessionFactory.session().selenium().getLocation();
		}
		try {
			return (new URI(uri));
		} catch (URISyntaxException e) {
			logger.error("Unable to parse current URL: "+ uri, e);
			return (ZimbraURI.defaultURI());
		}
	}

	/**
	 * Get the 'base' URL being used for this test run.  For example,
	 * https://zqa-001.eng.vmware.com.  Or, for performance test run,
	 * https://zqa-001.eng.vmware.com?perfMetric=1
	 * @return
	 * @throws URLSyntaxException
	 */
	public static URI getBaseURI() {
		
		String scheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
		String userinfo = null;
		String host = ZimbraSeleniumProperties.getStringProperty("server.host", "localhost");
		String port = ZimbraSeleniumProperties.getStringProperty("server.port", "7070");
		
		String path = null;
		ZimbraURI.ZimbraQuery query = new ZimbraURI.ZimbraQuery();
		String fragment = null;
		
		if ( CodeCoverage.getInstance().isEnabled() ) {
			query.addQuery(CodeCoverage.getInstance().getQueryAttributes());
		}
		
		if ( PerfMetrics.getInstance().Enabled ) {
			query.addQuery(PerfMetrics.getInstance().getQueryAttributes());
		}
		
		if ( ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ) {
		   logger.info("AppType is: " + ZimbraSeleniumProperties.getAppType());

		      ZimbraDesktopProperties zdp = ZimbraDesktopProperties.getInstance();
		      int maxRetry = 30;
		      int retry = 0;
		      while (retry < maxRetry && zdp.getSerialNumber() == null) {
		         logger.debug("Local Config file is still not ready");
		         SleepUtil.sleep(1000);
		         retry ++;
		         zdp = ZimbraDesktopProperties.getInstance();
		      }

		      port = zdp.getConnectionPort();
		      host = ZimbraSeleniumProperties.getStringProperty("desktop.server.host", "localhost");
		      path = "/desktop/login.jsp";
		      query.addQuery("at", zdp.getSerialNumber());

		}

		if ( ZimbraSeleniumProperties.getAppType() == AppType.AJAX ) {
			
			// FALL THROUGH

		}

		if ( ZimbraSeleniumProperties.getAppType() == AppType.HTML ) {
			
			path ="/h/";

		}

		if ( ZimbraSeleniumProperties.getAppType() == AppType.MOBILE ) {

			path ="/m/";
			
		}

		if ( ZimbraSeleniumProperties.getAppType() == AppType.ADMIN ) {
		
			scheme = "https";
			//path = "/zimbraAdmin/";
			path = "";
			port = "7071";

		}

		if ( ZimbraSeleniumProperties.getAppType() == AppType.OCTOPUS ) {
			
			// FALL THROUGH

		}
			
		try {
			URI uri = new URI(scheme, userinfo, host, Integer.parseInt(port), path, query.toString(), fragment);
			logger.info("Base uri: "+ uri.toString());
			return (uri);
		} catch (URISyntaxException e) {
			logger.error("unalbe to parse uri", e);
			return (ZimbraURI.defaultURI());
		}

	}
	
	private static URI defaultURI() {
		
		String scheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
		String host = ZimbraSeleniumProperties.getStringProperty("server.host", "localhost");
		String port = ZimbraSeleniumProperties.getStringProperty("server.port", "7070");

		try {
			return (new URI(scheme, null, host, Integer.parseInt(port), null, null, null));
		} catch (URISyntaxException e) {
			logger.error("Unable to generate default URL", e);
			return (null);
		}

	}
	
	public static class ZimbraQuery {
		
		protected final static String separator = "__SEP__";
		
		protected String myQuery = new String();
				
		public ZimbraQuery() {
		}
		
		public ZimbraQuery(String query) {
			myQuery = query;
		}
		
		public String toString() {
			return (normalize(myQuery));
		}
		
	    @Override 
	    public boolean equals(Object o) {
	    	
	    	if ( o == null ) {
	    		return (false);
	    	}
	    	
	    	if ( !(o instanceof ZimbraQuery) ) {
	    		return (false);
	    	}
	    	
	    	ZimbraQuery other = (ZimbraQuery)o;
	    	
	    	Map<String, String> mine = ZimbraQuery.buildMapFromQuery(myQuery);
	    	Map<String, String> theirs = ZimbraQuery.buildMapFromQuery(other.myQuery);
	    	
	    	if ( mine.entrySet().size() != theirs.entrySet().size() ) {
				logger.info("Query: inequal query count: "+ mine.entrySet().size() +" != "+ theirs.entrySet().size());
				return (false);
	    	}

			for (Map.Entry<String, String> entry : theirs.entrySet() ) {
				
				if ( !mine.containsKey(entry.getKey()) ) {
					logger.info("Query: mine does not contain query key: "+ entry.getKey());
					return (false); // Missing this key
				}
				
				String myValue = mine.get(entry.getKey());
				String theirValue = theirs.get(entry.getKey());
				
				if ( myValue == null ) {
					return ( theirValue == null );
				}

				if ( theirValue == null ) {
					return (false);
				}
				
				List<String> myList = Arrays.asList(myValue.split(ZimbraQuery.separator));
				List<String> theirList = Arrays.asList(theirValue.split(ZimbraQuery.separator));
				
				if ( !myList.equals(theirList) ) {
					return (false);
				}
				
			}

			return (true);
	    	
	    }
		
	    public ZimbraQuery addQuery(String attributes) {
	    	myQuery = myQuery + "&" + attributes;
	    	return (this);
	    }
	    
		public ZimbraQuery addQuery(String key, String value) {
			
			if ( key == null ) {
				return (this);
			}
			
			if ( myQuery == null ) {
				myQuery = new String();
			}
			
			if ( myQuery.trim().length() == 0 ) {
				
				if ( value == null ) {
					myQuery = key;
				} else {
					myQuery = key +"="+ value;
				}
				
			} else {
				
				if ( value == null ) {
					myQuery = myQuery + "&" + key;
				} else {
					myQuery = myQuery + "&" + key + "=" + value;
				}
			}

			return (this);
		}
		
		/**
		 * Remove duplicate key/value pairs
		 * @return null if empty, otherwise the query in String format
		 */
		public static String normalize(String query) {
			if ( query == null ) {
				return (query);
			}
			if ( query.trim().length() == 0 ) {
				return (null);
			}
			// TODO: remove duplicate key/value pairs
			return (query);
		}
		

		/**
		 * Convert a map to a query string (e.g. key1=value1&key2=value2)
		 * @return String
		 *  
		 */
		public static String buildQueryFromMap(Map<String, String> map){
			
			
			// Build the query from the map
			StringBuilder sb = null;
			
			for (Entry<String, String> set : map.entrySet()) {
				
				StringBuilder q = null;
				if ( set.getValue() == null ) {
					
					// No values, so set the query parameter to simply "key", for
					// example, http://server.com/?key1&key2&key3=value3&key4=value4
					
					q = new StringBuilder(set.getKey());
					
				} else {
					
					// Values are present.  Split into a separated list (if multiple), for
					// example, http://server.com/?key1=value1a&key1=value1b&key2&key3=value3
					//
					
					for ( String v : set.getValue().split(ZimbraQuery.separator)) {
						if ( q == null ) {
							q = (new StringBuilder()).append(set.getKey()).append("=").append(v);
						} else {
							q.append("&").append(set.getKey()).append("=").append(v);
						}
					}
					
				}

				if ( sb == null ) {
					sb = new StringBuilder(q.toString());
				} else {
					sb.append('&').append(q.toString());
				}
			}
			
			return ( sb == null ? null : sb.toString());
		}
		
		/**
		 * Convert a query string (i.e. ?key1=value1&key2=value2...)  
		 * to a map of key/values@param query
		 * @return
		 */
		public static Map<String, String> buildMapFromQuery(String query) {
			
			Map<String, String> map = new HashMap<String, String>();

			if ( query == null || query.trim().length() == 0 ) {
				return (map);
			}
			
			// Strip any starting '?' character
			String q = ( query.startsWith("?") ? query.replace("?", "") : query );
			
			for (String p : q.split("&")) {
				
				if ( p.contains("=") ) {
					
					String key = p.split("=")[0];
					String value = p.split("=")[1];
					
					if ( map.containsKey(key) ) {
						
						// Existing keys should be turned into a separated list of values
						String existing = map.get(key);
						map.put(key, existing + ZimbraQuery.separator + value);
						
					} else {
						
						map.put(key, value);
						
					}
					
				} else {
					
					// No value, just use p as the key and null as the value
					map.put(p, null);
					
				}
			}
			
			return (map);

		}
		
	}

}
