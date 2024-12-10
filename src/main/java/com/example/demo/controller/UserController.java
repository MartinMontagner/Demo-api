package com.example.demo.controller;

import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public List<User> mostrarUsuarios ()
    {
        return userRepository.findAll();
    }
    // Crear un nuevo usuario

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    result.getFieldErrors().stream()
                            .map(error -> error.getField() + ": " + error.getDefaultMessage())
                            .collect(Collectors.joining(", "))
            );
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente.");
    }


    // Actualizar un usuario por ID
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + id + " no encontrado."));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        userRepository.save(user);

        return "Usuario actualizado exitosamente.";
    }
    @GetMapping("/{id}")
    public User buscarUserId(@PathVariable Long id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + id + " no encontrado."));
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.delete(buscarUserId(id));
        return "Usuario eliminado exitosamente.";
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
    @DeleteMapping
    public ResponseEntity<String> deleteAllUsers() {
        userRepository.deleteAll();
        return ResponseEntity.ok("Todos los usuarios han sido eliminados.");
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countUsers() {
        long count = userRepository.count();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String keyword) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    @GetMapping("/paged")
    public ResponseEntity<List<User>> getPagedUsers(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return ResponseEntity.ok(userPage.getContent());
    }
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("La API está funcionando correctamente.");
    }
    @PostMapping("/batch")
    public ResponseEntity<String> createUsersBatch(@RequestBody List<User> usersBatch) {
        userRepository.saveAll(usersBatch);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuarios creados exitosamente.");
    }
    @GetMapping("/batch")
    public ResponseEntity<List<User>> getUsersByIds(@RequestParam List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return ResponseEntity.ok(users);
    }
    @PutMapping("/reset-names")
    public ResponseEntity<String> resetAllUserNames(@RequestParam String defaultName) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setName(defaultName);
        }
        userRepository.saveAll(users);
        return ResponseEntity.ok("Todos los nombres de usuario han sido restablecidos.");
    }
    @PostMapping("/{userId}/posts")
    public ResponseEntity<String> createPost(@PathVariable Long userId, @Valid @RequestBody Post post) {
        return userRepository.findById(userId).map(user -> {
            user.addPost(post);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Post creado exitosamente.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado."));
    }


}
