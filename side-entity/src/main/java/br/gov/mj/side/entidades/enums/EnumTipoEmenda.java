package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoEmenda implements BaseEnum<String> {
    INDIVIDUAL("I", "Individual"), BANCADA("B", "Bancada"), RELATOR("R", "Relator");

    private String valor;
    private String descricao;

    private EnumTipoEmenda(String valor, String descricao) {
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
