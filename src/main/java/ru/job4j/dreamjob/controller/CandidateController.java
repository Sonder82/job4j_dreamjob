package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

/**
 * Класс-контроллер для работы с кандидатами
 */
@ThreadSafe
@Controller
@RequestMapping("/candidates") /* Работать с кандидатами будем по URI /candidates/** */
public class CandidateController {

    /**
     * Поле {@link CandidateService} объект класса Сервис для работы с кандидатами
     */
    private final CandidateService candidateService;
    /**
     * Поле {@link CityService} объект класса Сервис для работы с городами
     */
    private final CityService cityService;

    public CandidateController(CandidateService candidateService, CityService cityService) {
        this.candidateService = candidateService;
        this.cityService = cityService;
    }

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
    public String getCreationPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
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
    public String create(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file, Model model) {
        try {
            candidateService.save(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
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
        model.addAttribute("cities", cityService.findAll());
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
    public String update(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file, Model model) {
        try {
            boolean isUpdated = candidateService.update(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdated) {
                model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
                return "errors/404";
            }
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
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
