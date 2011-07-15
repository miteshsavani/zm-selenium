package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class PageCalendar extends AbsTab {

	public static class Locators {
		public static final String NewButton = "css=td#zb__CLWW__NEW_MENU_title";
		public static final String RefreshButton = "css=td#CHECK_MAIL_left_icon>div[class='ImgRefresh']";
		public static final String ViewButton = "id=zb__CLD__VIEW_MENU_dropdown";
		public static final String ViewDayMenu = "css=div[id='zb__CLD__VIEW_MENU'] tr[id$='POPUP_DAY_VIEW']";
		public static final String ViewWorkWeekMenu = "css=tr#POPUP_WORK_WEEK_VIEW";
		public static final String ViewWeekMenu = "css=tr#POPUP_WEEK_VIEW";
		public static final String ViewMonthMenu = "css=tr#POPUP_MONTH_VIEW";
		public static final String ViewListMenu = "css=tr#POPUP_CAL_LIST_VIEW";
		public static final String ViewScheduleMenu = "css=tr#POPUP_SCHEDULE_VIEW";
	}

	public PageCalendar(AbsApplication application) {
		super(application);

		logger.info("new " + PageCalendar.class.getCanonicalName());
	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (button == Button.B_NEW) {

			// New button
			// 7.X version: locator =
			// "css=div[id^='ztb__CLD'] td[id$='zb__CLD__NEW_MENU_title']";
			locator = Locators.NewButton;

			// Create the page
			page = new FormApptNew(this.MyApplication);
			// FALL THROUGH

		} else if (button == Button.B_REFRESH) {
			locator = Locators.RefreshButton;
			page = null;
			// FALL THROUGH

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP
				&& button == Button.B_GETMAIL) {

			// Wait for the spinner image
			zWaitForDesktopLoadingSpinner(5000);
		}

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Button cannot be null!");

		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_LISTVIEW) {
			pulldownLocator = "id=zb__CLD__VIEW_MENU_left_icon";

			if (option == Button.O_LISTVIEW_DAY) {
				page = new ApptDayView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_WEEK) {
				page = new ApptWeekView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_WORKWEEK) {
				page = new ApptWorkWeekView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_SCHEDULE) {
				page = new ApptScheduleView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_LIST) {
				page = new ApptListView(this.MyApplication);
			} else if (option == Button.O_LISTVIEW_MONTH) {
				page = new ApptMonthView(this.MyApplication);
			}
		}

		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			if (ClientSessionFactory.session().currentBrowserName().contains(
					"IE")) {
				// IE
				sClickAt(pulldownLocator, "0,0");
			} else {
				// others
				zClickAt(pulldownLocator, "0,0");
			}

			zWaitForBusyOverlay();

			if (option != null) {

				// Make sure the locator exists
				if (option == Button.O_LISTVIEW_DAY) {
					optionLocator = "id=POPUP_DAY_VIEW";
				} else if (option == Button.O_LISTVIEW_WEEK) {
					optionLocator = "id=POPUP_WEEK_VIEW";
				} else if (option == Button.O_LISTVIEW_WORKWEEK) {
					optionLocator = "id=POPUP_WORK_WEEK_VIEW";
				} else if (option == Button.O_LISTVIEW_MONTH) {
					optionLocator = "id=POPUP_MONTH_VIEW";
				} else if (option == Button.O_LISTVIEW_LIST) {
					optionLocator = "id=POPUP_CAL_LIST_VIEW";
				} else if (option == Button.O_LISTVIEW_SCHEDULE) {
					optionLocator = "id=POPUP_SCHEDULE_VIEW";
				}	
				zClick(optionLocator);
				zWaitForBusyOverlay();

			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}

		}
		return page;

	}

	public Boolean zVerifyAppointmentExists(String apptSubject)
			throws HarnessException {
		logger.info(myPageName() + " zVerifyAppointmentExists(" + apptSubject
				+ ")");
		tracer.trace("Verify " + apptSubject + " appointment");
		if (apptSubject == null)
			throw new HarnessException("Appointment cannot be null!");

		// Default behavior variables
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		locator = "css=td.appt_name:contains('" + apptSubject + "')";
		SleepUtil.sleepMedium();
		boolean isExists = this.sIsElementPresent(locator);
		if (locator == null) {
			throw new HarnessException("locator was null for appointment "
					+ apptSubject);
		}

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			// Wait for the spinner image
			zWaitForDesktopLoadingSpinner(5000);
		}

		// If page was specified, make sure it is active
		if (page != null) {
			// This function (default) throws an exception if never active
			page.zWaitForActive();
		}
		SleepUtil.sleepMedium();
		return (isExists);
	}

	public AbsPage zClickAppointment(String apptSubject)
			throws HarnessException {
		logger.info(myPageName() + " zClickAppointment(" + apptSubject + ")");
		tracer.trace("Click the " + apptSubject + " appointment");
		if (apptSubject == null)
			throw new HarnessException("Appointment cannot be null!");

		// Default behavior variables
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		locator = "css=td.appt_name:contains('" + apptSubject + "')";
		SleepUtil.sleepMedium();
		this.sClickAt(locator, "");
		if (locator == null) {
			throw new HarnessException("locator was null for appointment "
					+ apptSubject);
		}

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			// Wait for the spinner image
			zWaitForDesktopLoadingSpinner(5000);
		}

		// If page was specified, make sure it is active
		if (page != null) {
			// This function (default) throws an exception if never active
			page.zWaitForActive();
		}
		SleepUtil.sleepMedium();
		return (page);
	}

	public AbsPage zDblClickAppointment(String apptSubject)
			throws HarnessException {
		logger
				.info(myPageName() + " zDblClickAppointment(" + apptSubject
						+ ")");
		tracer.trace("Double click the " + apptSubject + " appointment");
		if (apptSubject == null)
			throw new HarnessException("Appointment cannot be null!");

		// Default behavior variables
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		locator = "css=td.appt_name:contains('" + apptSubject + "')";
		SleepUtil.sleepMedium();
		page = new FormApptNew(this.MyApplication);
			
		this.sDoubleClick(locator);
		if (locator == null) {
			throw new HarnessException("locator was null for appointment "
					+ apptSubject);
		}

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			// Wait for the spinner image
			zWaitForDesktopLoadingSpinner(5000);
		}

		// If page was specified, make sure it is active
		if (page != null) {
			// This function (default) throws an exception if never active
			page.zWaitForActive();
		}
		SleepUtil.sleepMedium();
		return (page);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if (zIsActive()) {
			return;
		}

		// Make sure we are logged in
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to " + this.myPageName());

		this.zClick(PageMain.Locators.zAppbarCal);

		this.zWaitForBusyOverlay();

		zWaitForActive();

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		/**
		 * 8.0: <div id="ztb__CLD" style="position: absolute; overflow: visible; z-index: 300; left: 179px; top: 78px; width: 1280px; height: 26px;"
		 * class="ZToolbar ZWidget" parentid="z_shell">
		 */
		// If the "folders" tree is visible, then mail is active
		String locator = "css=div#ztb__CLD";

		boolean loaded = this.sIsElementPresent(locator);
		if (!loaded)
			return (false);

		boolean active = this.zIsVisiblePerPosition(locator, 178, 74);
		if (!active)
			return (false);

		// html body div#z_shell.DwtShell div#ztb__CLD.ZToolbar
		// Made it here. The page is active.
		return (true);

	}

}
