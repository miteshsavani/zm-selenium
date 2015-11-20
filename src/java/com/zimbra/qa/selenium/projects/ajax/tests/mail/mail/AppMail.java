package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;

public class AppMail extends PrefGroupMailByMessageTest {

	int pollIntervalSeconds = 60;

	public AppMail() {
		logger.info("New " + AppMail.class.getCanonicalName());

		super.startingAccountPreferences.put("zimbraPrefMailPollingInterval",
				"" + pollIntervalSeconds);

	}

	@Test(	description = "?app=mail in url",
			groups = { "smoke" })
	public void AppMail_01() throws HarnessException {

		//Go to AB tab
		app.zPageContacts.zNavigateTo();				
		SleepUtil.sleepMedium();

		// Create the message data to be sent
				String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
				
				ZimbraAccount.AccountA().soapSend(
							"<SendMsgRequest xmlns='urn:zimbraMail'>" +
								"<m>" +
									"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
									"<e t='c' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
									"<su>"+ subject +"</su>" +
									"<mp ct='text/plain'>" +
										"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
									"</mp>" +
								"</m>" +
							"</SendMsgRequest>");

				// Get all the SOAP data for later verification
				MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Reload the application, with app=contacts query parameter
		ZimbraURI uri = new ZimbraURI(ZimbraURI.getBaseURI());
		uri.addQuery("app", "mail");
		app.zPageMail.sOpen(uri.toString());

		SleepUtil.sleepMedium();



		// Click Get Mail button
			app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

			// Select the message so that it shows in the reading pane
			DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);

			// The body could contain HTML, even though it is only displaying text (e.g. <br> may be present)
			// do a contains, rather than equals.
			ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), mail.dBodyText, "Verify the body matches");


	}
}