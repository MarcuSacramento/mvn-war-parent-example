package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusExecucaoNotaRemessaContratante implements BaseEnum<String> {
    AGUARDANDO_RELATORIO_RECEBIMENTO("AGUA", "Aguardando relatório de recebimento"), ACEITO("ACEI", "Aceito"), NAO_ACEITO("NACE", "Não aceito"), ACEITO_COM_RESALVA("RESA", "Aceito com ressalva"),RELATORIO_RECEBIMENTO_ENVIADO("RELA", "Relatório de recebimento enviado"), EM_ANALISE("ANAL", "Em análise");

    private String valor;
    private String descricao;

    private EnumStatusExecucaoNotaRemessaContratante(String valor, String descricao) {
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

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
