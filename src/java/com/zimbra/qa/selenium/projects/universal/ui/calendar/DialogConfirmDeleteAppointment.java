/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.universal.ui.calendar;
/**
 * 
 */


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.projects.universal.ui.DialogWarning;

/**
 * Represents a "Delete Appointment" dialog box,
 * for an appointment without attendees.
 * 
 * No new buttons on this dialog, just YES and NO
 * <p>
 */
public class DialogConfirmDeleteAppointment extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "CONFIRM_DIALOG";

	
	
	public DialogConfirmDeleteAppointment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogConfirmDeleteAppointment.class.getCanonicalName());
	}


}

