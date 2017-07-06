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
package com.zimbra.qa.selenium.projects.universal.tests.network.preferences.accounts.twofactorauth;

import java.util.HashMap;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.UniversalCommonTest;
import com.zimbra.qa.selenium.projects.universal.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.universal.ui.preferences.PagePreferences.Locators;
import com.zimbra.qa.selenium.projects.universal.ui.preferences.TreePreferences.TreeItem;

public class RevokeDevice extends UniversalCommonTest {

	public RevokeDevice() {
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 2485388299568483622L;
			{				
		 		put("zimbraFeatureTwoFactorAuthAvailable", "TRUE");
			}
		};
	}

	@Test( description = "Revoke the trusted computer and verify that totp is required after that", priority=4, 
			groups = { "functional", "L2", "network" })
	
	public void RevokeThisDevie_01() throws HarnessException {
		
		String totp, secret, tempToken;
		
		ZimbraAccount.AccountZWC().soapSend(
        		"<EnableTwoFactorAuthRequest xmlns='urn:zimbraAccount'>" +
        		"<name>" + ZimbraAccount.AccountZWC().EmailAddress + "</name>" +
        		"<password>" + ZimbraAccount.AccountZWC().Password + "</password>" +
        		"</EnableTwoFactorAuthRequest>");
        secret = ZimbraAccount.AccountZWC().soapSelectValue("//acct:EnableTwoFactorAuthResponse", "secret");
        tempToken = ZimbraAccount.AccountZWC().soapSelectValue("//acct:EnableTwoFactorAuthResponse", "authToken");

		totp = CommandLine.cmdExecOnServer(ZimbraAccount.AccountZWC().EmailAddress, secret);
		ZimbraAccount.AccountZWC().soapSend(
        		"<EnableTwoFactorAuthRequest xmlns='urn:zimbraAccount'>" +
        		"<name>" + ZimbraAccount.AccountZWC().EmailAddress + "</name>" +
                "<authToken verifyAccount='0'>" + tempToken + "</authToken>" +
                "<twoFactorCode>" + totp + "</twoFactorCode>" +
        		"</EnableTwoFactorAuthRequest>");
		// Login
		totp = CommandLine.cmdExecOnServer(ZimbraAccount.AccountZWC().EmailAddress, secret);
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC(), totp, true);
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");

		app.zPagePreferences.zNavigateTo();
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);
		app.zPagePreferences.sClick(Locators.zRevokeThisDeviceLink);
		DialogWarning dialog = (DialogWarning) new DialogWarning(DialogWarning.DialogWarningID.RevokeTrustedDevice, app, app.zPagePreferences);
		dialog.zClickButton(Button.B_REVOKE);
		//Verification
		ZAssert.assertTrue(app.zPagePreferences.zVerifyTrustedDeviceCount(0), "Verify trusted device count is decreased");
		ZAssert.assertTrue(app.zPagePreferences.zVerifyDisabledRevokeThisDeviceLink(), "Verify revoke this device link is disabled");
		
		totp = CommandLine.cmdExecOnServer(ZimbraAccount.AccountZWC().EmailAddress, secret);
		app.zPageMain.zLogout();
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC(), totp, false);

	    logger.info("Login requires totp after revoke");

	}
	
	@AfterMethod(groups={"always"})
	public void beforeMethod() throws HarnessException {
		zFreshLogin();
		logger.info(app.zGetActiveAccount().EmailAddress);
	}
	
	@AfterMethod(groups={"always"})
	public void afterMethod() throws HarnessException {
		zFreshLogin();
		logger.info(app.zGetActiveAccount().EmailAddress);
	}
	
}