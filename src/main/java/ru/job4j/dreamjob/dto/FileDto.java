package ru.job4j.dreamjob.dto;

/**
 * Класс описывает доменную модель файла
 */
public class FileDto {

    private String name;

    private byte[] content; /* тут кроется различие. Доменная модель хранит путь, а не содержимое*/

    public FileDto(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
