package go.alarm.auth.domain;

import lombok.Getter;

/**
 * LoginArgumentResolver 클래스의 resolveArgument 메서드가 반환한 Accessor 객체가 컨트롤러의 파라미터로 전달됩니다.
 * 이러한 Accessor를 저장할 객체입니다.
 * */
@Getter
public class Accessor {

    private final Long userId;
    private final Authority authority;

    private Accessor(final Long userId, final Authority authority) {
        this.userId = userId;
        this.authority = authority;
    }

    public static Accessor user(final Long userId) {
        return new Accessor(userId, Authority.USER);
    }

    public static Accessor admin(final Long userId) {
        return new Accessor(userId, Authority.ADMIN);
    }

    public static Accessor master(final Long userId) {
        return new Accessor(userId, Authority.MASTER);
    }


    public boolean isUser() {
        return Authority.USER.equals(authority);
    }

    public boolean isAdmin() {
        return Authority.ADMIN.equals(authority) || Authority.MASTER.equals(authority);
    }

    public boolean isMaster() {
        return Authority.MASTER.equals(authority);
    }
}