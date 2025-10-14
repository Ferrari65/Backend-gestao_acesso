package com.controller.docs;

import com.domain.user.Enum.StatusForm;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface ColaboradorFormControllerDocs {


    @Operation(
            summary = "Criar formulário de aviso prévio ou outro tipo",
            description = "Permite que um **colaborador** crie um novo formulário, como um aviso prévio para uso de rota. O corpo da requisição deve conter os dados específicos do formulário em formato JSON. Após a criação, o formulário é inicializado com o status **ABERTO**.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "201",
            description = "Formulário criado com sucesso. O corpo da resposta contém os dados do formulário recém-criado, incluindo o ID.",
            content = @Content(
                    schema = @Schema(implementation = FormResponse.class),
                    examples = @ExampleObject(
                            name = "Exemplo de Resposta",
                            summary = "Resposta de sucesso para a criação de um formulário",
                            description = "Este exemplo mostra a estrutura de um formulário recém-criado com status inicial 'ABERTO'.",
                            value = """
                            {
                              "id": "6b9d2b43-9f2e-47a5-9a34-0d2b29d1a9e3",
                              "idColaborador": "55efc89a-e69c-4020-a853-e292885f736b",
                              "nomeColaborador": "Yasmin Ferrari",
                              "matricula": "0203",
                              "status": "ABERTO",
                              "criadoEm": "2025-09-17T11:12:35Z",
                              "atualizadoEm": "2025-09-17T11:12:35Z",
                              "dados": {
                                "tipoFormulario": "Aviso_Previo",
                                "dataInicioAfastamento": "2025-10-01",
                                "dataFimAfastamento": "2025-10-05",
                                "motivo": "Férias programadas"
                              }
                            }
                            """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "Requisição inválida. A estrutura do JSON não está correta.", content = @Content)
    @ApiResponse(responseCode = "401", description = "Credenciais de autenticação não fornecidas ou inválidas.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário autenticado não possui o perfil **COLABORADOR**.", content = @Content)
    @ApiResponse(responseCode = "422", description = "Validação falhou. Os dados do formulário estão incorretos (ex.: datas inválidas).", content = @Content)
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content)
    FormResponse criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados para criação do formulário. A estrutura do objeto `dados` pode variar de acordo com o tipo de formulário.",
                    content = @Content(schema = @Schema(implementation = FormCreateRequest.class))
            )
            @RequestBody FormCreateRequest request
    );


    @Operation(
            summary = "Listar meus formulários como colaborador",
            description = "Retorna uma lista de **todos os formulários** criados pelo **colaborador autenticado**. Opcionalmente, a lista pode ser filtrada por status do formulário."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de formulários retornada com sucesso. A lista pode estar vazia se nenhum formulário for encontrado, sem o filtro aplicado.",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FormResponse.class)))
    )
    @ApiResponse(responseCode = "204", description = "Nenhum formulário encontrado para o colaborador e filtro especificado.", content = @Content)
    @ApiResponse(responseCode = "401", description = "Credenciais de autenticação não fornecidas ou inválidas.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Acesso negado. O usuário autenticado não possui o perfil **COLABORADOR**.", content = @Content)
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content)
    List<FormResponse> meus(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Filtra a lista de formulários pelo status. Se omitido, todos os formulários do colaborador são retornados.",
                    schema = @Schema(implementation = StatusForm.class,
                            description = "Os valores aceitos para o filtro de status são **PENDENTE**, **LIBERADO**, **REPROVADO**.")
            )
            @RequestParam(required = false) StatusForm status
    );
}
