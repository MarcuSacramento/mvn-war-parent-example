package br.gov.mj.side.web.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;

public class ListaLoteUnitariaDto {

    private ItensOrdemFornecimentoContrato itemOf;
    private List<FormatacaoItensContrato> listaLote = new ArrayList<FormatacaoItensContrato>();
    private List<FormatacaoItensContrato> listaUnitaria = new ArrayList<FormatacaoItensContrato>();

    public ItensOrdemFornecimentoContrato getItemOf() {
        return itemOf;
    }

    public void setItemOf(ItensOrdemFornecimentoContrato itemOf) {
        this.itemOf = itemOf;
    }

    public List<FormatacaoItensContrato> getListaLote() {
        return listaLote;
    }

    public void setListaLote(List<FormatacaoItensContrato> listaLote) {
        this.listaLote = listaLote;
    }

    public List<FormatacaoItensContrato> getListaUnitaria() {
        return listaUnitaria;
    }

    public void setListaUnitaria(List<FormatacaoItensContrato> listaUnitaria) {
        this.listaUnitaria = listaUnitaria;
    }

}
