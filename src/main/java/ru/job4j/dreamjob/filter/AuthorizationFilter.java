package ru.job4j.dreamjob.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Класс-фильтр для проверки наличия прав доступа у пользователя.
 */
@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {

    /**
     * В методы выполняем проверку обращается ли пользователь к общедоступным адресам.
     * Если да, то сразу пропускаем запрос.
     * Если пользователь обращается к адресам, требующим прав,
     * то выполняем проверку вошел ли пользователь в систему.
     * Если не вошел, то перебрасываем его на страницу входа.
     * Если залогинен, разрешаем дальнейшее выполнение запроса.
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param chain {@link FilterChain}
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (isAlwaysPermitted(uri)) {
            chain.doFilter(request, response);
            return;
        }
        var userLoggedIn = request.getSession().getAttribute("user") != null;
        if (!userLoggedIn) {
            var loginPageUrl = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Метод проверяет, обращается ли пользователь к общедоступным адресам.
      * @param uri адрес
     * @return boolean логику
     */
    private boolean isAlwaysPermitted(String uri) {
        return uri.startsWith("/users/register") || uri.startsWith("/users/login");
    }
}
