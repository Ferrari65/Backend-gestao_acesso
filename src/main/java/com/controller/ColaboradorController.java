package com.controller;

import com.dto.ColaboradorDTO;
import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/listarcolaboradores")
@RequiredArgsConstructor
public class ColaboradorController {
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('GESTOR')")
    @GetMapping
    public List<ColaboradorDTO> listarColaborador(){
        return userRepository.findAll().stream().map(ColaboradorDTO::from).toList();
    }
}
