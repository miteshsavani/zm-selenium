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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.undo;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;


public class UndoMarkSpamMessage extends PrefGroupMailByMessageTest {

	
	public UndoMarkSpamMessage() {
		logger.info("New "+ UndoMarkSpamMessage.class.getCanonicalName());
		
		
		


		
	}
	
	@Test(	description = "Undo - Mark a message as spam, using 'Spam' toolbar button",
			groups = { "functional" })
	public void UndoMarkSpamMessage_01() throws HarnessException {
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		FolderItem junk = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Junk);
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
				
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" 
			+		"<m l='" + inbox.getId() + "'>"
			+			"<content>"
			+				"From: foo@foo.com\n" 
			+ 				"To: foo@foo.com \n"
			+				"Subject: " + subject + "\n" 
			+ 				"MIME-Version: 1.0 \n"
			+				"Content-Type: text/plain; charset=utf-8 \n"
			+				"Content-Transfer-Encoding: 7bit\n" 
			+				"\n"
			+				"content \n"
			+				"\n"
			+				"\n"
			+			"</content>"
			+		"</m>"
			+	"</AddMsgRequest>");
		
		MailItem message = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +") is:anywhere");
		ZAssert.assertNotNull(message, "Verify the message was created");
		

		
		
		// Refresh current view
		app.zPageMail.zVerifyMailExists(subject);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Click spam
		app.zPageMail.zToolbarPressButton(Button.B_RESPORTSPAM);

		
		// Get the mail item for the new message
		// Need 'is:anywhere' to include the spam folder
		MailItem spam = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:("+ subject +")");
		ZAssert.assertEquals(spam.dFolderId, junk.getId(), "Verify the message is in the spam folder");
				
		
		// Click undo
		Toaster toaster = app.zPageMain.zGetToaster();
		toaster.zClickUndo();
		
		
		// Get the mail item for the new message
		// Need 'is:anywhere' to include the spam folder
		MailItem undone = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:("+ subject +")");
		ZAssert.assertEquals(undone.dFolderId, inbox.getId(), "Verify the message is back in the inbox folder");

	}


}
