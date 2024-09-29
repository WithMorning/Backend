package go.alarm.fcm.service;


import go.alarm.user.domain.User;

public interface FCMService {

    void sendAlarms();
    User sendAlarmsTest(Long userId);
    void sendBedTimeAlarms();

    void prick(Long senderId, Long receiverId);

}
