/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.mail;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class RedirectMessage extends PrefGroupMailByMessageTest {
	
	public RedirectMessage() {
		logger.info("New "+ RedirectMessage.class.getCanonicalName());
	}
	
	@Test(	description = "Redirect message, using 'Redirect' toolbar button - in separate window",
			groups = { "smoke" })
	public void RedirectMessage_01() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
	

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		
		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Click redirect
			SeparateWindowDialogRedirect dialog = (SeparateWindowDialogRedirect)window.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_REDIRECT);
			dialog.zFillField(SeparateWindowDialogRedirect.Field.To, ZimbraAccount.AccountB().EmailAddress);
			dialog.zClickButton(Button.B_OK);


		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}

		
		// Verify the redirected message is received
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the redirected message is received");
		ZAssert.assertEquals(received.dRedirectedFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the message shows as redirected from the test account");


	}


}
