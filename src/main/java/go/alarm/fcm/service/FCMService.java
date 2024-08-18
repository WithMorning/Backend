package go.alarm.fcm.service;


import go.alarm.user.domain.User;
import java.util.List;

public interface FCMService {

    public void sendAlarms();
    public List<User> sendAlarmsTest();

}
