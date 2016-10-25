package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumResponsavelPreencherFormatacaoItem implements BaseEnum<String> {
    FORNECEDOR("F", "Fornecedor"), BENEFICIARIO("B", "Beneficiário"), AMBOS("A", "Ambos");

    private String valor;
    private String descricao;

    private EnumResponsavelPreencherFormatacaoItem(String valor, String descricao) {
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
