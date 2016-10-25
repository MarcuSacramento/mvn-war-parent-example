package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSiglaSistema implements BaseEnum<String> {
    SIDE("SIDE", "SIDE");

    private String valor;
    private String descricao;

    private EnumSiglaSistema(String valor, String descricao) {
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
