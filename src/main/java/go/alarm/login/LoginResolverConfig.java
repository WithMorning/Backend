package go.alarm.login;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class LoginResolverConfig implements WebMvcConfigurer {

    private final LoginArgumentResolver loginArgumentResolver;

    /**
     * Spring은 요청이 들어올 때마다 등록된 모든 HandlerMethodArgumentResolver를 순회하면서 각 파라미터에 적용 가능한 리졸버를 찾아 실행합니다.
     * 이 과정에서 직접 추가한 LoginArgumentResolver도 고려되어, 적절한 상황에서 사용됩니다.
     * */
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver); // 직접 정의한 LoginArgumentResolver를 이 리스트에 추가함.
        // 추가된 LoginArgumentResolver는 Spring MVC가 컨트롤러 메소드의 파라미터를 분석할 때 사용함.
        // LoginArgumentResolver의 supportsParameter 메소드가 true를 반환하는 파라미터에 대해,
        // resolveArgument 메소드가 호출되어 파라미터 값을 결정하게 됨.
    }
}