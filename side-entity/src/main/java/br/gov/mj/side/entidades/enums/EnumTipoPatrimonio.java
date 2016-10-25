package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoPatrimonio implements BaseEnum<String> {
    UNICO("UNIC", "Patrimônio Único"), MULTIPLO("MULT", "Patrimonio Múltiplo"), NAO_PATRIMONIAVEL("NAOP","Não patrimoniável");

    private String valor;
    private String descricao;

    private EnumTipoPatrimonio(String valor, String descricao) {
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
