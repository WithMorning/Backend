package go.alarm.auth.domain;

import lombok.Getter;

@Getter
public class Accessor {

    private final Long userId;
    private final Authority authority;

    private Accessor(final Long userId, final Authority authority) {
        this.userId = userId;
        this.authority = authority;
    }

    public static Accessor member(final Long userId) {
        return new Accessor(userId, Authority.MEMBER);
    }

    public static Accessor admin(final Long userId) {
        return new Accessor(userId, Authority.ADMIN);
    }

    public static Accessor master(final Long userId) {
        return new Accessor(userId, Authority.MASTER);
    }


    public boolean isMember() {
        return Authority.MEMBER.equals(authority);
    }

    public boolean isAdmin() {
        return Authority.ADMIN.equals(authority) || Authority.MASTER.equals(authority);
    }

    public boolean isMaster() {
        return Authority.MASTER.equals(authority);
    }
}