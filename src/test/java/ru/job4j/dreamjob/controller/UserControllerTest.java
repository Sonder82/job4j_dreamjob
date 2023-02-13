package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    private HttpSession httpSession;

    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void initService() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        httpSession = mock(HttpSession.class);
        httpServletRequest = mock(HttpServletRequest.class);
    }

    @Test
    void whenRequestGetRegistrationPageThenGetIt() {
        String view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenPostRegistrationThenDoItAndRedirectToStartPage() {
        var user = new User(1, "user1@mail", "user1", "qwerty");
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        String view = userController.register(model, user);
        User actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenPostRegistrationWithSameEmailThenRedirectToErrorPage() {
        var user = new User(1, "user1@mail", "user1", "qwerty");
        var expectedMessage = "Пользователь с такой почтой уже существует";
        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        String view = userController.register(model, user);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(expectedMessage).isEqualTo(actualExceptionMessage);
    }

    @Test
    void whenRequestGetLoginPageThenGetIt() {
        String view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenPostLoginThenDoItAndRedirectToVacanciesPage() {
        var user = new User(1, "user1@mail", "user1", "qwerty");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));
        when(httpServletRequest.getSession()).thenReturn(httpSession);

        var model = new ConcurrentModel();
        String view = userController.loginUser(model, user, httpServletRequest);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenPostLoginWithSameEmailThenRedirectToErrorPage() {
        var user = new User(1, "user1@mail", "user1", "qwerty");
        var expectedMessage = "Почта или пароль введены неверно";
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        String view = userController.loginUser(model, user, httpServletRequest);
        var actualExceptionMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(expectedMessage).isEqualTo(actualExceptionMessage);
    }

    @Test
    void whenRequestForLogout() {
        String view = userController.logout(httpSession);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}