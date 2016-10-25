package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusPrograma implements BaseEnum<String> {
    EM_ELABORACAO("ELAB", "Em elaboração"), FORMULADO("FORM", "Formulado"), PUBLICADO("PUBL", "Publicado"),ABERTO_REC_PROPOSTAS("ABRP", "Aberto para recebimento de propostas"),SUSPENSO("SUSP", "Suspenso"),SUSPENSO_PRAZO_PROPOSTAS("SUPR", "Suspenso prazo para recebimento de propostas"),EM_ANALISE("ANAL", "Em análise"),ABERTO_REC_LOC_ENTREGA("ARLE", "Aberto para recebimento de locais de entrega"),ABERTO_PLANEJAMENTO_LICITACAO("APLI", "Aberto para planejamento de licitação"),ABERTO_GERACAO_CONTRATO("ACON", "Aberto para geração de contratos"),EM_EXECUCAO("EXEC","Em execução"), ACOMPANHAMENTO("ACOM","Acompanhamento"), CANCELADO("CANC", "Cancelado");

    private String valor;
    private String descricao;

    private EnumStatusPrograma(String valor, String descricao) {
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
