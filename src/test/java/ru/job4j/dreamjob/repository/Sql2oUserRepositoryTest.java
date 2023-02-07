package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        DataSource connection = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(connection);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearRegister() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM users");
            query.executeUpdate();
        }
    }

    @Test
    void whenSaveThenGetSame() {
        Optional<User> user = sql2oUserRepository.save(new User(
                0, "abc@gmail.com", "Aleksey", "12345"));
        Optional<User> savedUser = sql2oUserRepository.findByEmailAndPassword(
                user.get().getEmail(), user.get().getPassword());
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenEmailAndPasswordWrong() {
        assertThat(sql2oUserRepository.findByEmailAndPassword("abc", "qwerty")).isEqualTo(empty());
    }

    @Test
    void whenSaveUserWithTheSameEmail() {
        User user = new User(1, "abc@gmail.com", "Aleksey", "12345");
        User anotherUser = new User(2, "abc@gmail.com", "Ivan", "12345");
        sql2oUserRepository.save(user);
        assertThat(sql2oUserRepository.save(anotherUser)).isEmpty();
    }
}