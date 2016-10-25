package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;

public class ItensOrdemFornecimentoPorEntidadeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Entidade entidade;
    private Municipio municipio;
    private LocalEntregaEntidade localEntrega;
    private OrdemFornecimentoContrato ordemFornecimento;
    private List<ItensDaOfPorLocalEntregaDto> listaItens = new ArrayList<ItensDaOfPorLocalEntregaDto>();
    private String notaRemessa;
    private LocalDate dataPrevisaoEntrega;
    private LocalDate dataEfetivaEntrega;

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public List<ItensDaOfPorLocalEntregaDto> getListaItens() {
        return listaItens;
    }

    public void setListaItens(List<ItensDaOfPorLocalEntregaDto> listaItens) {
        this.listaItens = listaItens;
    }

    public LocalEntregaEntidade getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(LocalEntregaEntidade localEntrega) {
        this.localEntrega = localEntrega;
    }

    public OrdemFornecimentoContrato getOrdemFornecimento() {
        return ordemFornecimento;
    }

    public void setOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        this.ordemFornecimento = ordemFornecimento;
    }

    public String getNotaRemessa() {
        return notaRemessa;
    }

    public void setNotaRemessa(String notaRemessa) {
        this.notaRemessa = notaRemessa;
    }

    public LocalDate getDataPrevisaoEntrega() {
        return dataPrevisaoEntrega;
    }

    public void setDataPrevisaoEntrega(LocalDate dataPrevisaoEntrega) {
        this.dataPrevisaoEntrega = dataPrevisaoEntrega;
    }

    public LocalDate getDataEfetivaEntrega() {
        return dataEfetivaEntrega;
    }

    public void setDataEfetivaEntrega(LocalDate dataEfetivaEntrega) {
        this.dataEfetivaEntrega = dataEfetivaEntrega;
    }
}
