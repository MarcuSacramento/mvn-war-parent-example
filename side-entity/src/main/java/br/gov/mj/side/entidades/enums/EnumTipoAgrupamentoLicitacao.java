package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoAgrupamentoLicitacao implements BaseEnum<String> {
    GRUPO("G", "Grupo"), ITEM("I", "Item");

    private String valor;
    private String descricao;

    private EnumTipoAgrupamentoLicitacao(String valor, String descricao) {
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
