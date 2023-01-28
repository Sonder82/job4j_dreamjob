package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;
import ru.job4j.dreamjob.service.SimpleVacancyService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.Optional;

/**
 * Класс-контроллер для работы с вакансиями
 */
@Controller
@RequestMapping("/vacancies") /* Работать с вакансиями будем по URI /vacancies/** */
public class VacancyController {

    /**
     * Поле {@link VacancyService} - хранилище с вакансиями
     */
    private final VacancyService vacancyService = SimpleVacancyService.getInstance();

    /**
     * Метод выполняет работу по представлению всех вакансий в браузере
     *
     * @param model {@link Model}
     * @return возвращает отображение всех вакансий
     */
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    /**
     * Метод выполняет работу по созданию отображения страницы для создания вакансии в браузере.
     *
     * @return возвращает отображение страницы с формой по созданию вакансии
     */
    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }

    /**
     * Метод выполняет работу по созданию новой вакансии.
     * Мы формируем отправку данных(то, что написали в форме создания вакансии) на сервер.
     *
     * @param vacancy объект vacancy
     * @return используется ключевое слово redirect, которое сообщает Spring,
     * чтобы после выполнения метода create перейти к обработке ссылки /vacancies GET,
     * то есть вывести таблицу со списком всех вакансий.
     * Т.е. мы повторно зайдем в метод getAll() запросим данные и выведем их на странице
     */
    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy) {
        vacancyService.save(vacancy);
        return "redirect:/vacancies";
    }

    /**
     * Метод извлекает вакансию из репозитория и возвращает на страницу
     *
     * @param model объект Model. Он используется Thymeleaf для поиска объектов, которые нужны отобразить на виде.
     * @param id    id вакансии
     * @return строку с ошибкой или представление для редактирования вакансии
     */
    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        return "vacancies/one";
    }

    /**
     * Метод производит обновление и если оно произошло,
     * то делает перенаправление на страницу со всеми вакансиями;
     *
     * @param vacancy {@link Vacancy}
     * @param model   {@link Model}
     * @return возвращает сообщение с указанием ошибки или возвращает таблицу со списком всех вакансий
     */
    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        boolean isUpdated = vacancyService.update(vacancy);
        if (!isUpdated) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    /**
     * Метод производит удаление и если оно произошло,
     * то делает перенаправление на страницу со всеми вакансиями
     *
     * @param model {@link Model}
     * @param id id вакансии
     * @return возвращает сообщение с указанием ошибки или возвращает таблицу со списком всех вакансий
     */
    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = vacancyService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }
}
