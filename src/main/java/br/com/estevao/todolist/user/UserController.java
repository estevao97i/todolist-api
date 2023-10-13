package br.com.estevao.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository repository) {
        this.userRepository = repository;
    }

    @PostMapping("/createUser")
    public ResponseEntity create(@RequestBody User user) throws Exception {
        Integer countLogin = userRepository.verificarLoginExistente(user.getLogin());
        if (countLogin > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login já existente, tente outro");
        }

        var passwordCrypt = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray()); // encrypt password
        user.setPassword(passwordCrypt);

        var userCreated = userRepository.save(user);
        return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
    }
}
