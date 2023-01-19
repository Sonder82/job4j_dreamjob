package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс описывает хранилище вакансий в оперативной памяти сервера.
 */
public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    public MemoryVacancyRepository() {
        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        save(new Vacancy(0, "Intern Java Developer",
                "We are looking for a Intern Java Developer", localDateTime));
        save(new Vacancy(0, "Junior Java Developer",
                "We are looking for a Junior Java Developer", localDateTime));
        save(new Vacancy(0, "Junior+ Java Developer",
                "We are looking for a Junior+ Java Developer", localDateTime));
        save(new Vacancy(0, "Middle Java Developer",
                "We are looking for Middle Java Developer", localDateTime));
        save(new Vacancy(0, "Middle+ Java Developer",
                "We are looking for a Middle+ Java Developer", localDateTime));
        save(new Vacancy(0, "Senior Java Developer",
                "We are looking for a Senior Java Developer", localDateTime));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(
                vacancy.getId(), (id, oldVacancy) -> new Vacancy(
                        id, vacancy.getTitle(), vacancy.getDescription(), vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
