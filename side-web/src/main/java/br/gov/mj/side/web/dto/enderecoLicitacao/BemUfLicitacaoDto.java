package br.gov.mj.side.web.dto.enderecoLicitacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;

public class BemUfLicitacaoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Bem bem;
    private AgrupamentoLicitacao agrupamento;
    private Uf uf;
    private Long quantidade;
    private List<EnderecoDto> endereco = new ArrayList<EnderecoDto>();

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public AgrupamentoLicitacao getAgrupamento() {
        return agrupamento;
    }

    public void setAgrupamento(AgrupamentoLicitacao agrupamento) {
        this.agrupamento = agrupamento;
    }

    public List<EnderecoDto> getEndereco() {
        return endereco;
    }

    public void setEndereco(List<EnderecoDto> endereco) {
        this.endereco = endereco;
    }

}
