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

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowDisplayMail;


public class MarkUnReadMail extends PrefGroupMailByMessageTest {

	
	public MarkUnReadMail() {
		logger.info("New "+ MarkUnReadMail.class.getCanonicalName());
		

	}
	



	@Test(	description = "Mark a message as unread by clicking on it, then using 'mu' hotkeys",
			groups = { "functional" })
	public void MarkUnReadMail_01() throws HarnessException {
		

		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m l='"+ inboxFolder.getId() +"' f=''>"
				+			"<content>"
				+				"From: foo@foo.com\n"
				+				"To: foo@foo.com \n"
				+				"Subject: "+ subject +"\n"
				+				"MIME-Version: 1.0 \n"
				+				"Content-Type: text/plain; charset=utf-8 \n"
				+				"Content-Transfer-Encoding: 7bit\n"
				+				"\n"
				+				"simple text string in the body\n"
				+			"</content>"
				+      	"</m>"
				+	"</AddMsgRequest>");

		// Refresh current view
		app.zPageMail.zVerifyMailExists(subject);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			window.zKeyboardShortcut(Shortcut.S_MAIL_MARKUNREAD);

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}


		// Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringContains(mail.getFlags(), "u", "Verify the message is marked unread in the server");
		
	}

	@Test(	description = "Mark a message as unread by action menu -> mark read",
			groups = { "functional" })
	public void MarkUnReadMail_02() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();

		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m l='"+ inboxFolder.getId() +"' f=''>"
				+			"<content>"
				+				"From: foo@foo.com\n"
				+				"To: foo@foo.com \n"
				+				"Subject: "+ subject +"\n"
				+				"MIME-Version: 1.0 \n"
				+				"Content-Type: text/plain; charset=utf-8 \n"
				+				"Content-Transfer-Encoding: 7bit\n"
				+				"\n"
				+				"simple text string in the body\n"
				+			"</content>"
				+      	"</m>"
				+	"</AddMsgRequest>");

		
		// Refresh current view
		app.zPageMail.zVerifyMailExists(subject);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			window.zToolbarPressPulldown(Button.B_ACTIONS, Button.O_MARK_AS_UNREAD);

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}


		// Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringContains(mail.getFlags(), "u", "Verify the message is marked read in the server");

	}
		
	@Bugs(ids = "64133")
	@Test(	description = "Verify mark unread is enabled in the separate window",
			groups = { "functional" })
	public void MarkUnReadMail_03() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();

		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m l='"+ inboxFolder.getId() +"' f=''>"
				+			"<content>"
				+				"From: foo@foo.com\n"
				+				"To: foo@foo.com \n"
				+				"Subject: "+ subject +"\n"
				+				"MIME-Version: 1.0 \n"
				+				"Content-Type: text/plain; charset=utf-8 \n"
				+				"Content-Transfer-Encoding: 7bit\n"
				+				"\n"
				+				"simple text string in the body\n"
				+			"</content>"
				+      	"</m>"
				+	"</AddMsgRequest>");

		
		// Refresh current view
		app.zPageMail.zVerifyMailExists(subject);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		SeparateWindowDisplayMail window = null;
		
		try {
			
			// Choose Actions -> Launch in Window
			window = (SeparateWindowDisplayMail)app.zPageMail.zToolbarPressPulldown(Button.B_ACTIONS, Button.B_LAUNCH_IN_SEPARATE_WINDOW);
			
			window.zSetWindowTitle(subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			// Verify that the icon is not disabled
			window. zToolbarPressButton(Button.B_ACTIONS);
			SleepUtil.sleep(10000);
			ZAssert.assertTrue(window.sIsElementPresent("css=tr[id$='__MARK_UNREAD'] td[id$='__MARK_UNREAD_title']"), "Verify that the icon is not disabled");

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}


	}
		


}
