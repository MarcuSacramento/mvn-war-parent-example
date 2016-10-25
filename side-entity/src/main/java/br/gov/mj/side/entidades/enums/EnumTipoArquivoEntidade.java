package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoArquivoEntidade implements BaseEnum<String> {
    COMPROVANTE_ENDERECO("C", "Comprovante de Endereço"), DEFINICAO_COMPETENCIA_SERVIDOR("D", "Definição de Competência do Servidor"), DOCUMENTACAO_PESSOAL("P", "Documentação Pessoal"), NORMA_INSTITUTIVA("N", "Norma Institutiva"),INDICACAO_PREPOSTO("I", "Indicação Preposto");

    private String valor;
    private String descricao;

    private EnumTipoArquivoEntidade(String valor, String descricao) {
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
