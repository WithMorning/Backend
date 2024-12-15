package go.alarm.fcm.service;


import go.alarm.group.domain.Group;
import go.alarm.user.domain.User;

public interface FCMService {

    void sendAlarms();
    User sendAlarmsTest(Long userId);
    void sendBedTimeAlarms();
    void prick(Long senderId, Long receiverId);
    void sendTriggerAlarms(Group group);

}
