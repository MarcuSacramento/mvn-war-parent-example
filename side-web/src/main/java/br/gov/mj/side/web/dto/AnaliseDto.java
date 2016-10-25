package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;

public class AnaliseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private EnumAbaFaseAnalise abaClicada;
    private boolean geradaSegundaListaElegibilidade = false;
    private boolean geradaSegundaListaClassificacao = false;
    private EnumStatusInscricao statusInscricao;

    public EnumAbaFaseAnalise getAbaClicada() {
        return abaClicada;
    }

    public void setAbaClicada(EnumAbaFaseAnalise abaClicada) {
        this.abaClicada = abaClicada;
    }

    public boolean isGeradaSegundaListaElegibilidade() {
        return geradaSegundaListaElegibilidade;
    }

    public void setGeradaSegundaListaElegibilidade(boolean geradaSegundaListaElegibilidade) {
        this.geradaSegundaListaElegibilidade = geradaSegundaListaElegibilidade;
    }

    public boolean isGeradaSegundaListaClassificacao() {
        return geradaSegundaListaClassificacao;
    }

    public void setGeradaSegundaListaClassificacao(boolean geradaSegundaListaClassificacao) {
        this.geradaSegundaListaClassificacao = geradaSegundaListaClassificacao;
    }

    public EnumStatusInscricao getStatusInscricao() {
        return statusInscricao;
    }

    public void setStatusInscricao(EnumStatusInscricao statusInscricao) {
        this.statusInscricao = statusInscricao;
    }

}
