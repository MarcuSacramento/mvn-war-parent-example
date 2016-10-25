package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusRepresentante implements BaseEnum<String> {
    ATIVA("A", "Ativo"), INATIVO("I", "Inativo"), EXPIRADO("E", "Expirado"), BLOQUEADO_POR_INATIVIDADE("B", "Bloqueado por Inatividade");

    private String valor;
    private String descricao;

    private EnumStatusRepresentante(String valor, String descricao) {
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
