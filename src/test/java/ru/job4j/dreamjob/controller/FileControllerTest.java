package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    void whenRequestFileByIdThenGetFile() throws IOException {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.of(fileDto));

        var actualFileDto = fileController.getById(1);

        assertThat(actualFileDto.getStatusCode()).isEqualTo(HttpStatus.valueOf(200));
        assertThat(fileDto.getContent()).isEqualTo(actualFileDto.getBody());
    }

    @Test
    void whenRequestFileByNonExistIdThenFileNotFound() throws IOException {
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.empty());

        var actualFileDto = fileController.getById(1);

        assertThat(actualFileDto.getStatusCode()).isEqualTo(HttpStatus.valueOf(404));
        assertThat(actualFileDto.getBody()).isNull();
    }
}