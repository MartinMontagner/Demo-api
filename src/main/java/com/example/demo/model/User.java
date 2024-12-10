package com.example.demo.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "El nombre no puede estar vacío.")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
        private String name;

        @NotBlank(message = "El correo electrónico no puede estar vacío.")
        @Email(message = "Debe proporcionar un correo electrónico válido.")
        private String email;
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Post> posts = new ArrayList<>();

        // Métodos para gestionar la lista de posts
        public void addPost(Post post) {
                posts.add(post);
                post.setUser(this);
        }

        public void removePost(Post post) {
                posts.remove(post);
                post.setUser(null);
        }

        // Constructor por defecto
        public User() {}

        // Constructor con parámetros
        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
}
