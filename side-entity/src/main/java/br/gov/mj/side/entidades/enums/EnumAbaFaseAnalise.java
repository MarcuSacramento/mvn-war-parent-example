package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumAbaFaseAnalise implements BaseEnum<String> {
    ELEGIBILIDADE("E", "Elegibilidade"), CLASSIFICACAO("C", "Classificação");

    private String valor;
    private String descricao;

    private EnumAbaFaseAnalise(String valor, String descricao) {
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
