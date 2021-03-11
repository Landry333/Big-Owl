package com.example.bigowlapp.model;

public class Field {

    private Field() {
        // This is a constants class, and should not be instantiated
    }

    public static class Group extends Field {
        public static final String NAME = "name";
        public static final String SUPERVISOR_ID = "supervisorId";
        public static final String MEMBER_ID_LIST = "memberIdList";
    }

    public static class Notification extends Field {
        public static final String TYPE = "type";
        public static final String TIME = "time";
    }

    public static class Schedule extends Field {
        public static final String TITLE = "title";
        public static final String EVENT = "event";
        public static final String GROUP_UID = "groupUid";
        public static final String GROUP_SUPERVISOR_UID = "groupSupervisorUid";
        public static final String MEMBER_LIST = "memberList";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String LOCATION = "location";
        public static final String USER_SCHEDULE_RESPONSE_MAP = "userScheduleResponseMap";
    }

    public static class ScheduleRequest extends Notification {
        public static final String SENDER_UID = "senderUid";
        // TODO: should probably pull up this field as its relevant to all Notifications
        public static final String RECEIVER_UID = "receiverUid";
        public static final String GROUP_UID = "groupUid";
        // TODO: should probably pull up this field as its relevant to all Notifications
        public static final String TIME_READ = "timeRead";
        public static final String SENDER_RESPONSE = "senderResponse";
    }

    public static class SupervisionRequest extends Notification {
        public static final String SENDER_UID = "senderUid";
        public static final String RECEIVER_UID = "receiverUid";
        public static final String GROUP_UID = "groupUid";
        public static final String RESPONSE = "response";
        public static final String TIME_RESPONSE = "timeResponse";
    }

    public static class User extends Field {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String EMAIL = "email";
        public static final String PROFILE_IMAGE = "profileImage";
        public static final String MEMBER_GROUP_ID_LIST = "memberGroupIdList";
    }
}
