package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusInscricao implements BaseEnum<String> {
    EM_ELABORACAO("ELAB", "Em elaboração"),ENVIADA_ANALISE("ENVA", "Enviada para Análise"), ANALISE_ELEGIBILIDADE("ANEL", "Em análise de Elegibilidade"),CONCLUIDA_ANALISE_ELEGIBILIDADE("CANE", "Análise de Elegibilidade Concluída"), ANALISE_AVALIACAO("ANAV", "Em análise de Avaliação"),CONCLUIDA_ANALISE_AVALIACAO("CANA", "Análise de Avaliação Concluída"),FINALIZADA("FINA", "Finalizada");

    private String valor;
    private String descricao;

    private EnumStatusInscricao(String valor, String descricao) {
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
