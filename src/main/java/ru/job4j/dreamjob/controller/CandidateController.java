package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.SimpleCandidateService;

import java.util.Optional;

/**
 * Класс-контроллер для работы с кандидатами
 */
@Controller
@RequestMapping("/candidates") /* Работать с кандидатами будем по URI /candidates/** */
public class CandidateController {

    /**
     * Поле {@link CandidateService} - хранилище с кандидатами
     */
    private final CandidateService candidateService = SimpleCandidateService.getInstance();

    /**
     * Метод выполняет работу по представлению всех кандидатов в браузере
     *
     * @param model {@link Model}
     * @return возвращает отображение всех кандидатов
     */
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    /**
     * Метод выполняет работу по созданию отображения страницы для создания кандидата в браузере.
     *
     * @return возвращает отображение страницы с формой по созданию кандидата
     */
    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }

    /**
     * Метод выполняет работу по созданию нового кандидата.
     * Мы формируем отправку данных(то, что написали в форме создания кандидата) на сервер.
     *
     * @param candidate {@link Candidate}
     * @return используется ключевое слово redirect, которое сообщает Spring,
     * чтобы после выполнения метода create перейти к обработке ссылки /candidates GET,
     * то есть вывести таблицу со списком всех кандидатов.
     * Т.е. мы повторно зайдем в метод getAll() запросим данные и выведем их на странице
     */
    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate) {
        candidateService.save(candidate);
        return "redirect:/candidates";
    }

    /**
     * Метод извлекает кандидата из репозитория и возвращает на страницу
     *
     * @param model объект Model. Он используется Thymeleaf для поиска объектов, которые нужны отобразить на виде.
     * @param id id кандидата
     * @return строку с ошибкой или представление для редактирования вакансии
     */
    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Optional<Candidate> candidateOptional = candidateService.findById(id);
        if (candidateOptional.isEmpty()) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("candidate", candidateOptional.get());
        return "candidates/one";
    }

    /**
     * Метод производит обновление и если оно произошло,
     * то делает перенаправление на страницу со всеми кандидатами;
     *
     * @param candidate {@link Candidate}
     * @param model {@link Model}
     * @return возвращает сообщение с указанием ошибки или возвращает таблицу со списком всех кандидатов
     */
    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        boolean isUpdated = candidateService.update(candidate);
        if (!isUpdated) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    /**
     * Метод производит удаление и если оно произошло,
     * то делает перенаправление на страницу со всеми кандидатами
     *
     * @param model {@link Model}
     * @param id id кандидата
     * @return возвращает сообщение с указанием ошибки или возвращает таблицу со списком всех кандидатов
     */
    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = candidateService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}
