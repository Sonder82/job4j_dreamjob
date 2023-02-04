package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * Класс Сервис для работы с кандидатами в хранилище
 */
@Service
public class SimpleCandidateService implements CandidateService {

    /**
     * Поле хранилище кандидатов
     */
    private final CandidateRepository candidateRepository;

    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository, FileService fileService) {
        this.candidateRepository = sql2oCandidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        saveNewFile(candidate, image);
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto image) {
        File file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Candidate> fileOptional = findById(id);
        if (fileOptional.isEmpty()) {
            return false;
        }
        boolean isDeleted = candidateRepository.deleteById(id);
        fileService.deleteById(fileOptional.get().getFileId());
        return isDeleted;
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        boolean isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }
        /* если передан новый не пустой файл, то старый удаляем, а новый сохраняем*/
        int oldFileId = candidate.getFileId();
        saveNewFile(candidate, image);
        boolean isUpdated = candidateRepository.update(candidate);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
