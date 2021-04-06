package com.example.bigowlapp.repository;

/**
 * Description: A class that contains all the end points (repository) in the system
 * Purpose: For classes that need the end points (repository)
 * Reason: To de-couple the uses of each repository to a single point.
 */
public class RepositoryFacade {
    private AuthRepository authRepository;
    private UserRepository userRepository;
    private ScheduleRepository scheduleRepository;
    private GroupRepository groupRepository;
    private SmsInvitationRepository smsInvitationRepository;

    private static final RepositoryFacade instance = new RepositoryFacade();

    public static RepositoryFacade getInstance() {
        return instance;
    }

    private RepositoryFacade() {
        // Repositories are only initialized when needed by a ViewModel
    }

    public String getCurrentUserUid() {
        return getAuthRepository().getCurrentUser().getUid();
    }

    public AuthRepository getAuthRepository() {
        if (authRepository == null) {
            authRepository = new AuthRepository();
        }
        return authRepository;
    }

    public UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    public ScheduleRepository getScheduleRepository() {
        if (scheduleRepository == null) {
            scheduleRepository = new ScheduleRepository();
        }
        return scheduleRepository;
    }

    public GroupRepository getGroupRepository() {
        if (groupRepository == null) {
            groupRepository = new GroupRepository();
        }
        return groupRepository;
    }

    public NotificationRepository getCurrentUserNotificationRepository() {
        return new NotificationRepository(getCurrentUserUid());
    }

    public NotificationRepository getNotificationRepository(String userUid) {
        return new NotificationRepository(userUid);
    }

    public SmsInvitationRepository getSmsInvitationRepository(){
        if(smsInvitationRepository == null){
            smsInvitationRepository = new SmsInvitationRepository();
        }
        return smsInvitationRepository;
    }
}
