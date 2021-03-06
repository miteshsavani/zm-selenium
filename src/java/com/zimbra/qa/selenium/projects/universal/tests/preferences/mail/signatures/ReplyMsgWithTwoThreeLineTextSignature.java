/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013, 2014, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.universal.tests.preferences.mail.signatures;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.SignatureItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ConfigProperties;
import com.zimbra.qa.selenium.projects.universal.core.UniversalCommonTest;
import com.zimbra.qa.selenium.projects.universal.ui.mail.FormMailNew;

public class ReplyMsgWithTwoThreeLineTextSignature extends UniversalCommonTest {

	@SuppressWarnings("serial")
	public ReplyMsgWithTwoThreeLineTextSignature() {
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefComposeFormat", "text");

			}
		};
	}

	/**
	 * Test case : Reply Msg with text signature and Verify signature through
	 * soap Create signature through soap Send message with text signature
	 * through soap Reply same message. Verify text signature in Replied msg
	 * through soap
	 * 
	 * @throws HarnessException
	 */

	@Bugs(ids = "45490")
	@Test(description = "Reply Msg with text signature with multiple line and Verify signature through soap", groups = {
			"functional", "L2" })
	public void ReplyMsgWithTextSignature_01() throws HarnessException {

		String sigName = "signame" + ConfigProperties.getUniqueString();
		String sigBody = "Signature" + ConfigProperties.getUniqueString() + "\n" + "sign line two" + "\n"
				+ "sign line three";

		String sigName1 = "signame" + ConfigProperties.getUniqueString();
		String sigBody1 = "Signature" + ConfigProperties.getUniqueString() + "\n" + "sign line two" + "\n"
				+ "sign line three    wordAfterFourSpaces";

		app.zGetActiveAccount()
				.soapSend("<CreateSignatureRequest xmlns='urn:zimbraAccount'>" + "<signature name='" + sigName + "' >"
						+ "<content type='text/plain'>" + sigBody + "</content>" + "</signature>"
						+ "</CreateSignatureRequest>");

		String SignatureId = ZimbraAccount.AccountZWC().soapSelectValue("//acct:CreateSignatureResponse/acct:signature",
				"id");

		app.zGetActiveAccount()
				.soapSend("<CreateSignatureRequest xmlns='urn:zimbraAccount'>" + "<signature name='" + sigName1 + "' >"
						+ "<content type='text/plain'>" + sigBody1 + "</content>" + "</signature>"
						+ "</CreateSignatureRequest>");

		String SignatureId1 = ZimbraAccount.AccountZWC()
				.soapSelectValue("//acct:CreateSignatureResponse/acct:signature", "id");

		app.zGetActiveAccount()
				.soapSend("<GetIdentitiesRequest xmlns='urn:zimbraAccount'>" + "</GetIdentitiesRequest>");

		String IdentityId = ZimbraAccount.AccountZWC().soapSelectValue("//acct:GetIdentitiesResponse/acct:identity",
				"id");

		app.zGetActiveAccount()
				.soapSend("<ModifyIdentityRequest xmlns='urn:zimbraAccount'>" + "<identity id='" + IdentityId + "'>"
						+ "<a name='zimbraPrefDefaultSignatureId'>" + SignatureId + "</a>"
						+ "<a name='zimbraPrefForwardReplySignatureId'>" + SignatureId1 + "</a>" + "</identity>"
						+ "</ModifyIdentityRequest>");

		// Refresh UI
		app.zPageMain.sRefresh();

		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		// Signature is created
		SignatureItem signature = SignatureItem.importFromSOAP(app.zGetActiveAccount(), sigName);
		ZAssert.assertEquals(signature.getName(), sigName, "verified Text Signature is created");

		String subject = "subject" + ConfigProperties.getUniqueString();

		// Send a message to the account(self)
		ZimbraAccount.AccountZWC()
				.soapSend("<SendMsgRequest xmlns='urn:zimbraMail'>" + "<m>" + "<e t='t' a='"
						+ app.zGetActiveAccount().EmailAddress + "'/>" + "<su>" + subject + "</su>"
						+ "<mp ct='text/plain'>" + "<content>content" + ConfigProperties.getUniqueString() + "\n\n"
						+ signature.dBodyText + "\n</content>" + "</mp>" + "</m>" + "</SendMsgRequest>");

		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountZWC(), "in:inbox subject:(" + subject + ")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inboxFolder);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLY);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");

		// verify signature is present
		ZAssert.assertStringContains(mailform.zGetPlainBodyText(), sigBody,
				"Verify compose signature is present in body");
		ZAssert.assertStringContains(mailform.zGetPlainBodyText(), sigBody1,
				"Verify reply signature is present in body");

		// Send the message
		mailform.zSubmit();

		ZimbraAccount.AccountZWC().soapSend("<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+ "<query>in:inbox subject:(" + mail.dSubject + ")</query>" + "</SearchRequest>");

		String id = ZimbraAccount.AccountZWC().soapSelectValue("//mail:SearchResponse/mail:m", "id");
		ZimbraAccount.AccountZWC()
				.soapSend("<GetMsgRequest xmlns='urn:zimbraMail'>" + "<m id='" + id + "' />" + "</GetMsgRequest>");
		Element getMsgResponse = ZimbraAccount.AccountZWC().soapSelectNode("//mail:GetMsgResponse", 1);
		MailItem received = MailItem.importFromSOAP(getMsgResponse);

		// Verify TO, Subject, Text Body,Text Signature for replied msg
		ZAssert.assertStringContains(received.dSubject, "Re", "Verify the subject field contains the 'Fwd' prefix");
		ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress,
				"Verify the from field is correct");
		ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountZWC().EmailAddress,
				"Verify the to field is correct");
		ZAssert.assertStringContains(received.dBodyText, mail.dBodyText, "Verify the body content is correct");
		ZAssert.assertStringContains(received.dBodyText, sigBody, "Verify the signature is correct");
		ZAssert.assertStringContains(received.dBodyText, sigBody1, "Verify the signature is correct");

	}
}
