package ru.job4j.dreamjob.model;

/**
 * Класс содержит модель, описывающую город
 */
public class City {
    /**
     * поле id
     */
    private final int id;
    /**
     * поле название города
     */
    private final String name;

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
