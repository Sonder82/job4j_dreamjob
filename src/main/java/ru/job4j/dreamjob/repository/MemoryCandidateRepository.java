package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс описывает хранилище кандидатов в оперативной памяти сервера.
 */
@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger id = new AtomicInteger();

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {

        save(new Candidate(0, "Ivan Ivanov",
                "Intern Java Developer", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "Sergey Petrov",
                "Junior Java Developer", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "Aleksey Polenov",
                "Junior+ Java Developer", LocalDateTime.now(), 2, 0));
        save(new Candidate(0, "Aleksandr Ustinov",
                "Middle Java Developer", LocalDateTime.now(), 2, 0));
        save(new Candidate(0, "Dmitriy Svetlov",
                "Middle+ Java Developer", LocalDateTime.now(), 3, 0));
        save(new Candidate(0, "Andrey Sidorov",
                "Senior Java Developer", LocalDateTime.now(), 3, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(id.incrementAndGet());
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
                        candidate.getCreationDate(), candidate.getCityId(), candidate.getFileId())) != null;
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
