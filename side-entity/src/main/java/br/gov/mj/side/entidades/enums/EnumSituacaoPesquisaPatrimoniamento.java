package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSituacaoPesquisaPatrimoniamento implements BaseEnum<String> {
    ENTREGUE("ENTR", "Entregue"), RECEBIDO("RECE", "Recebido"), DOADO("DOAD", "Doado");

    private String valor;
    private String descricao;

    private EnumSituacaoPesquisaPatrimoniamento(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    @Override
    public String getValor() {
        return this.valor;
    }

    public String getDescricao() {
        return this.descricao;
    }

}
