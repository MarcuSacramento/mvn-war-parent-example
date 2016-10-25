package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoPessoa implements BaseEnum<String> {
    REPRESENTANTE_ENTIDADE("E", "Representante da Entidade"),
    REPRESENTANTE_FORNECEDOR("F", "Representante de Fornecedor"),
    REPRESENTANTE_LEGAL("R", "Representante Legal"),
    PREPOSTO_FORNECEDOR("P", "Preposto"),
    TITULAR("T", "Titular"),
    MEMBRO_COMISSAO_RECEBIMENTO("M","Membro da Comissão de Recebimento"),
    USUARIO_INTERNO("I", "Usuário Interno");

    private String valor;
    private String descricao;

    private EnumTipoPessoa(String valor, String descricao) {
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
