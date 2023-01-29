package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Класс описывает хранилище кандидатов в оперативной памяти сервера.
 */

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {

        save(new Candidate(0, "Ivan Ivanov",
                "Intern Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Sergey Petrov",
                "Junior Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Aleksey Polenov",
                "Junior+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Aleksandr Ustinov",
                "Middle Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Dmitriy Svetlov",
                "Middle+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Andrey Sidorov",
                "Senior Java Developer", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(
                candidate.getId(), (id, oldCandidate) -> new Candidate(
                        id, candidate.getName(), candidate.getDescription(),
                        candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
