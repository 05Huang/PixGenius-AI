package cn.hx.pix.genius.core.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hx.pix.genius.core.dto.common.Result;
import cn.hx.pix.genius.core.dto.common.ResultCode;
import cn.hx.pix.genius.core.pojo.User;
import cn.hx.pix.genius.core.utils.JwtUtils;
import cn.hx.pix.genius.core.utils.UserUtils;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

public class UserInterceptor implements HandlerInterceptor {

    /**
     * 处理Controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 业务逻辑：
        // 排错不需要解析用户的地址  ——比如登录地址直接放行
        String uri = request.getRequestURI();
        // 白名单：登录接口、密码验证接口、错误页面
        if(uri.contains("/api/1.0/user/login") 
            || uri.contains("/api/1.0/auth/verify-password")
            || uri.contains("/error")) {
            return true;
        }
        // 去请求中拿token           ——header中token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StrUtil.isEmpty(token)) {
            writeAuthorizationedFailed(response);
            return false;
        }
        User user = JwtUtils.getToekn(token);
        if(user==null){
            writeAuthorizationedFailed(response);
            return false;
        }
        UserUtils.saveUser(user);
//        // 解析用户                 ——JwtUtils
//        boolean isOk = JwtUtils.verifyToekn(token);
//        if(!isOk){
//            writeAuthorizationedFailed(response);
//            return false;
//        }
//        // 存放到上下文中              ——UserUtils
//        User user = JwtUtils.getToekn(token);
//        UserUtils.saveUser(user);
        return true;
    }

    /**
     * 封装未授权的返回结果信息
     * @param response
     */
    private void writeAuthorizationedFailed(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Result<Object> failed = Result.failed(ResultCode.ACCESS_UNAUTHORIZED);
        String body = JSON.toJSONString(failed);
        try {
            PrintWriter writer = response.getWriter();
            writer.write(body);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    /**
//     * 处理Controller之后
//     * @param request
//     * @param response
//     * @param handler
//     * @param modelAndView
//     * @throws Exception
//     */
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
//    }

    /**
     * 做完所有的mvc处理之后，返回给前端数据之前，所要做的事情
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserUtils.removeUser();
    }

}
