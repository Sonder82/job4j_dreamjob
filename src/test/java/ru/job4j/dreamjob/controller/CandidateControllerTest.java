package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateService candidateService;

    private CityService cityService;

    private CandidateController candidateController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    void whenRequestCandidateListPageThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var candidate2 = new Candidate(2, "test2", "desc2", now(), 3, 4);
        List<Candidate> expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        String view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "????????????");
        var city2 = new City(2, "??????????-??????????????????");
        List<City> expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        String view = candidateController.getCreationPage(model);
        var actualCandidates = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCandidates).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        String view = candidateController.create(candidate, testFile, model);
        Candidate actualCandidate = candidateArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage2222() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(Candidate.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        String view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestIdThenGetPageWithCandidates() {
        int searchId = 1;
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        when(candidateService.findById(searchId)).thenReturn(Optional.of(candidate));

        var model = new ConcurrentModel();
        String view = candidateController.getById(model, searchId);
        var actualCandidate = model.getAttribute("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidate).isEqualTo(candidate);
    }

    @Test
    public void whenRequestIdThenGetPageWithError() {
        int notExistID = 2;
        var expectedMessage = "???????????????? ?? ?????????????????? ?????????????????????????????? ???? ????????????";
        when(candidateService.findById(notExistID)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        String view = candidateController.getById(model, notExistID);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenPostCandidateWithFileThenUpdateAndRedirectToCandidatesPage() throws Exception {
        var candidateUpdate = new Candidate(1, "test1", "updatedDescription", now(), 2, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        String view = candidateController.update(candidateUpdate, testFile, model);
        Candidate actualCandidate = candidateArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidateUpdate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenPostCandidateWithFileThenTryUpdateAndRedirectToErrorPage() {
        var expectedMessage = "???????????????? ?? ?????????????????? ?????????????????????????????? ???? ????????????";
        var candidateUpdate = new Candidate(1, "test1", "updatedDescription", now(), 2, 2);
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenReturn(false);

        var model = new ConcurrentModel();
        String view = candidateController.update(candidateUpdate, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenPostCandidateWithFileThenTryUpdateNotExistIdAndRedirectToErrorPage() {
        var expectedException = new RuntimeException("Failed to update candidate");
        var candidateUpdate = new Candidate(1, "test1", "updatedDescription", now(), 2, 2);
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        String view = candidateController.update(candidateUpdate, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    void whenDeleteCandidateByIdThenGetPageWithCandidates() {
        when(candidateService.deleteById(any(Integer.class))).thenReturn(true);

        var model = new ConcurrentModel();
        String view = candidateController.delete(model, 1);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    void whenDeleteCandidateByIdThenGetPageWithError() {
        var expectedMessage = "???????????????? ?? ?????????????????? ?????????????????????????????? ???? ????????????";
        when(candidateService.deleteById(any(Integer.class))).thenReturn(false);

        var model = new ConcurrentModel();
        String view = candidateController.delete(model, 1);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}