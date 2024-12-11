package com.forumHub.controllers.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.enums.Status;
import com.forumHub.services.TopicoService;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminTopicoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TopicoService topicoService;

    Topico topico;

    @BeforeEach
    void setUp() {
        topico = Topico.builder()
                .id(99L)
                .titulo("Titulo topico")
                .mensagem("Mensagem topico")
                .dataCriacao(LocalDateTime.now().minusDays(1))
                .ativo(true)
                .status(Status.ABERTA)
                .build();
    }

    @Test
    @DisplayName("Should delete a Topico")
    @WithMockUser(roles = "ADMIN")
    void AdminTopicoController_delete_returnStatusCodeNoContent() throws Exception {
        Long topicoId = 1L;

        when(topicoService.findById(topicoId)).thenReturn(Optional.of(topico));
        doAnswer(invocation -> {
            topico.setAtivo(false);
            return null;
        }).when(topicoService).delete(topico);

        ResultActions response = mockMvc
                .perform(delete("/admin/topicos/{id}", topicoId).contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());

        assertEquals(false, topico.isAtivo());

        verify(topicoService, times(1)).findById(topicoId);
        verify(topicoService, times(1)).delete(topico);

        verifyNoMoreInteractions(topicoService);
    }

    @Test
    @DisplayName("Should not delete a Topico, when Topico doesn't exist")
    @WithMockUser(roles = "ADMIN")
    void AdminTopicoController_delete_errorCase1() throws Exception {
        Long topicoId = 10L;

        String expectedResponse = "Nenhum topico com o ID informado";

        when(topicoService.findById(topicoId)).thenReturn(Optional.empty());

        ResultActions response = mockMvc
                .perform(delete("/admin/topicos/{id}", topicoId).contentType(MediaType.APPLICATION_JSON));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse));

        verify(topicoService, times(1)).findById(topicoId);

        verifyNoMoreInteractions(topicoService);
    }

    @Test
    @DisplayName("Should not delete a Topico, when Topico already is deleted")
    @WithMockUser(roles = "ADMIN")
    void AdminTopicoController_delete_errorCase2() throws Exception {
        Long topicoId = 1L;
        topico.setAtivo(false);

        String expectedResponse = "Topico já excluido";

        when(topicoService.findById(topicoId)).thenReturn(Optional.of(topico));

        ResultActions response = mockMvc
                .perform(delete("/admin/topicos/{id}", topicoId).contentType(MediaType.APPLICATION_JSON));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse));

        verify(topicoService, times(1)).findById(topicoId);

        verifyNoMoreInteractions(topicoService);
    }

    @Test
    @DisplayName("Should not delete Topico when param id is invalid, return error message")
    @WithMockUser(roles = "ADMIN")
    void AdminTopicoController_delete_errorCase3() throws Exception {
        String topicoId = "abc";

        String expectedResponse = "Parametro informado está em um formato invalido, verifique e tente novamente";

        ResultActions response = mockMvc
                .perform(delete("/admin/topicos/{id}", topicoId).contentType(MediaType.APPLICATION_JSON));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("id"))
                .andExpect(jsonPath("$.message").value(expectedResponse));

        verifyNoInteractions(topicoService);
    }

    @Test
    @DisplayName("Should not delete Topico when User isn't ADMIN")
    @WithMockUser()
    void AdminTopicoController_delete_errorCase4() throws Exception {
        Long topicoId = 1l;

        ResultActions response = mockMvc
                .perform(delete("/admin/topicos/{id}", topicoId).contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isForbidden());

        verifyNoInteractions(topicoService);
    }

}
