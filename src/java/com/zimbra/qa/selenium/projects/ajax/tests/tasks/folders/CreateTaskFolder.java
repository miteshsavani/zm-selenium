/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014, 2015, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.tasks.folders;

import java.util.HashMap;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.DialogCreateTaskFolder;

public class CreateTaskFolder extends AjaxCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	

	@SuppressWarnings("serial")
	public CreateTaskFolder() {
		logger.info("New " + CreateTaskFolder.class.getCanonicalName());

		// test starts at the task tab
		super.startingPage = app.zPageTasks;
		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
			put("zimbraPrefShowSelectionCheckbox", "TRUE");
		}};
	}	

	@Test( description = "Create a new tasklist by clicking 'Create a new task' on task folders tree", 
			groups = { "sanity", "L0"})
	
	public void CreateTaskFolder_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem taskFolder = FolderItem.importFromSOAP(account,SystemFolder.Tasks);
		
		_folderName = "taskfolder" + ConfigProperties.getUniqueString();
		
		//Create folder
		//DialogCreateTaskFolder createTaskFolderDialog =(DialogCreateTaskFolder)app.zTreeTasks.zPressButton(Button.B_TREE_NEWTASKLIST);
		DialogCreateTaskFolder createTaskFolderDialog =(DialogCreateTaskFolder)app.zTreeTasks.zPressPulldown(Button.B_TREE_FOLDERS_OPTIONS, Button.B_TREE_NEWTASKLIST);
		
		createTaskFolderDialog.zEnterFolderName(_folderName);
		createTaskFolderDialog.zClickButton(Button.B_OK);
		
		_folderIsCreated = true;
		
		SleepUtil.sleepVerySmall();
		
		// refresh task page
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
				
		// Make sure the task folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),_folderName);
		ZAssert.assertNotNull(folder, "Verify task folder is created");
		ZAssert.assertEquals(folder.getName(), _folderName,"Verify the server and client folder names match");
	}
	
	@Test( description = "Create a new tasklist using tasks app New -> New Task Folder", 
			groups = { "functional", "L2"})
	public void CreateTaskFolder_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem taskFolder = FolderItem.importFromSOAP(account,SystemFolder.Tasks);
		
		_folderName = "taskfolder" + ConfigProperties.getUniqueString();
		
		//Create folder
		DialogCreateTaskFolder createTaskFolderDialog =(DialogCreateTaskFolder)app.zPageTasks.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_TASK_FOLDER);
		
		createTaskFolderDialog.zEnterFolderName(_folderName);
		createTaskFolderDialog.zClickButton(Button.B_OK);
		
		_folderIsCreated = true;
		
		SleepUtil.sleepVerySmall();
		
		// refresh task page
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);
				
		// Make sure the task folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),_folderName);
		ZAssert.assertNotNull(folder, "Verify task folder is created");
		ZAssert.assertEquals(folder.getName(), _folderName,"Verify the server and client folder names match");
	}

	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
		if (_folderIsCreated) {
			try {
				app.zPageTasks.zNavigateTo();
				// Delete it from Email Server
				FolderItem
						.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
			} catch (Exception e) {
				logger.warn("Failed while removing the folder.", e);
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
		}
	}
}
