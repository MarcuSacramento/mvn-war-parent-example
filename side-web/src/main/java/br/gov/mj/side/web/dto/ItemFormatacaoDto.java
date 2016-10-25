package br.gov.mj.side.web.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;

public class ItemFormatacaoDto {

    private Bem item;
    private List<FormatacaoItensContrato> listaFormatacao = new ArrayList<FormatacaoItensContrato>();

    public Bem getItem() {
        return item;
    }

    public void setItem(Bem item) {
        this.item = item;
    }

    public List<FormatacaoItensContrato> getListaFormatacao() {
        return listaFormatacao;
    }

    public void setListaFormatacao(List<FormatacaoItensContrato> listaFormatacao) {
        this.listaFormatacao = listaFormatacao;
    }

}
