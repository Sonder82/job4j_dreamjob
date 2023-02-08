package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Класс контроллер для работы с пользователями.
 */
@ThreadSafe
@Controller
@RequestMapping("/users")
public class UserController {

    /**
     * Поле {@link UserService} объект класса Сервиса для работы с пользователями
     */
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Метод служит для отображения страницы с регистрацией пользователя
     * @param model {@link Model}
     * @param session {@link HttpSession}
     * @return возвращает страницу с регистрацией
     */
    @GetMapping("/register")
    public String getRegistrationPage(Model model, HttpSession session) {
        User user = checkSession(session);
        model.addAttribute("user", user);
        return "users/register";
    }

    /**
     * Метод служит для передачи информации о пользователе при регистрации
     * @param model {@link Model}
     * @param user пользователь
     * @return при вводе данных пользователя с уже имеющимся email происходит перенаправление на
     * страницу ошибки. При успешной регистрации перенаправляется на начальную страницу.
     */
    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        Optional<User> savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с такой почтой уже существует");
            return "errors/404";
        }
        return "redirect:/index";
    }

    /**
     * Метод служит для отображения страницы ввода данных для входа в аккаунт.
     * @param model {@link Model}
     * @param session {@link HttpSession}
     * @return возвращает страницу с вводом данных
     */
    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session) {
        User user = checkSession(session);
        model.addAttribute("user", user);
        return "users/login";
    }

    /**
     * Метод служит для входа пользователя в свой аккаунт
     * @param model {@link Model}
     * @param user пользователь
     * @return при вводе данных пользователя с уже имеющимся email происходит перенаправление на
     * страницу ошибки. При успешной регистрации перенаправляется на страницу с вакансиями.
     */
    @PostMapping("/login")
    public String loginUser(Model model, @ModelAttribute User user, HttpServletRequest request) {
        Optional<User> userOptional = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Почта или пароль введены неверно");
            return "users/login";
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", userOptional.get());
        return "redirect:/vacancies";
    }

    /**
     * Метод используется для выхода пользователя из системы.
     * @param session {@link HttpSession}
     * @return возвращает отображение входа пользователя в аккаунт
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    /**
     * Метод позволяет привязать данные сессии {@link HttpSession} к клиенту {@link User}
     * @param session {@link HttpSession}
     * @return {@link User}
     */
    private static User checkSession(HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        return user;
    }
}

