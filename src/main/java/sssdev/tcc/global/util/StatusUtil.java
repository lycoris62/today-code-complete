package sssdev.tcc.global.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@Component
public class StatusUtil {

    private final String LOGIN_USER = "login_user";

    public LoginUser getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ServiceException(ErrorCode.NOT_LOGIN);
        }
        return (LoginUser) session.getAttribute(LOGIN_USER);
    }
}
