package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

/**
 * Класс контроллер начальной страницы eb приложения
 */
@ThreadSafe
@Controller
public class IndexController {

    /**
     * Метод используется для отображения начальной страницы
     *
     * @return возвращает начальную страницу
     */
    @GetMapping({"/", "/index"})
    public String getIndex() {
        return "index";
    }
}
