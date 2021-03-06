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
package com.zimbra.qa.selenium.projects.universal.tests.calendar.meetings.organizer.singleday.modify;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZDate;
import com.zimbra.qa.selenium.framework.util.ZTimeZone;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ConfigProperties;
import com.zimbra.qa.selenium.projects.universal.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.DialogConfirmModification;

public class CloseModifiedAppointment extends CalendarWorkWeekTest {
	
	public CloseModifiedAppointment() {
		logger.info("New " + CloseModifiedAppointment.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test( description = "Close modifying appt and take action from warning dialog : Save Changes and send updates", 
			groups = { "functional", "L2"})
	public void CloseModifiedAppointment_01() throws HarnessException {

		// Create appointment data 
		String tz, apptSubject, apptAttendee1,apptAttendee2;
		apptSubject = ConfigProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
        apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;
		
        // Absolute dates in UTC zone
        tz = ZTimeZone.TimeZoneEST.getID();
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
		
		// Create appointment 
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ConfigProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);

        // Remove attendee and close the appt
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        FormApptNew apptForm = new FormApptNew(app);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_CLOSE);
        
        DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(app, app.zPageCalendar);
        confirmClose.zClickButton(Button.B_SAVE_MODIFICATION);
        DialogSendUpdatetoAttendees sendUpdateDialog = (DialogSendUpdatetoAttendees) new DialogSendUpdatetoAttendees(app, app.zPageCalendar);
        sendUpdateDialog.zClickButton(Button.B_OK);

        // Verify if an attendee is removed from appt and attendee gets update
        AppointmentItem actual = AppointmentItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ apptSubject +")");
		ZAssert.assertEquals(actual.getSubject(), apptSubject, "Subject: Verify the appointment data");
		ZAssert.assertStringDoesNotContain(actual.getAttendees(), apptAttendee2, "Attendees: Verify the attendee data");
		
		// Verify attendee2 receives cancellation message
		ZimbraAccount.AccountB().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>subject:" + (char)34 + "Cancelled: " + apptSubject + (char)34 + "</query>"
			+	"</SearchRequest>");
		String id = ZimbraAccount.AccountB().soapSelectValue("//mail:m", "id");
		ZAssert.assertNotNull(id, "Verify attendee2 receives cancelled meeting message");
		
		// Verify appointment is deleted from attendee2's calendar
		AppointmentItem removedAttendee = AppointmentItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ apptSubject +")");
		ZAssert.assertNull(removedAttendee, "Verify meeting is deleted from attendee2's calendar");
		
	}

	@Test( description = "Close modifying appt and take action from warning dialog : Discard and close", 
			groups = { "functional", "L2"})
	public void CloseModifiedAppointment_02() throws HarnessException {

		// Creating object for appointment data
		AppointmentItem appt = new AppointmentItem();
		String tz, apptSubject, editApptSubject, apptAttendee1,apptAttendee2;
		tz = ZTimeZone.TimeZoneEST.getID();
		
		apptSubject = ConfigProperties.getUniqueString();
		editApptSubject = ConfigProperties.getUniqueString();
        apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
        apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;

		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 11, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ConfigProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
		
		// Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");
 
        // Open appointment & modify subject, remove attendee &  close it
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        FormApptNew apptForm = new FormApptNew(app);
        appt.setSubject(editApptSubject);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_CLOSE);
        
        DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(app, app.zPageCalendar);
        confirmClose.zClickButton(Button.B_DISCARD_CLOSE);
        confirmClose.zClickButton(Button.B_SAVE_MODIFICATION);
        SleepUtil.sleepSmall();
        
        // Verify 'Save Appointment' dialog is closed
        ZAssert.assertFalse(confirmClose.zIsActive(), "Verify 'Save Appointment' dialog is closed");
       
        // Verify new appt page has been closed
        ZAssert.assertFalse(apptForm.zVerifyNewApptTabRemainsOpened(), "Verify new appt page has been closed");
            
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");
 
        // Verify that appointment subject is not modified
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "name", apptSubject), true, "");
        
        // Verify meeting attendees remains as it is
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee2, "Attendees: Verify attendee1 is present in the meeting invite");
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee1, "Attendees: Verify attendee2 is present in the meeting invite");
	}
	
	@Test( description = "Close modifying appt and take action from warning dialog : Dont save But keep open", 
			groups = { "functional", "L2"})
	public void CloseModifiedAppointment_03() throws HarnessException {
		
		// Creating object for appointment data
		AppointmentItem appt = new AppointmentItem();
		String tz, apptSubject, apptAttendee1,apptAttendee2,editApptSubject;
		tz = ZTimeZone.TimeZoneEST.getID();
		
		apptSubject = ConfigProperties.getUniqueString();
		editApptSubject = ConfigProperties.getUniqueString();
		apptAttendee1 = ZimbraAccount.AccountA().EmailAddress;
        apptAttendee2 = ZimbraAccount.AccountB().EmailAddress;
		
        // Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 11, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.AccountA().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ConfigProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        
		// Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");

        String apptId = app.zGetActiveAccount().soapSelectValue("//mail:CreateAppointmentResponse", "apptId");

        // Try to remove attendee and press close button
        app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        FormApptNew apptForm = new FormApptNew(app);
        appt.setSubject(editApptSubject);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zToolbarPressButton(Button.B_CLOSE);
        
        DialogConfirmModification confirmClose = (DialogConfirmModification) new DialogConfirmModification(app, app.zPageCalendar);
        confirmClose.zClickButton(Button.B_DONTSAVE_KEEP_OPEN);
        confirmClose.zClickButton(Button.B_SAVE_MODIFICATION);    
        
        // Verify 'Save Appointment' dialog is closed
        ZAssert.assertFalse(confirmClose.zIsActive(), "Verify 'Save Appointment' dialog is closed");
       
        // Verify new appt page is still open
        ZAssert.assertTrue(apptForm.zVerifyNewApptTabRemainsOpened(), "Verify new appt page is still open");
            
        app.zGetActiveAccount().soapSend("<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ apptId +"'/>");
        AppointmentItem modifyAppt = AppointmentItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ apptSubject +")");
 
        // Verify that appointment subject is not modified
        ZAssert.assertEquals(app.zGetActiveAccount().soapMatch("//mail:GetAppointmentResponse//mail:comp", "name", apptSubject), true, "");
        
        // Verify meeting attendees remains as it is
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee2, "Attendees: Verify attendee1 is present in the meeting invite");
        ZAssert.assertStringContains(modifyAppt.getAttendees(), apptAttendee1, "Attendees: Verify attendee2 is present in the meeting invite");
        
	}
	
}