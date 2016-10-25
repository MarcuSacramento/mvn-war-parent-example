package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSituacaoAvaliacaoPreliminarPreenchimentoItem implements BaseEnum<String> {

    EM_CONFORMIDADE("CONF", "Em Conformidade"), NAO_CONFORMIDADE("NCON", "Não Conformidade"), SEM_AVALIACAO("SAVA","Sem Avaliação");

    private String valor;
    private String descricao;

    private EnumSituacaoAvaliacaoPreliminarPreenchimentoItem(String valor, String descricao) {
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
