package ru.job4j.dreamjob.model;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Класс содержит модель, описывающую вакансию.
 */
public class Vacancy {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "title", "title",
            "description", "description",
            "creation_date", "creationDate",
            "visible", "visible",
            "city_id", "cityId",
            "file_id", "fileId"
    );
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
    /**
     * поле видимость
     */
    private boolean visible;

    /**
     * поле id города
     */
    private int cityId;

    /**
     * поле id файла
     */
    private int fileId;

    public Vacancy() {

    }

    public Vacancy(int id, String title, String description, LocalDateTime creationDate,
                   boolean visible, int cityId, int fileId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.visible = visible;
        this.cityId = cityId;
        this.fileId = fileId;
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

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
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


