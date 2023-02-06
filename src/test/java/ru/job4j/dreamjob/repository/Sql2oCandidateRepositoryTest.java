package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;

class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;

    /**
     * Читаем настройки к тестовой БД из файла connection.properties
     * Прежде чем создавать репозитории нам нужно создать клиент БД Sql2o.
     * Он в свою очередь зависит от пула соединений.
     * Вызываем метод connectionPool() для создания пула соединений
     * и вызываем databaseClient() для создания Sql2o
     * @throws IOException
     */
    @BeforeAll
    public static void initRepositories() throws IOException {
        var properties = new Properties();
        try (InputStream inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        DataSource datasource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(datasource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        // нужно сохранить хотя бы один файл, т.к. Vacancy от него зависит
        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    /**
     * После окончания всех тестов, файл,
     * на который ссылались резюме кандидатов, нужно удалить.
     */
    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    /**
     * Резюме кандидатов удаляем после каждого теста для изолированности тестирования.
     * Изменения внесенные одним тестом не должны быть видны в другом.
     */
    @AfterEach
    public void clearVacancies() {
        Collection<Candidate> candidates = sql2oCandidateRepository.findAll();
        for (Candidate candidate : candidates) {
            sql2oCandidateRepository.deleteById(candidate.getId());
        }
    }
    @Test
    void whenSaveThenGetSame() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(
                0, "name", "description", creationDate, 1, file.getId()));
        Candidate savedCandidate = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate1 = sql2oCandidateRepository.save(new Candidate(
                0, "name1", "description1", creationDate, 1, file.getId()));
        Candidate candidate2 = sql2oCandidateRepository.save(new Candidate(
                0, "name2", "description2", creationDate, 1, file.getId()));
        Candidate candidate3 = sql2oCandidateRepository.save(new Candidate(
                0, "name3", "description3", creationDate, 1, file.getId()));
        Collection<Candidate> result = sql2oCandidateRepository.findAll();
        assertThat(result).isEqualTo(List.of(candidate1, candidate2, candidate3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(
                0, "name", "description", creationDate, 1, file.getId()
        ));
        boolean isDeleted = sql2oCandidateRepository.deleteById(candidate.getId());
        Optional<Candidate> savedCandidate = sql2oCandidateRepository.findById(candidate.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedCandidate).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(
                0, "name", "description", creationDate, 1, file.getId()
        ));
        Candidate updatedCandidate = new Candidate(
                candidate.getId(), "new name", "new description",
                creationDate.plusDays(1), 1, file.getId()
        );
        boolean isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        Candidate savedCandidate = sql2oCandidateRepository.findById(updatedCandidate.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(
                0, "name", "description", creationDate, 1, file.getId());
        boolean isUpdated = sql2oCandidateRepository.update(candidate);
        assertThat(isUpdated).isFalse();
    }
}