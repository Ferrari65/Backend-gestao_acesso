package com.dto.colaborador;

import com.domain.user.colaborador.User;
import java.time.LocalDate;
import java.util.UUID;

public record ColaboradorDTO(
        UUID    idColaborador,
        String  role,
        Integer idCidade,
        String  cidadeNome,
        String  cidadeUf,
        String  nome,
        String  matricula,
        String  cpf,
        String  email,
        LocalDate dataNasc,
        String  logradouro,
        String  bairro,
        Integer numero,
        Boolean ativo
) {
    public static ColaboradorDTO from(User u) {
        return new ColaboradorDTO(
                u.getIdColaborador(),
                u.getRole() != null ? u.getRole().getNome() : null,
                u.getCidade() != null ? u.getCidade().getIdCidade() : null,
                u.getCidade() != null ? u.getCidade().getNome() : null,
                u.getCidade() != null ? u.getCidade().getUf() : null,
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
