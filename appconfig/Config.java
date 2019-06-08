package app.zerobugz.fcms.ims.appconfig;

/**
 * Created by Mohanraj on 1/07/2018.
 */
public class Config {
    //URL to our login.php file
    public static final String LOGIN_URL = "http://api.skooldesk.co.in/api/sysauth/token";

    //URL to our Reset Password Change within login
    public static final String PASSWORD_RESET_URL = "http://api.skooldesk.co.in/api/LoginRelated/PasswordReset";

    //URL to get our student details from server
    public static final String GET_STUDENT_DETAILS_URL = "http://api.skooldesk.co.in/api/LoginRelated/StudentDetailsList";

    //URL to get our student Messages from server
    public static final String GET_MESSAGE_URL = "http://api.skooldesk.co.in/api/Notification/NotificationDetails";

    //URL to get our student Homework from server
    public static final String GET_HOMEWORK_URL = "http://api.skooldesk.co.in/api/Homework/HomeworkDetails";

    //URL to get our student Attendance from server
    public static final String GET_ATTENDANCE_URL = "http://api.skooldesk.co.in/api/Attendance/AttendanceDetails";

    //URL to get our student Timetable from server
    public static final String GET_TIMETABLE_URL = "http://api.skooldesk.co.in/api/TimeTable/TimeTableDetails";

    //URL to get our student Marks from server
    public static final String GET_MARKS_URL = "http://api.skooldesk.co.in/api/Mark/MarkDetails";

    //URL to get our student All Event from server
    public static final String GET_ALL_EVENT_URL = "http://api.skooldesk.co.in/api/Gallery/OverAllGalleryList";

    //URL to get Image & Video Event Based from server
    public static final String GET_SPECIFIC_EVENT_URL = "http://api.skooldesk.co.in/api/Gallery/GalleryList";

    //URL to get our student Specific Event from server
    public static final String GET_STUDENT_BASED_EVENT_URL = "http://api.skooldesk.co.in/api/Gallery/StudentwiseGalleryList";

    //Insert Device Id to Server
    public static final String INSERT_DEVICE_ID_URL = "http://api.skooldesk.co.in/api/LoginRelated/InsertDeviceId";

    //Delete Device Id to Server
    public static final String DELETE_DEVICE_ID_URL = "http://api.skooldesk.co.in/api/LoginRelated/DeleteDeviceId";


    // Set Manually Your Branch Sys Id Here
    public static final String BRANCH_SYS_ID_SHARED_PREF = "6";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the mobile of current logged in user
    public static final String MOBILE_SHARED_PREF = "mobile";

    //We will use this to store the string in sharedpreference to track user is token id or not
    public static final String TOKEN_SHARED_PREF = "token";

    //This would be used to store User(Parent) Details
    public static final String USER_SYS_ID_SHARED_PREF = "user_sys_id";
    public static final String USER_NAME_SHARED_PREF = "user_name";
    public static final String BRANCH_NAME_SHARED_PREF = "branch_name";

    //This would be used to store Student Details
    public static final String STUDENT_SYS_ID_SHARED_PREF = "student_sys_id";
    public static final String STUDENT_NAME_SHARED_PREF = "student_name";
    public static final String STUDENT_IMAGE_SHARED_PREF = "student_image";
    public static final String CLASS_NAME_SHARED_PREF = "class_name";
    public static final String SECTION_NAME_SHARED_PREF = "section_name";
    public static final String GROUP_NAME_SHARED_PREF = "group_name";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    //Firebase Config Section
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //Topic Subscribe Status
    public static final String SUBSCRIBE_STATUS = "false";

    //Send Registration Id to Server Status
    public static final String SEND_REGISTRATION_TO_SERVER = "false";

    //FCM REGISTRATION ID
    public static final String FCM_REGISTRATION_ID = "registerid";

    //Topic Name Subscribe
    public static final String SUBSCRIBE_CLASS_NAME = "class_name";
    public static final String SUBSCRIBE_CLASS_SECTION_NAME = "class_section_name";
    public static final String SUBSCRIBE_CLASS_GROUP_NAME = "class_group_name";
    public static final String SUBSCRIBE_CLASS_SECTION_GROUP_NAME = "class_section_group_name";

    //Youtube API Key
    public static final String YOUTUBE_API_KEY = "AIzaSyBJbHy1YxGtwoRriQwEkqOFAvbA4EG_8og";

}
