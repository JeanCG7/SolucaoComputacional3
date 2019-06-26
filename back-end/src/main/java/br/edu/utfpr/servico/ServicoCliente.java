package br.edu.utfpr.servico;

import br.edu.utfpr.dto.ClienteDTO;
import br.edu.utfpr.dto.PaisDTO;
import br.edu.utfpr.excecao.NomeClienteJaExisteException;
import br.edu.utfpr.excecao.NomeClienteMenor5CaracteresException;
import br.edu.utfpr.excecao.PaisNaoEncontradoException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ServicoCliente {

    private List<ClienteDTO> clientes;
    private List<PaisDTO> paises;

    public ServicoCliente() {
        paises = Stream.of(
                PaisDTO.builder().id(1).nome("Itália").sigla("IT").codigoTelefone(66).build(),
                PaisDTO.builder().id(2).nome("Austrália").sigla("AU").codigoTelefone(77).build(),
                PaisDTO.builder().id(3).nome("Brasil").sigla("BR").codigoTelefone(55).build()
                ).collect(Collectors.toList());

        clientes = Stream.of(
                ClienteDTO.builder().id(1).nome("Jean").idade(21).telefone("(18) 99694-6472").limiteCredito(350.25).pais(paises.get(0)).build(),
                ClienteDTO.builder().id(2).nome("Silvia").idade(49).telefone("(18) 00000-0000").limiteCredito(470.25).pais(paises.get(0)).build(),
                ClienteDTO.builder().id(3).nome("José").idade(50).telefone("(18) 111111-1111").limiteCredito(987.25).pais(paises.get(0)).build(),
                ClienteDTO.builder().id(4).nome("Vitória").idade(22).telefone("(18) 22222-2222").limiteCredito(525.25).pais(paises.get(0)).build()
                ).collect(Collectors.toList());
    }

    @GetMapping("/servico/cliente")
    public ResponseEntity<List<ClienteDTO>> listar() {

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> listaPorId(@PathVariable int id) {
        Optional<ClienteDTO> cliente = clientes.stream().filter(c -> c.getId() == id).findAny();

        return ResponseEntity.of(cliente);
    }

    @PostMapping("/servico/cliente")
    public ResponseEntity<ClienteDTO> cadastrar (@RequestBody ClienteDTO cliente) throws PaisNaoEncontradoException, NomeClienteMenor5CaracteresException, NomeClienteJaExisteException {
        Optional<PaisDTO> paisExistente = paises.stream().filter(p -> p.getId() == cliente.getPais().getId()).findAny();

        if(verificaNomeExiste(cliente))
            throw new NomeClienteJaExisteException("Nome: " + cliente.getNome() + " já cadastrado");

        if(cliente.getNome().length() < 5)
            throw new NomeClienteMenor5CaracteresException("Nome: " + cliente.getNome() + " possui menos de 5 caracteres");

        cliente.setPais(
                Optional.ofNullable(paisExistente.get()).orElseThrow(() ->
                        new PaisNaoEncontradoException("Pais com id: " + cliente.getPais().getId() + " não encontrado")));

        cliente.setId(clientes.size() + 1);
        clientes.add(cliente);

        return ResponseEntity.status(201).body(cliente);
    }

    @PutMapping("servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> atualizar (@PathVariable int id, @RequestBody ClienteDTO clienteReq) throws NomeClienteMenor5CaracteresException, PaisNaoEncontradoException {
        Optional<ClienteDTO> cliente = clientes.stream().filter(c -> c.getId() == id).findAny();

        if(cliente.isPresent())
        {
            Optional<PaisDTO> paisExistente = paises.stream().filter(p -> p.getId() == cliente.get().getPais().getId()).findAny();
            cliente.get().setPais(
                    Optional.ofNullable(paisExistente.get()).orElseThrow(() ->
                            new PaisNaoEncontradoException("Pais com id: " + cliente.get().getPais().getId() + " não encontrado")));
            cliente.get().setNome(clienteReq.getNome());
            cliente.get().setIdade(clienteReq.getIdade());
            cliente.get().setTelefone(clienteReq.getTelefone());
            cliente.get().setLimiteCredito(clienteReq.getLimiteCredito());
        }

        return ResponseEntity.of(cliente);
    }

    @DeleteMapping("servico/cliente/{id}")
    public ResponseEntity deletar (@PathVariable int id) {
        if (clientes.removeIf(clientes -> clientes.getId() == id))
            return ResponseEntity.noContent().build();

        else
            return ResponseEntity.notFound().build();
    }

    private boolean verificaNomeExiste(ClienteDTO cliente)
    {
        for(ClienteDTO clienteDaLista: clientes)
        {
            if(clienteDaLista.getNome().equals(cliente.getNome())
                && clienteDaLista.getId() != cliente.getId())
                return true;
        }
        return false;
    }

}
