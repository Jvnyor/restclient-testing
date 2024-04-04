package com.jvnyor.demorestclient.controllers;

import com.jvnyor.demorestclient.dtos.CatRequestDTO;
import com.jvnyor.demorestclient.dtos.CatResponseDTO;
import com.jvnyor.demorestclient.services.CrudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CrudController {
    private final CrudService crudService;

    public CrudController(CrudService crudService) {
        this.crudService = crudService;
    }

    @PostMapping("/cats")
    public ResponseEntity<CatResponseDTO> createCat(CatRequestDTO catRequestDTO) {
        var catResponseDTO = crudService.createCat(catRequestDTO);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id})")
                        .buildAndExpand(catResponseDTO._id())
                        .toUri())
                .body(catResponseDTO);
    }

    @GetMapping("/cats/{id}")
    public ResponseEntity<CatResponseDTO> getCat(@PathVariable String id) {
        return ResponseEntity.ok(crudService.getCat(id));
    }
    
    @PutMapping("/cats/{id}")
    public ResponseEntity<Void> updateCat(@PathVariable String id, CatRequestDTO catRequestDTO) {
        crudService.updateCat(id, catRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cats/{id}")
    public ResponseEntity<Void> deleteCat(@PathVariable String id) {
        crudService.deleteCat(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/cats")
    public ResponseEntity<List<CatResponseDTO>> listCats() {
        return ResponseEntity.ok(crudService.listCats());
    }
}