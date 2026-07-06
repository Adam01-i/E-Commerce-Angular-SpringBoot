package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.CommandeDTO;
import com.ecommerce.order_service.dto.CreerCommandeRequest;
import com.ecommerce.order_service.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    public ResponseEntity<CommandeDTO> creerCommande(@Valid @RequestBody CreerCommandeRequest request) {
        CommandeDTO commande = commandeService.creerCommande(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commande);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> consulterCommande(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.consulterCommande(id));
    }

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> listerCommandes(@RequestParam Long utilisateurId) {
        return ResponseEntity.ok(commandeService.listerCommandesUtilisateur(utilisateurId));
    }
}
