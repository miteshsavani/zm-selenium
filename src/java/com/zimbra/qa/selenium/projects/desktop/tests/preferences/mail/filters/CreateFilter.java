/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.filters;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.DialogEditFilter;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.DialogEditFilter.ConditionConstraint;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.DialogEditFilter.ConditionType;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.TreePreferences.TreeItem;


public class CreateFilter extends AjaxCommonTest {

	public CreateFilter() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test( description = "Create a basic filter", groups = { "smoke" })
	public void CreateFilter_01() throws HarnessException {

		String filterName = "filter"+ ZimbraSeleniumProperties.getUniqueString();
		String conditionValue = "contains"+ ZimbraSeleniumProperties.getUniqueString();

		
		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailFilters);

		// Click "Add New"
		DialogEditFilter dialog = (DialogEditFilter)app.zPagePreferences.zToolbarPressButton(Button.B_NEW_FILTER);
		
		// Give a name
		dialog.zSetFilterName(filterName);
		
		// Give a criteria
		dialog.zAddFilterCriteria(ConditionType.Subject, ConditionConstraint.Contains, conditionValue);
	
		// Save
		dialog.zClickButton(Button.B_OK);
		
		//Sync to ZWC
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());		
		
		// Verify the filter is created
		app.zGetActiveAccount().soapSend("<GetFilterRulesRequest xmlns='urn:zimbraMail'/>");
		
		Element[] rules = app.zGetActiveAccount().soapSelectNodes("//mail:GetFilterRulesResponse//mail:filterRule[@name='" + filterName +"']");
		ZAssert.assertEquals(rules.length, 1, "Verify the rule exists in the server");
	}
}
