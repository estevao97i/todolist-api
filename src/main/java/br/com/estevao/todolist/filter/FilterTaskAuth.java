package br.com.estevao.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.estevao.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component // spring vai gerenciar
// vai ser o entryPoint da api - chega aqui antes da camada controller
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        var servletPath = request.getServletPath();

//        if ((servletPath.equals("/tasks/createTask")) || (servletPath.equals("/tasks/getAllTasks"))  || (servletPath.equals("/tasks/updateTask/{idTask}"))) {
        if (servletPath.startsWith("/tasks")) {
            var auth = request.getHeader("Authorization").replace("Basic", "").trim();

            byte[] authDecoded = Base64.getDecoder().decode(auth);

            var authString = new String(authDecoded);

            var login = authString.split(":")[0];
            var password = authString.split(":")[1];

            var user = repository.verificarLoginExistenteModel(login);

            if (user.getLogin() == null) {
                response.sendError(401);
            } else {
                var validPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (validPassword.verified) {
                    request.setAttribute("userId", user.getId());
                    chain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
