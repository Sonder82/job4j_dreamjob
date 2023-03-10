package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VacancyControllerTest {

    private VacancyService vacancyService;

    private CityService cityService;

    private VacancyController vacancyController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        List<Vacancy> expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        String view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "????????????");
        var city2 = new City(2, "??????????-??????????????????");
        List<City> expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        String view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Vacancy> vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        String view = vacancyController.create(vacancy, testFile, model);
        Vacancy actualVacancy = vacancyArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage2222() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(Vacancy.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        String view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestIdThenGetPageWithVacancies() {
        int searchId = 1;
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        when(vacancyService.findById(searchId)).thenReturn(Optional.of(vacancy));

        var model = new ConcurrentModel();
        String view = vacancyController.getById(model, searchId);
        var actualVacancy = model.getAttribute("vacancy");

        assertThat(view).isEqualTo("vacancies/one");
        assertThat(actualVacancy).isEqualTo(vacancy);
    }

    @Test
    public void whenRequestIdThenGetPageWithError() {
        int notExistID = 2;
        var expectedMessage = "???????????????? ?? ?????????????????? ?????????????????????????????? ???? ??????????????";
        when(vacancyService.findById(notExistID)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        String view = vacancyController.getById(model, notExistID);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenPostVacancyWithFileThenUpdateAndRedirectToVacanciesPage() throws Exception {
        var vacancyUpdate = new Vacancy(1, "test1", "updatedDescription", now(), true, 2, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Vacancy> vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        String view = vacancyController.update(vacancyUpdate, testFile, model);
        Vacancy actualVacancy = vacancyArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancyUpdate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenPostVacancyWithFileThenTryUpdateAndRedirectToErrorPage() {
        var expectedMessage = "???????????????? ?? ?????????????????? ?????????????????????????????? ???? ??????????????";
        var vacancyUpdate = new Vacancy(1, "test1", "updatedDescription", now(), true, 2, 2);
        when(vacancyService.update(any(Vacancy.class), any(FileDto.class))).thenReturn(false);

        var model = new ConcurrentModel();
        String view = vacancyController.update(vacancyUpdate, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenPostVacancyWithFileThenTryUpdateNotExistIdAndRedirectToErrorPage() {
        var expectedException = new RuntimeException("Failed to update vacancy");
        var vacancyUpdate = new Vacancy(1, "test1", "updatedDescription", now(), true, 2, 2);
        when(vacancyService.update(any(Vacancy.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        String view = vacancyController.update(vacancyUpdate, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    void whenDeleteVacancyByIdThenGetPageWithVacancies() {
        when(vacancyService.deleteById(any(Integer.class))).thenReturn(true);

        var model = new ConcurrentModel();
        String view = vacancyController.delete(model, 1);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void whenDeleteVacancyByIdThenGetPageWithError() {
        var expectedMessage = "???????????????? ?? ?????????????????? ?????????????????????????????? ???? ??????????????";
        when(vacancyService.deleteById(any(Integer.class))).thenReturn(false);

        var model = new ConcurrentModel();
        String view = vacancyController.delete(model, 1);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}