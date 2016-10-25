package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusContrato implements BaseEnum<String> {
    NAO_EXECUTADO("NEXE", "Não executado"), EM_EXECUCAO("EXEC", "Em execução"), CONCLUIDO("CONC","Concluído"), FINALIZADO("FINA","Finalizado");

    private String valor;
    private String descricao;

    private EnumStatusContrato(String valor, String descricao) {
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
