package com.zimbra.qa.selenium.projects.octopus.core;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class CommonMethods {
	
	public CommonMethods() {}
	
	protected void MarkFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
	 account.soapSend
       ("<DocumentActionRequest xmlns='urn:zimbraMail'>"
		+ "<action id='" + fileId + "'  op='watch' /></DocumentActionRequest>");
	} 

	protected void UnMarkFileFavoriteViaSoap(ZimbraAccount account, String fileId)
	throws HarnessException {
	 account.soapSend
       ("<DocumentActionRequest xmlns='urn:zimbraMail'>"
		+ "<action id='" + fileId + "'  op='!watch' /></DocumentActionRequest>");
	} 

	// upload file
	protected String uploadFileViaSoap(ZimbraAccount account, String fileName) 
    throws HarnessException {

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
		SystemFolder.Briefcase);

		// Create file item
        String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/" + fileName;

        // Upload file to server through RestUtil
        String attachmentId = account.uploadFile(filePath);

        // Save uploaded file to the root folder through SOAP
         account.soapSend(
         "<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
		+ briefcaseRootFolder.getId() + "'>" + "<upload id='"
		+ attachmentId + "'/>" + "</doc></SaveDocumentRequest>");

        //return id
        return account.soapSelectValue(
		  "//mail:SaveDocumentResponse//mail:doc", "id");
    }

}