package ru.job4j.dreamjob.model;

import java.util.Objects;
/**
 * Класс содержит модель, описывающую кандидата.
 */
public class Candidate {
    /**
     * поле id
     */
    private int id;
    /**
     * поле имя кандидата
     */
    private String name;
    /**
     * поле описание
     */
    private String description;
    /**
     * поле дата создания
     */
    private String creationDate;

    public Candidate(int id, String name, String description, String creationDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Можно исключить поля name, description, created.
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
        Candidate candidate = (Candidate) o;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
