package br.gov.mj.side.web.dto.enderecoLicitacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao;

public class AgrupamentoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private EnumTipoAgrupamentoLicitacao tipoAgrupamentoLicitacao;

    private List<BemUfLicitacaoDto> bensUfs = new ArrayList<BemUfLicitacaoDto>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EnumTipoAgrupamentoLicitacao getTipoAgrupamentoLicitacao() {
        return tipoAgrupamentoLicitacao;
    }

    public void setTipoAgrupamentoLicitacao(EnumTipoAgrupamentoLicitacao tipoAgrupamentoLicitacao) {
        this.tipoAgrupamentoLicitacao = tipoAgrupamentoLicitacao;
    }

    public List<BemUfLicitacaoDto> getBensUfs() {
        return bensUfs;
    }

    public void setBensUfs(List<BemUfLicitacaoDto> bensUfs) {
        this.bensUfs = bensUfs;
    }

}
