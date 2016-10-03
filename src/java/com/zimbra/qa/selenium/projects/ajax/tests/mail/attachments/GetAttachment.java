/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014, 2015, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AttachmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ConfigProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class GetAttachment extends PrefGroupMailByMessageTest {

	
	public GetAttachment() throws HarnessException {
		logger.info("New "+ GetAttachment.class.getCanonicalName());
		
	}
	
	
	@Test( description = "Receive a message with one attachment",
			groups = { "smoke" })
	public void GetAttachment_01() throws HarnessException {
		
		final String mimeFile = ConfigProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		final String attachmentname = "file.txt";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Refresh current view
		ZAssert.assertTrue(app.zPageMail.zVerifyMailExists(subject), "Verify message displayed in current view");

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		List<AttachmentItem> items = display.zListGetAttachments();
		ZAssert.assertEquals(items.size(), 1, "Verify one attachment is in the message");
		
		boolean found = false;
		for ( AttachmentItem item : items ) {
			if ( item.getAttachmentName().equals(attachmentname)) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the attachment appears in the list (by file name)");
		
	}

	@Test( description = "Receive a message with three attachments",
			groups = { "functional" })
	public void GetAttachment_02() throws HarnessException {
		
		final String mimeFile = ConfigProperties.getBaseDirectory() + "/data/public/mime/email05/mime02.txt";
		final String subject = "subject151111738";
		final String attachmentname1 = "file01.txt";
		final String attachmentname2 = "file02.txt";
		final String attachmentname3 = "file03.txt";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));

		// Refresh current view
		ZAssert.assertTrue(app.zPageMail.zVerifyMailExists(subject), "Verify message displayed in current view");

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		List<AttachmentItem> items = display.zListGetAttachments();
		ZAssert.assertEquals(items.size(), 3, "Verify three attachment in the message");
		
		// Verify each attachment by file name
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		for ( AttachmentItem item : items ) {
			if ( item.getAttachmentName().equals(attachmentname1)) {
				found1 = true;
				continue;
			}
			if ( item.getAttachmentName().equals(attachmentname2)) {
				found2 = true;
				continue;
			}
			if ( item.getAttachmentName().equals(attachmentname3)) {
				found3 = true;
				continue;
			}
		}
		ZAssert.assertTrue(found1, "Verify the attachments appear in the list (by file name)");
		ZAssert.assertTrue(found2, "Verify the attachments appear in the list (by file name)");
		ZAssert.assertTrue(found3, "Verify the attachments appear in the list (by file name)");
		
	}


}
