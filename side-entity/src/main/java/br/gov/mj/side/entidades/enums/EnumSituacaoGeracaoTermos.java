package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSituacaoGeracaoTermos implements BaseEnum<String> {

    NAO_GERADO("NAOG", "Nenhum termo gerado"), TERMO_RECEBIMENTO_DEFINITIVO_GERADO("TERG", "Termo de recebimento definitivo gerado"), TERMO_DOACAO_GERADO("TEDG", "Termo de doação gerado"), DOACAO_CONCLUIDA("CONC","Doação Concluída");

    private String valor;
    private String descricao;

    private EnumSituacaoGeracaoTermos(String valor, String descricao) {
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
