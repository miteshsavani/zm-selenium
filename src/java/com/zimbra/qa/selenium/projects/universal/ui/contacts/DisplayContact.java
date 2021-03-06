/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2015, 2016 Synacor, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.universal.ui.contacts;

import java.util.ArrayList;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;


public class DisplayContact extends  AbsDisplay {
	public static String ALPHABET_PREFIX = "css=table[id$='alphabet'] td[_idx=";
	public static String ALPHABET_POSTFIX = "]";

	public static class Locators {
		public static final String zLocator = "xpath=//div[@class='ZmContactInfoView']";
	}

	public static enum Field {
		FirstName, LastName, FileAs, Location, JobTitle, Company, Email
	}
	
	protected DisplayContact(AbsApplication application) {
		super(application);
		logger.info("new " + DisplayContact.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		tracer.trace("Click "+ button);
		throw new HarnessException("no logic defined for button: "+ button);
	}

	public String zGetContactProperty(Field field) throws HarnessException {
		logger.info("DisplayContact.zGetContactProperty(" + field + ")");

		ArrayList<String> locatorArray = new ArrayList<String>();
		
		if ( field == Field.FileAs ) {			
		    locatorArray.add("css=table[class*='contactHeaderTable'] div[class*='contactHeader']");
		} else if ( field == Field.JobTitle ) {					   			
		    locatorArray.add("css=table[class*='contactHeaderTable'] div[class='companyName']:nth-of-type(1)");
		} else if ( field == Field.Company ) {					   						
		    locatorArray.add("css=table[class*='contactHeaderTable'] div[class='companyName']:nth-of-type(2)");
		} else if ( field == Field.Email ) {					   			
			getAllLocators(locatorArray,"email");
		} else {
			throw new HarnessException("no logic defined for field "+ field);			
		}
		
		String value = "";

		for (int i=0; i<locatorArray.size(); i++) {
           String locator = locatorArray.get(i);
           
		   if ( locator == null )
			   throw new HarnessException("locator was null for field = "+ field);
		
		   if ( !this.sIsElementPresent(locator) )
			   throw new HarnessException("Unable to find the field = "+ field +" using locator = "+ locator);
		
		    value += this.sGetText(locator).trim();		
		}
		
		logger.info("DisplayContact.zGetContactProperty(" + field + ") = " + value);
		return(value);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		return sIsElementPresent("css=div#zv__CNS-main");
	}

	private void getAllLocators(ArrayList<String> array, String postfix) throws HarnessException {
  	   String css= "css=div[id$='_content'][class='ZmContactInfoView'] table:nth-of-type(2) tbody tr";
       int count= this.sGetCssCount(css);

       for (int i=1; i<=count; i++) {
	     array.add( css + ":nth-of-type(" + i + ")" + " td[id$='_" + postfix + "']");
       }
    }
}
