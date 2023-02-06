package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
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

class Sql2oVacancyRepositoryTest {

    private static Sql2oVacancyRepository sql2oVacancyRepository;

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
        try (InputStream inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        DataSource datasource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(datasource);

        sql2oVacancyRepository = new Sql2oVacancyRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        // нужно сохранить хотя бы один файл, т.к. Vacancy от него зависит
        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    /**
     * После окончания всех тестов, файл,
     * на который ссылались вакансии, нужно удалить.
     */
    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    /**
     * Вакансии удаляем после каждого теста для изолированности тестирования.
     * Изменения внесенные одним тестом не должны быть видны в другом.
     */
    @AfterEach
    public void clearVacancies() {
        Collection<Vacancy> vacancies = sql2oVacancyRepository.findAll();
        for (Vacancy vacancy : vacancies) {
            sql2oVacancyRepository.deleteById(vacancy.getId());
        }
    }
    @Test
    void whenSaveThenGetSame() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = sql2oVacancyRepository.save(new Vacancy(
                0, "title", "description", creationDate, true, 1, file.getId()));
        Vacancy savedVacancy = sql2oVacancyRepository.findById(vacancy.getId()).get();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(vacancy);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy1 = sql2oVacancyRepository.save(new Vacancy(
                0, "title1", "description1", creationDate, true, 1, file.getId()));
        Vacancy vacancy2 = sql2oVacancyRepository.save(new Vacancy(
                0, "title2", "description2", creationDate, false, 1, file.getId()));
        Vacancy vacancy3 = sql2oVacancyRepository.save(new Vacancy(
                0, "title3", "description3", creationDate, true, 1, file.getId()));
        Collection<Vacancy> result = sql2oVacancyRepository.findAll();
        assertThat(result).isEqualTo(List.of(vacancy1, vacancy2, vacancy3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oVacancyRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oVacancyRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = sql2oVacancyRepository.save(new Vacancy(
                0, "title", "description", creationDate, true, 1, file.getId()
        ));
        boolean isDeleted = sql2oVacancyRepository.deleteById(vacancy.getId());
        Optional<Vacancy> savedVacancy = sql2oVacancyRepository.findById(vacancy.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedVacancy).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oVacancyRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = sql2oVacancyRepository.save(new Vacancy(
                0, "title", "description", creationDate, true, 1, file.getId()
        ));
        Vacancy updatedVacancy = new Vacancy(
                vacancy.getId(), "new title", "new description",
                creationDate.plusDays(1), true, 1, file.getId()
        );
        boolean isUpdated = sql2oVacancyRepository.update(updatedVacancy);
        Vacancy savedVacancy = sql2oVacancyRepository.findById(updatedVacancy.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(updatedVacancy);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = new Vacancy(
                0, "title", "description", creationDate, true, 1, file.getId());
        boolean isUpdated = sql2oVacancyRepository.update(vacancy);
        assertThat(isUpdated).isFalse();
    }
}