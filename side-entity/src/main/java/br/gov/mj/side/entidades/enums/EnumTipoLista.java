package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoLista implements BaseEnum<String> {
    PRELIMINAR("P", "Preliminar"), DEFINITIVA("D", "Definitiva");

    private String valor;
    private String descricao;

    private EnumTipoLista(String valor, String descricao) {
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
