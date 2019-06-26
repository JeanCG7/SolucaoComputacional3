package br.edu.utfpr.excecao;

public class PaisNaoEncontradoException extends Exception {
    public PaisNaoEncontradoException (String descricao) {
        super(descricao);
    }
}
