package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumResultadoFinalAnaliseElegibilidade implements BaseEnum<String> {
    ELEGIVEL("E", "Elegível"),NAO_ELEGIVEL("N", "Não Elegível");

    private String valor;
    private String descricao;

    private EnumResultadoFinalAnaliseElegibilidade(String valor, String descricao) {
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
