package br.gov.mj.side.web.dto;

import java.util.List;

import br.gov.mj.side.entidades.entidade.PessoaEntidade;

public class ListasPorTipoPessoaDto {

    private List<PessoaEntidade> listaTitular;
    private List<PessoaEntidade> listaRepresentante;
    private List<PessoaEntidade> listaMembroComissao;

    public ListasPorTipoPessoaDto(List<PessoaEntidade> listaTitular, List<PessoaEntidade> listaRepresentante, List<PessoaEntidade> listaMembroComissao) {
        super();
        this.listaTitular = listaTitular;
        this.listaRepresentante = listaRepresentante;
        this.listaMembroComissao = listaMembroComissao;
    }

    public List<PessoaEntidade> getListaTitular() {
        return listaTitular;
    }

    public List<PessoaEntidade> getListaRepresentante() {
        return listaRepresentante;
    }

    public List<PessoaEntidade> getListaMembroComissao() {
        return listaMembroComissao;
    }

}
