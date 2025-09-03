package com.controller;

import com.dto.ColaboradorDTO;
import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colaboradores")
@RequiredArgsConstructor
public class ColaboradoresController {
    private final UserRepository userRepository;

    @GetMapping
    public List<ColaboradorDTO> listar() {
        return userRepository.findAll().stream().map(ColaboradorDTO::from).toList();
    }
}
