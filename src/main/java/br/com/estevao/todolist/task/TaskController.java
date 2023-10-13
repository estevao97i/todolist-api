package br.com.estevao.todolist.task;

import br.com.estevao.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping(value = "/createTask")
    public ResponseEntity createTask(@RequestBody Task task, HttpServletRequest request) throws Exception {
        // pegando o atributo do UserId dentro do escopo
        task.setUserId(((UUID) request.getAttribute("userId")));

        var dataNow = LocalDateTime.now();

        // 2003-06-25T12:30:00 - LocalDateTime
        if (dataNow.isAfter(task.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Inicial antes da Data atual");
        }
        // 2025-06-25T12:30:00 - LocalDateTime
        if (dataNow.isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Final antes da Data atual");
        }
        // 2027-06-25T12:30:00 - LocalDateTime
        if (task.getStartAt().isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Inicial de ser antes da Data Final");
        }

        var taskSaved = taskRepository.save(task);
        return ResponseEntity.ok(taskSaved);
    }

    @GetMapping(value = "/getAllTasks")
    public ResponseEntity<List<Task>> list(HttpServletRequest request) {
        var idUser = request.getAttribute("userId");
        var taskResponse = taskRepository.findByUserId((UUID) idUser);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/updateTask/{idTask}")
    public ResponseEntity update(@RequestBody Task task, HttpServletRequest request, @PathVariable("idTask") UUID idTask) {
        var taskReturn = taskRepository.findById(idTask).orElse(null);

        if (taskReturn == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        var userId = (request.getAttribute("userId"));
        assert taskReturn != null;
        if (!taskReturn.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem permissão para alterar essa tarefa");
        }
        // pegar as informações nao nulas e mesclar com os novos parametros
        Utils.copyNonNullProperties(task, taskReturn);
        return ResponseEntity.ok().body(taskRepository.save(taskReturn));
    }
}
