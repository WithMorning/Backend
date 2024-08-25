package go.alarm.auth;

import static go.alarm.global.response.ResponseCode.INVALID_AUTHORITY;

import go.alarm.auth.domain.Accessor;
import go.alarm.global.response.exception.AuthException;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @UserOnly 어노테이션이 붙은 메서드가 호출될 때마다 자동으로 권한 검사를 수행합니다.
 * 메서드의 인자 중 Accessor 타입이면서 isUser()가 true인 객체가 있는지 확인합니다.
 * 조건을 만족하는 객체가 없으면 AuthException을 발생시켜 권한이 없음을 알립니다.
 * */
@Aspect
@Component
public class UserOnlyChecker {

    @Before("@annotation(go.alarm.auth.UesrOnly)") // @MemberOnly 어노테이션이 붙은 메서드가 실행되기 전에 호출
    public void check(final JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()) // 메서드의 모든 인자를 스트림으로 변환
            .filter(Accessor.class::isInstance) // Accessor 타입의 인자만 필터링
            .map(Accessor.class::cast) // 필터링된 객체를 Accessor로 캐스팅(형 변환)
            .filter(Accessor::isUser)
            .findFirst()// 조건을 만족하는 첫 번째 Accessor 객체를 찾음
            .orElseThrow(() -> new AuthException(INVALID_AUTHORITY));
    }
}