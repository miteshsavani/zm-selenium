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
import com.zimbra.qa.selenium.projects.universal.ui.Dialog2FactorAuthEnable;
import com.zimbra.qa.selenium.projects.universal.ui.preferences.TreePreferences.TreeItem;

public class ZimbraFeatureTwoFactorAuthRequired extends UniversalCommonTest {

	public ZimbraFeatureTwoFactorAuthRequired() {
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 2485388299568483623L;
			{				
		 		put("zimbraFeatureTwoFactorAuthAvailable", "TRUE");
			}
		};
	}

	@Test( description = "Verify that after setting zimbraFeatureTwoFactorAuthRequired to true, user is not allowed to acces mailbox till 2fa setup is completed ",
			priority=4, groups = { "sanity", "L0", "network" } )
	
	public void ZimbraFeatureTwoFactorAuthRequired_01() throws HarnessException {

		logger.info("Click on 'Setup two-step authentication'");

		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
			+		"<id>"+ app.zGetActiveAccount().ZimbraId +"</id>"
			+		"<a n='zimbraFeatureTwoFactorAuthRequired'>TRUE</a>"
			+	"</ModifyAccountRequest>");
		
		app.zPageMain.zLogout();
		app.zPageLogin.zSetupAfterLogin(ZimbraAccount.AccountZWC());
		
		Dialog2FactorAuthEnable dialog = (Dialog2FactorAuthEnable) new Dialog2FactorAuthEnable(app, app.zPageLogin);
		dialog.zClickButton(Button.B_BEGIN_SETUP);
		SleepUtil.sleepMedium();

		dialog.zSetUserPassword(ZimbraAccount.AccountZWC().Password);
		SleepUtil.sleepSmall();
		dialog.zClickButton(Button.B_NEXT);
		SleepUtil.sleepMedium();
		
		dialog.zClickButton(Button.B_NEXT);
		String secretKey = dialog.zGetSecretKey();
		String totp = CommandLine.cmdExecOnServer(ZimbraAccount.AccountZWC().EmailAddress, secretKey);
		
		dialog.zClickButton(Button.B_NEXT);
		SleepUtil.sleepMedium();
		dialog.zSetTotpCode(totp);
		SleepUtil.sleepMedium();
		
		dialog.zClickButton(Button.B_NEXT);
		SleepUtil.sleepMedium();
		
		dialog.zClickButton(Button.B_FINISH);
		SleepUtil.sleepVeryLong();

		//-- VERIFICATION
		this.app.zPagePreferences.zNavigateTo();
		this.app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);
        ZAssert.assertFalse(app.zPagePreferences.zVerifyDisable2FALink(), "Verify Disable link is not present");
        ZAssert.assertFalse(app.zPagePreferences.zVerifySetup2FALink(), "Verify set-up two factor auth link is also not present");
        
	}

	@AfterMethod(groups={"always"})
	public void afterMethod() throws HarnessException {
		zFreshLogin();
		logger.info(app.zGetActiveAccount().EmailAddress);
	}
}