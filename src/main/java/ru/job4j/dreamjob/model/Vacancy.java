package ru.job4j.dreamjob.model;


import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс содержит модель, описывающую вакансию.
 */
public class Vacancy {
    /**
     * поле id
     */
    private int id;
    /**
     * поле название вакансии
     */
    private String title;
    /**
     * поле описание вакансии
     */
    private String description;
    /**
     * поле дата создания вакансии
     */
    private LocalDateTime creationDate = LocalDateTime.now();

    public Vacancy() {

    }

    public Vacancy(int id, String title, String description, LocalDateTime creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Можно исключить поля title, description, created.
     * Эти поля могут иметь одинаковые значение у разных объектов.
     * @param o объект
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vacancy vacancy = (Vacancy) o;
        return id == vacancy.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


