package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSituacaoBem implements BaseEnum<String> {

    NAO_ENTREGUE("NAOE", "Não entregue"), ENTREGUE("ENTR", "Entregue"), RECEBIDO("RECE", "Recebido"), DOADO("DOAD","Doado");

    private String valor;
    private String descricao;

    private EnumSituacaoBem(String valor, String descricao) {
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
