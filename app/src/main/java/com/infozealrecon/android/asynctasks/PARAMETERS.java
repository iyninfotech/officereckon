package com.infozealrecon.android.asynctasks;

public class PARAMETERS {

    public interface SHARED_PREFERENCE_KEYS {

        /* Shared Preference Title */
        public final String SHARED_PREFERENCE_TITLE = "SP_SHIVAM_GPS_APP";
        public final String KEY_HANDLER_MESSAGE = "handler_message";
    }

    public interface USER {
        public final String KEY_USER_ID = "user_id";
        public final String KEY_ORGANIZATION_ID = "organization_id";
        public final String KEY_ORGANIZATION_NAME = "organization_name";
        public final String KEY_USER_NAME = "user_name";
        public final String KEY_EMAIL = "email";
        public final String KEY_MOBILE = "mobile";
        public final String KEY_PASSWORD = "password";

    }

    public interface OTHERS {
        public final String TAG_APPOINTMENT_NAVIGATION_ID = "appointment_navigation_id";
        public final String TAG_ALERT_DIALOG = "AlarmDialog";
        public final String KEY_IS_FROM_VEHICLE_LISTING = "is_from_vehicle_listing";
        public final String KEY_SELECTED_VEHICLE_POSITION = "selected_vehicle_position";
        public final String KEY_SELECTED_VEHICLE_ID = "selected_vehicle_id";
        public final String KEY_SELECTED_VEHICLE_TYPE = "selected_vehicle_type";
    }

    public interface ALARM_KEYS {
        public final String ALARM_KEY = "key";
        public final String ALARM_PENDING_INTENT_ID = "pending_intent_id";
        public final String ALARM_IS_REMIND = "is_remind";
        public final String ALARM_IS_TIME_TO_LEAVE = "is_time_to_leave";
        public final String ALARM_REPEAT = "repeat";
        public final String ALARM_IS_START_TIME = "is_start_time";
    }

    public interface EVERNOTE {
        public final String IS_EVERNOTE_LOGIN = "is_evernote_login";
    }

    public interface STATES {
        public final String IS_LOGIN = "is_login";
    }

    public interface ACTIONBAR_TYPE {

        public final String ACTIONBAR_MAIN = "actionbar_main";
        public final String ACTIONBAR_SUB = "actionbar_sub";

    }

    public interface CALENDAR {
        public static String KEY_EVENT_SET = "event_set";
        public static String KEY_USE_24_HR_FORMAT = "twenty_four_hour_format";
        public static String KEY_NUM_DAYS = "number_of_days";
    }

    public interface DATABASE_PARAMS {
        public final String KEY_SET_0 = "0";
        public final String KEY_SET_1 = "1";
    }

    public interface WEB_SERVICE_RESPONSE {
        public final String KEY_WEB_API_STATUS = "status";
        public final String KEY_WEB_API_OK = "OK";
        public final String KEY_WEB_API_DATA = "data";
        public final String KEY_WEB_API_STATUS_0 = "0";
        public final String KEY_WEB_API_STATUS_1 = "1";
        public final String KEY_WEB_API_MESSAGE = "message";
    }

    public interface FILE_PARAMS {
    }

    public interface DATE_PARAMS {
        public final String KEY_DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
        public final String KEY_DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        public final String KEY_DATE_FORMAT_HH_MM_SS = "HH:mm:ss";
        //		public final String KEY_DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
        public final String KEY_DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
        public final String KEY_DATE_FORMAT_YYYY_MM_DD_HH_MM_12 = "dd MMM yyyy hh:mm aa";
        public final String KEY_DATE_FORMAT_DD_MMM_YYYY = "dd MMM yyyy";
        public final String KEY_DATE_FORMAT_DD_comma_MMM_YYYY = "dd, MMM yyyy";
        public final String KEY_DATE_FORMAT_DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm";
        public final String KEY_DATE_FORMAT_MMMM = "MMMM";
        public final String KEY_DATE_FORMAT_E_MMM_D = "E, MMM d";
        public final String KEY_DATE_FORMAT_EEEE_DD_MMM_YYYY = "EEEE, dd MMM yyyy";
        public final String KEY_DATE_FORMAT_EEEE_MMM_DD = "EEEE, MMM dd";
        public final String KEY_DATE_FORMAT_EEE_DD_MMM = "EEE, dd MMM";
        public final String KEY_DATE_FORMAT_MMMM_YYY = "MMMM yyyy";
        public final String KEY_DATE_FORMAT_YYYY_M_DD = "yyyy-M-dd";
        public final String KEY_DATE_FORMAT_EEEE_DD_MMM_YYYY_HH_MM = "EEEE, dd MMM yyyy HH:mm";
        public final String KEY_DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

