package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * Класс Сервис для работы с вакансиями в хранилище
 */
@Service
public class SimpleVacancyService implements VacancyService {

    /**
     * Поле хранилище для вакансий
     */
    private final VacancyRepository vacancyRepository;

    private final FileService fileService;

    public SimpleVacancyService(VacancyRepository sql2oVacancyRepository, FileService fileService) {
        this.vacancyRepository = sql2oVacancyRepository;
        this.fileService = fileService;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto image) {
        saveNewFile(vacancy, image);
        return vacancyRepository.save(vacancy);
    }

    private void saveNewFile(Vacancy vacancy, FileDto image) {
        File file = fileService.save(image);
        vacancy.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Vacancy> fileOptional = findById(id);
        if (fileOptional.isEmpty()) {
            return false;
        }
        boolean isDeleted = vacancyRepository.deleteById(id);
        fileService.deleteById(fileOptional.get().getFileId());
        return isDeleted;
    }

    @Override
    public boolean update(Vacancy vacancy, FileDto image) {
        boolean isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return vacancyRepository.update(vacancy);
        }
        /* если передан новый не пустой файл, то старый удаляем, а новый сохраняем*/
        int oldFileId = vacancy.getFileId();
        saveNewFile(vacancy, image);
        boolean isUpdated = vacancyRepository.update(vacancy);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}
