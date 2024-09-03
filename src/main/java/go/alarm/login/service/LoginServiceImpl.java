package go.alarm.login.service;



import static go.alarm.global.response.ResponseCode.FAIL_TO_VALIDATE_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_REFRESH_TOKEN;

import go.alarm.group.domain.repository.UserGroupRepository;
import go.alarm.user.domain.User;
import go.alarm.user.domain.repository.UserRepository;
import go.alarm.global.response.exception.AuthException;
import go.alarm.login.domain.OauthProvider;
import go.alarm.login.domain.OauthProviders;
import go.alarm.login.domain.OauthUserInfo;
import go.alarm.login.domain.RefreshToken;
import go.alarm.login.domain.UserTokens;
import go.alarm.login.domain.repository.RefreshTokenRepository;
import go.alarm.login.infrastructure.BearerAuthorizationExtractor;
import go.alarm.login.infrastructure.JwtProvider;
import go.alarm.login.presentation.LoginConverter;
import go.alarm.wakeupdayofweek.domain.repository.WakeUpDayOfWeekRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginServiceImpl implements LoginService{

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final WakeUpDayOfWeekRepository wakeUpDayOfWeekRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;


    @Override
    public UserTokens login(final String providerName, final String code) {
        OauthProvider provider = oauthProviders.mapping(providerName);
        OauthUserInfo oauthUserInfo = provider.getUserInfo(code);
        User user = findOrCreateUser(
            oauthUserInfo.getSocialLoginId(),
            oauthUserInfo.getEmail()
        );
        
        RefreshToken refreshTokenObject = refreshTokenRepository.findByUserId(user.getId());
        log.warn("서비스단(/login) refreshTokenObject >>" + refreshTokenObject);

        if (refreshTokenObject != null){ // 기존에 리프레시 토큰이 없을 때는 제외
            removeRefreshToken(refreshTokenObject.getRefreshToken()); // 기존에 존재하던 리프레시 토큰 제거
        }

        final UserTokens userTokens = jwtProvider.generateLoginToken(user.getId().toString());
        // UserTokens에서 엑세스, 리프레쉬 토큰 가져와서 유저 필드에 저장
        log.warn("서비스단(/login) 리프레시 토큰 >>" + userTokens.getRefreshToken());
        log.warn("서비스단(/login) 엑세스 토큰 >>" + userTokens.getAccessToken());

        RefreshToken savedRefreshToken = LoginConverter.toRefreshToken(
            userTokens.getRefreshToken(), user.getId());

        refreshTokenRepository.save(savedRefreshToken); // 이후 유저 ID와 리프레시토큰을 리프레시토큰 레포에 저장
        return userTokens;
    }

    private User findOrCreateUser(final String socialLoginId, final String email) {
        return userRepository.findBySocialLoginId(socialLoginId)
            .orElseGet(() -> createUser(socialLoginId, email));
    }

    private User createUser(final String socialLoginId, final String email) {
        User user = LoginConverter.toUser(socialLoginId, email);
        return userRepository.save(user);
    }

    @Override
    public String renewalAccessToken(final String refreshTokenRequest, final String authorizationHeader) {
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);
        log.warn("서비스단(/accesstoken)의 bearer 제거 후 엑세스 토큰 >> " + accessToken);
        // Authorization 헤더에서 액세스 토큰을 추출

        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)) {
            final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
            return jwtProvider.regenerateAccessToken(refreshToken.getUserId().toString());
            // 유효한 리프레시 토큰으로 새로운 액세스 토큰을 생성하여 반환
        }
        if (jwtProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)) {
            return accessToken;
            // 기존 액세스 토큰이 아직 유효하므로 그대로 반환
        }
        throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
        // 두 토큰 모두 유효하지 않은 경우
    }

    @Override
    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken); // 리프레시 토큰이 삭제되지 않고 남아있음. 이거 해결해야 함!!
    }

    @Override
    public void deleteAccount(final Long userId) {

        User user = userRepository.findById(userId).get();
        Long dayOfWeekId = user.getBedDayOfWeek().getId();

        userRepository.deleteByUserId(userId);
        wakeUpDayOfWeekRepository.deleteById(dayOfWeekId);
        userGroupRepository.deleteByUserId(userId);
        refreshTokenRepository.deleteByUserId(userId);

    }

}