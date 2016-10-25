package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoMinuta implements BaseEnum<String> {
    DOC("D", "DOC"), ODT("O", "ODT"), HTML("H", "HTML"), PDF("P","PDF");

    private String valor;
    private String descricao;

    private EnumTipoMinuta(String valor, String descricao) {
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