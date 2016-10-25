package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;

public class EntidadePesquisaDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Entidade entidade;
    private Pessoa representante;
    private Pessoa titular;
    private Pessoa todos;
    private Usuario usuarioLogado;
    private EnumPerfilEntidade tipoPerfil; // Se Beneficiario ou Fornecedor
                                           // //especifico para uso em
                                           // fornecedor
    private EnumOrigemCadastro origemCadastro;
    private EnumValidacaoCadastro validacaoCadastro;
    private Programa programa;
    private Bem bem;
    private Contrato contrato;
    private boolean pesquisarTodos = false;

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Pessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(Pessoa representante) {
        this.representante = representante;
    }

    public Pessoa getTitular() {
        return titular;
    }

    public void setTitular(Pessoa titular) {
        this.titular = titular;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public EnumPerfilEntidade getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(EnumPerfilEntidade tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public boolean isPesquisarTodos() {
        return pesquisarTodos;
    }

    public void setPesquisarTodos(boolean pesquisarTodos) {
        this.pesquisarTodos = pesquisarTodos;
    }

    public Pessoa getTodos() {
        return todos;
    }

    public void setTodos(Pessoa todos) {
        this.todos = todos;
    }

    public EnumOrigemCadastro getOrigemCadastro() {
        return origemCadastro;
    }

    public void setOrigemCadastro(EnumOrigemCadastro origemCadastro) {
        this.origemCadastro = origemCadastro;
    }

    public EnumValidacaoCadastro getValidacaoCadastro() {
        return validacaoCadastro;
    }

    public void setValidacaoCadastro(EnumValidacaoCadastro validacaoCadastro) {
        this.validacaoCadastro = validacaoCadastro;
    }
}
