package go.alarm.login.service;



import static go.alarm.global.response.ResponseCode.FAIL_TO_VALIDATE_TOKEN;
import static go.alarm.global.response.ResponseCode.INVALID_REFRESH_TOKEN;

import go.alarm.group.domain.repository.UserGroupRepository;
import go.alarm.login.domain.AppleRefreshToken;
import go.alarm.login.domain.repository.AppleRefreshTokenRepository;
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
import go.alarm.wakeupdayofweek.domain.WakeUpDayOfWeek;
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
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;
    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;


    @Override
    public UserTokens login(final String providerName, final String identityToken, final String code) {
        OauthProvider provider = oauthProviders.mapping(providerName);
        OauthUserInfo oauthUserInfo = provider.getUserInfo(identityToken);
        log.warn("oauthUserInfo.getEmail() >>>> " + oauthUserInfo.getEmail());

        // 이 부분에 authorization code를 통해 리프레시 토큰을 저장하는 코드 추가해야 함!!!!
        // 애플 회원탈퇴를 할 때 리프레시 토큰을 넘겨줘야 하기 때문
        /**
         * 이 부분에 authorization code를 통해 만료 기간이 없는 애플 refresh token을 받아서 우리서버 db에 저장해놓는다.
         * iOS에서 회원탈퇴를 요청하면 우리서버에서는 유저정보를지우고 이때 저장되어있던 애플 refresh token을 가지고
         * 애플회원탈퇴 API를 호출하고 iOS에게 회원탈퇴가 완료되었다고 알려준다.
         *
         * */
        User user = findOrCreateUser(
            oauthUserInfo.getSocialLoginId(),
            oauthUserInfo.getEmail()
        );

        // 애플 로그인인 경우 refresh token 처리
        if ("apple".equalsIgnoreCase(providerName) && code != null) {
            // authorization code로 애플 refresh token 발급 요청
            String appleRefreshToken = provider.getRefreshToken(code);

            // 기존 애플 refresh token이 있다면 제거
            AppleRefreshToken existingAppleToken = appleRefreshTokenRepository.findByUserId(user.getId());
            if (existingAppleToken != null) {
                appleRefreshTokenRepository.delete(existingAppleToken);
            }

            // 새로운 애플 refresh token 저장
            AppleRefreshToken newAppleToken = LoginConverter.toAppleRefreshToken(
                appleRefreshToken, user.getId());
            appleRefreshTokenRepository.save(newAppleToken);
        }
        
        RefreshToken refreshTokenObject = refreshTokenRepository.findByUserId(user.getId());

        if (refreshTokenObject != null){ // 기존에 리프레시 토큰이 없을 때는 제외
            removeRefreshToken(refreshTokenObject.getRefreshToken()); // 기존에 존재하던 리프레시 토큰 제거
        }

        // 앱 서버에서 자체적으로 토큰 엑세스, 리프레시 토큰 생성
        final UserTokens userTokens = jwtProvider.generateLoginToken(user.getId().toString());

        RefreshToken newRefreshToken = LoginConverter.toRefreshToken(
            userTokens.getRefreshToken(), user.getId());
        
        refreshTokenRepository.save(newRefreshToken); // 이후 유저 ID와 리프레시토큰을 리프레시토큰 레포에 저장
        
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
    public void deleteAccount(final Long userId, final String providerName) {
        User user = userRepository.findById(userId).get();
        OauthProvider provider = oauthProviders.mapping(providerName);

        AppleRefreshToken appleRefreshTokenObject = appleRefreshTokenRepository.findByUserId(userId);

        String appleRefreshToken = appleRefreshTokenObject.getRefreshToken();

        Boolean isRevokeToken = provider.revokeToken(userId, appleRefreshToken);

        if (isRevokeToken){
            appleRefreshTokenRepository.delete(appleRefreshTokenObject);
        }

        WakeUpDayOfWeek bedDayOfWeek = user.getBedDayOfWeek();
        if (bedDayOfWeek != null) {
            Long dayOfWeekId = bedDayOfWeek.getId();
            wakeUpDayOfWeekRepository.deleteById(dayOfWeekId);
        }

        userRepository.deleteById(userId);
        userGroupRepository.deleteByUserId(userId);
        refreshTokenRepository.deleteByUserId(userId);

    }

}