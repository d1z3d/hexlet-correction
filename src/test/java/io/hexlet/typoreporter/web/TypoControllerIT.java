package io.hexlet.typoreporter.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import io.hexlet.typoreporter.domain.typo.Typo;
import io.hexlet.typoreporter.domain.typo.TypoStatus;
import io.hexlet.typoreporter.repository.TypoRepository;
import io.hexlet.typoreporter.test.DBUnitEnumPostgres;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.database.rider.core.api.configuration.Orthography.LOWERCASE;
import static io.hexlet.typoreporter.domain.typo.TypoEvent.CANCEL;
import static io.hexlet.typoreporter.test.Constraints.POSTGRES_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@Testcontainers
@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
@Transactional
@DBRider
@DBUnit(caseInsensitiveStrategy = LOWERCASE, dataTypeFactoryClass = DBUnitEnumPostgres.class, cacheConnection = false)
@DataSet(value = {"workspaces.yml", "typos.yml"})
public class TypoControllerIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
        .withUsername("inmemory")
        .withPassword("inmemory");

    @Autowired
    private TypoRepository typoRepository;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @ParameterizedTest
    @MethodSource("io.hexlet.typoreporter.test.factory.EntitiesFactory#getTypoIdsExist")
    void updateTypoStatusIsSuccessful(final Long typoId) throws Exception {
        Typo typo = typoRepository.findById(typoId).orElse(null);
        Long wksId = typo.getWorkspace().getId();

        typo.setTypoStatus(TypoStatus.IN_PROGRESS);
        TypoStatus previousStatus = typo.getTypoStatus(); // IN_PROGRESS

        mockMvc.perform(patch("/typos/{id}/status", typoId)
                .param("wksId", wksId.toString())
                .param("event", CANCEL.name())
                .with(csrf()))
            .andExpect(redirectedUrl("/workspace/" + wksId + "/typos"));
        assertThat(previousStatus).isNotEqualTo(typo.getTypoStatus());
    }

    @ParameterizedTest
    @MethodSource("io.hexlet.typoreporter.test.factory.EntitiesFactory#getTypoIdsExist")
    void updateTypoStatusWithNext(final Long typoId) throws Exception {
        Typo typo = typoRepository.findById(typoId).orElse(null);
        Long wksId = typo.getWorkspace().getId();
        String next = "/typos?page=0&size=2";

        typo.setTypoStatus(TypoStatus.IN_PROGRESS);
        TypoStatus previousStatus = typo.getTypoStatus(); // IN_PROGRESS

        mockMvc.perform(patch("/typos/{id}/status", typoId)
                .param("wksId", wksId.toString())
                .param("event", CANCEL.name())
                .param("next", next)
                .with(csrf()))
            .andExpect(redirectedUrl("/workspace/" + wksId + next));
        assertThat(previousStatus).isNotEqualTo(typo.getTypoStatus());
    }

    @ParameterizedTest
    @MethodSource("io.hexlet.typoreporter.test.factory.EntitiesFactory#getTypoIdsExist")
    void updateTypoStatusWithEventIsEmpty(final Long typoId) throws Exception {
        Typo typo = typoRepository.findById(typoId).orElse(null);
        Long wksId = typo.getWorkspace().getId();

        typo.setTypoStatus(TypoStatus.IN_PROGRESS);
        TypoStatus previousStatus = typo.getTypoStatus(); // IN_PROGRESS

        mockMvc.perform(patch("/typos/{id}/status", typoId)
                .param("wksId", wksId.toString())
                .param("event", "")
                .with(csrf()))
            .andExpect(redirectedUrl("/workspace/" + wksId + "/typos"));
        assertThat(previousStatus).isEqualTo(typo.getTypoStatus());
    }

    @ParameterizedTest
    @MethodSource("io.hexlet.typoreporter.test.factory.EntitiesFactory#getWorkspaceIdsExist")
    void updateTypoStatusWithUpdatedTypoIsEmpty(final Long wksId) throws Exception {
        Long nonExistTypoId = 11L;
        mockMvc.perform(patch("/typos/{id}/status", nonExistTypoId)
                .param("wksId", wksId.toString())
                .param("event", CANCEL.name())
                .with(csrf()))
            .andExpect(redirectedUrl("/workspace/" + wksId + "/typos"));
    }

}
