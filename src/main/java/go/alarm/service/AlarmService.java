package go.alarm.service;


import go.alarm.domain.entity.User;
import java.util.List;

public interface AlarmService {

    public void sendAlarms();
    public List<User> sendAlarmsTest();

}
