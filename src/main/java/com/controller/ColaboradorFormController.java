package com.controller;

import com.domain.user.Enum.StatusForm;
import com.dto.colaborador.ColaboradorDTO;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;
import com.repositories.UserRepository;
import com.services.impl.ColaboradorFormServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/forms", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Gestão de Aviso Prévio", description = "Criação, listagem e atualização de status de formulários.")
@SecurityRequirement(name = "bearerAuth")
public class ColaboradorFormController implements com.controller.docs.ColaboradorFormControllerDocs {

    private final ColaboradorFormServiceImpl service;
    private final UserRepository userRepository;

    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('COLABORADOR')")
    @Override
    public FormResponse criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados para criação do formulário. A estrutura do objeto `dados` pode variar de acordo com o tipo de formulário.",
                    content = @Content(schema = @Schema(implementation = FormCreateRequest.class))
            )
            @RequestBody FormCreateRequest request
    ) {
        ColaboradorDTO colab = resolveColaboradorDTO();
        return service.criarPara(colab, request);
    }

    @GetMapping("/colaboradorAviso")
    @PreAuthorize("hasRole('COLABORADOR')")
    @Override
    public List<FormResponse> meus(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Filtra a lista de formulários pelo status. Se omitido, todos os formulários do colaborador são retornados.",
                    schema = @Schema(implementation = StatusForm.class,
                            description = "Os valores aceitos para o filtro de status são **PENDENTE**, **LIBERADO**, **REPROVADO**.")
            )
            @RequestParam(required = false) StatusForm status
    ) {
        ColaboradorDTO colab = resolveColaboradorDTO();
        return service.listarTodosDoColaborador(colab.idColaborador(), status);
    }


    private ColaboradorDTO resolveColaboradorDTO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        String email = auth.getName();

        var u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado pelo email autenticado."));

        return new ColaboradorDTO(
                u.getIdColaborador(),
                null,
                null,
                null,
                null,
                u.getNome(),
                u.getMatricula(),
                u.getCpf(),
                u.getEmail(),
                u.getDataNasc(),
                u.getLogradouro(),
                u.getBairro(),
                u.getNumero(),
                u.getAtivo()
        );
    }
}