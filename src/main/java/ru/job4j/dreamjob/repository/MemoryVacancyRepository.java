package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс описывает хранилище вакансий в оперативной памяти сервера.
 */
@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger id = new AtomicInteger();

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {

        save(new Vacancy(0, "Intern Java Developer",
                "We are looking for a Intern Java Developer", LocalDateTime.now(),
                true, 1, 0));
        save(new Vacancy(0, "Junior Java Developer",
                "We are looking for a Junior Java Developer", LocalDateTime.now(),
                true, 2, 0));
        save(new Vacancy(0, "Junior+ Java Developer",
                "We are looking for a Junior+ Java Developer", LocalDateTime.now(),
                true, 3, 0));
        save(new Vacancy(0, "Middle Java Developer",
                "We are looking for Middle Java Developer", LocalDateTime.now(),
                true, 1, 0));
        save(new Vacancy(0, "Middle+ Java Developer",
                "We are looking for a Middle+ Java Developer", LocalDateTime.now(),
                true, 2, 0));
        save(new Vacancy(0, "Senior Java Developer",
                "We are looking for a Senior Java Developer", LocalDateTime.now(),
                true, 3, 0));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(id.incrementAndGet());
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
                        id, vacancy.getTitle(), vacancy.getDescription(),
                        vacancy.getCreationDate(), vacancy.getVisible(),
                        vacancy.getCityId(), vacancy.getFileId())) != null;
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
