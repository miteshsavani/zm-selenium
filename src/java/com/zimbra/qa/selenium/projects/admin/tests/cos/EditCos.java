package com.zimbra.qa.selenium.projects.admin.tests.cos;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormEditCos;

public class EditCos extends AdminCommonTest {
	public EditCos() {
		logger.info("New "+ EditCos.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageCOS;

	}

	/**
	 * Testcase : Edit account name  - Manage Account View
	 * Steps :k
	 * 1. Create an cos using SOAP.
	 * 2. Go to Manage Cos View
	 * 3. Select an Cos.
	 * 4. Edit an cos using edit button in Gear box menu.
	 * 5. Verify cos is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Cos name  - Manage Cos View",
			groups = { "smoke" })
			public void EditCos_01() throws HarnessException {

		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(cosName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on cos to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, cos.getName());


		// Click on Edit button
		FormEditCos form = (FormEditCos) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditCos.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedCos_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the cos exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetCosRequest xmlns='urn:zimbraAdmin'>" +
		                     "<cos by='name'>"+cos.getName()+"</cos>"+
		                   "</GetCosRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCosResponse/admin:cos", 1);
		ZAssert.assertNull(response, "Verify the cos is edited successfully");	}
	
	/**
	 * Testcase : Edit cos name -- right click 
	 * Steps :
	 * 1. Create an cos using SOAP.
	 * 2. Edit the cos name using UI Right Click.
	 * 3. Verify cos name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit cos name -- right click",
			groups = { "smoke" })
			public void EditCOS_02() throws HarnessException {
		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");

		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(cosName);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on cos to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, cos.getName());

		// Click on Edit button
		FormEditCos form = (FormEditCos) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditCos.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedCos_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the cos exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetCosRequest xmlns='urn:zimbraAdmin'>" +
		                     "<cos by='name'>"+cos.getName()+"</cos>"+
		                   "</GetCosRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCosResponse/admin:cos", 1);
		ZAssert.assertNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=74487");
	}

}