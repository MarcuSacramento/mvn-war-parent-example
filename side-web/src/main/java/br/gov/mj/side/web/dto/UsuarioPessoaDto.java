package br.gov.mj.side.web.dto;

import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Pessoa;

public class UsuarioPessoaDto {

    private Usuario usuario;
    private Pessoa pessoa;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

}
