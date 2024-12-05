package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    // Obtener todos los usuarios
    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }

    // Crear un nuevo usuario
    @PostMapping
    public String createUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()){
            return "El campo 'name' es obligatorio.";
        }
        if(user.getEmail()==null || user.getEmail().isEmpty())
        {
            return "El campo 'email' es obligatorio";
        }
        users.add(user);
        return "Usuario creado exitosamente.";
    }

    // Actualizar un usuario por ID
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                user.setName(updatedUser.getName());
                user.setEmail(updatedUser.getEmail());
                return "Usuario actualizado exitosamente.";
            }
        }
        return "Usuario no encontrado.";
    }
    @GetMapping("/{id}")
    public User buscarUserId(@PathVariable Long id)
    {
        for(User user: users)
        {
            if(user.getId().equals(id))
            {
                return user;
            }
        }return null;
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        users.removeIf(user -> user.getId().equals(id));
        return "Usuario eliminado exitosamente.";
    }
}
