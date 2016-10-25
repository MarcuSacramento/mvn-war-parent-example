package br.gov.mj.side.web.view.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSelecao implements BaseEnum<String> {
    PROPOSTAS_ELEGIVEIS("PE", "Propostas Elegíveis"), CLASSIFICACAO_PROSPOSTA("CP", "Classificação das Propostas");

    private String valor;
    private String descricao;

    private EnumSelecao(String valor, String descricao) {
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
