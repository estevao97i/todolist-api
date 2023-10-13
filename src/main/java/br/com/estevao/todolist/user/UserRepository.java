package br.com.estevao.todolist.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select count(u) from tb_users u where u.login = :login")
    Integer verificarLoginExistente(String login);

    @Query("select u from tb_users u where u.login = :login")
    User verificarLoginExistenteModel(String login);
}
