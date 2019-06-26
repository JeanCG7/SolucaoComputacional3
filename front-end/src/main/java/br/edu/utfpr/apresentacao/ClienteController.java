package br.edu.utfpr.apresentacao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClienteController {
    private final String baseURL = "http://localhost:8081/servico/cliente";

    @GetMapping("/cliente")
    public String inicial(Model data) throws JsonSyntaxException, UnirestException {

        ClienteModel[] clientes = getListaClientes();
        data.addAttribute("clientes", clientes);

        return "cliente-view";
    }

    @GetMapping("/cliente/criar")
    public String criar(ClienteModel cliente) throws UnirestException {

        Unirest.post(baseURL)
                .header("Content-type", "application/json")
                .header("accept", "application/json")
                .body(new Gson().toJson(cliente, ClienteModel.class))
                .asJson();

        return "redirect:/cliente";

    }

    @GetMapping("/cliente/deletar")
    public String deletar (@PathVariable int id) throws UnirestException {
        Unirest.delete(baseURL + "/{id}")
                .routeParam("id", String.valueOf(id))
                .asJson();
        return "redirect:/cliente";
    }

    @GetMapping ("/cliente/abrirTelaAlteracao")
    public String abrirTelaAlteracao (@RequestParam int id, Model data) throws JsonSyntaxException, UnirestException {

        ClienteModel clienteExistente = new Gson()
                .fromJson(
                        Unirest
                                .get(baseURL + "/{id}")
                                .routeParam("id", String.valueOf(id))
                                .asJson()
                                .getBody()
                                .toString(),
                        ClienteModel.class
                );

        data.addAttribute("clienteAtual", clienteExistente);

        ClienteModel[] clientes = getListaClientes();

        data.addAttribute("clientes", clientes);

        return "cliente-view-alterar";
    }

    @PostMapping("/cliente/alterar")
    public String alterar (ClienteModel clienteAlterado) throws UnirestException {

        Unirest
                .put(baseURL + "/{id}")
                .routeParam("id", String.valueOf(clienteAlterado.getId()))
                .header("Content-type", "application/json")
                .header("accept", "application/json")
                .body(new Gson().toJson(clienteAlterado, ClienteModel.class))
                .asJson();

        return "redirect:/cliente";
    }

    private ClienteModel[] getListaClientes () throws UnirestException {
        ClienteModel clientes[] = new Gson()
                .fromJson(
                        Unirest
                                .get(baseURL)
                                .asJson()
                                .getBody()
                                .toString(),
                        ClienteModel[].class
                );
        return clientes;
    }
}
