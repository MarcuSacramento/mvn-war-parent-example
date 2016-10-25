package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoResposta implements BaseEnum<String> {
    TEXTO("T", "Texto"), NUMERICO("N", "Numérico"), LISTA_SELECAO("L", "Lista de Seleção");

    private String valor;
    private String descricao;

    private EnumTipoResposta(String valor, String descricao) {
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