        public final String KEY_DATE_FORMAT_YYYY_MM_DD_Z = "yyyy-mm-dd'T'HH:mm:ss'Z'";
        // 22:15:02
        public final String KEY_DATE_FORMAT_HH_MM_24 = "hh:mm:ss";
        public final String KEY_DATE_FORMAT_HH_MM_12 = "hh:mm aa";

    }

    public interface APPOINTMENT_PARAMS {
        public final String KEY_SELECTED_INVITES = "selected_invites";
    }

    public interface INTENT_PARAMS {

    }

    public interface POSITION_PARAMS {
        public final String KEY_CURRNET_TAB_POSITION = "current_tab_position";
    }

    public interface FIXED_VALUES {

        public final int DEFAULT_SLIDE_SHOW_DURATION = 5;
        public final int DEFAULT_SLIDE_SHOW_PLAY_MODE = 1;
    }

    public interface GCM_DATA {
        public final String DEVICE_TYPE = "2";
        public final String KEY_SENDER_ID = "SENDER_ID";
        public final String DEVICE_ID = "device_id";
        public final String SENDER_ID = "719279746043";
        public static final String GCM_TOKEN = "gcmToken";

        //  Project ID: esoteric-crow-90308 Project Number:

    }

    public interface SETTINGS {
        public final String MILEAGE_UNIT = "mileage_unit";
        public final String SAT_NAV_APP_SELECTION = "sat_nav_app_selection";
        public final String USER_HOME_LOCATION_NAME = "users_home_location_name";
        public final String USER_HOME_LOCATION_LATLONG = "users_home_location_latlong";
        public final String USER_WORK_LOCATION_NAME = "users_work_location_name";
        public final String USER_WORK_LOCATION_LATLONG = "users_work_location_latlong";

        public final String USER_HOME_LOCATION_NAME_TEXT = "users_home_location_name_text";
        public final String USER_WORK_LOCATION_NAME_TEXT = "users_work_location_name_text";
    }

    public interface NETWORKS_PARAMS {
        public final String KEY_RESPONSE_SEPARATOR = "response_separator";
        public final String KEY_POST = "POST";
        public final String KEY_GET = "GET";
        public final String KEY_REQUESTED_JSON_OBJECT = "api_request_by_json_object";
        public final String KEY_REQUESTED_NAME_VALUE_PAIR = "api_request_by_name_value_pair";
    }

    public interface WEBSERVICES_PARAMS {
        public final String KEY_START_TAG = "tag_webservice_";

        public final String KEY_WEB_SERVICE_TAG_LOGIN = KEY_START_TAG + "login";
    }

    public interface FRAGMENTS_TAB_POSITIONS {
        public final int POSITION_FRAGMENT_DAY = 0;
        public final int POSITION_FRAGMENT_WEEK = 1;
        public final int POSITION_FRAGMENT_HOME = 2;
        public final int POSITION_FRAGMENT_TASKS = 3;
        public final int POSITION_FRAGMENT_APPOINTMENT = 4;
    }

    public interface FRAGMENTS_LISTVIEW_POSITIONS {
        public final int POSITION_FRAGMENT_HOME = 0;
        public final int POSITION_FRAGMENT_MY_PROFILE = 1;
        public final int POSITION_FRAGMENT_INVITATION = 2;
        public final int POSITION_FRAGMENT_MILEAGE_TRACKER = 3;
        public final int POSITION_FRAGMENT_CHANGE_PASSWORD = 4;
        public final int POSITION_FRAGMENT_SETTINGS = 5;
        public final int POSITION_FRAGMENT_LOGOUT = 6;
    }

}

