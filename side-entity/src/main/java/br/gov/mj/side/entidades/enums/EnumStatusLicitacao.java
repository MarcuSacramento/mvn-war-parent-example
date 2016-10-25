package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusLicitacao implements BaseEnum<String> {
    EM_ELABORACAO("ELAB", "Em elaboração"), FINALIZADA("FINA", "Finalizada");

    private String valor;
    private String descricao;

    private EnumStatusLicitacao(String valor, String descricao) {
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
