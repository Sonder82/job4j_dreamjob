package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Класс описывает хранилище кандидатов в оперативной памяти сервера.
 */

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        save(new Candidate(0, "Ivan Ivanov",
                "Intern Java Developer", localDateTime));
        save(new Candidate(0, "Sergey Petrov",
                "Junior Java Developer", localDateTime));
        save(new Candidate(0, "Aleksey Polenov",
                "Junior+ Java Developer", localDateTime));
        save(new Candidate(0, "Aleksandr Ustinov",
                "Middle Java Developer", localDateTime));
        save(new Candidate(0, "Dmitriy Svetlov",
                "Middle+ Java Developer", localDateTime));
        save(new Candidate(0, "Andrey Sidorov",
                "Senior Java Developer", localDateTime));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
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
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> new Candidate(
                        oldCandidate.getId(), oldCandidate.getName(),
                oldCandidate.getDescription(), oldCandidate.getCreationDate())) != null;
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
