package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumMes implements BaseEnum<String> {

    JANEIRO(1, "Janeiro"), FEVEREIRO(2, "Fevereiro"), MARCO(3, "Março"), ABRIL(4, "Abril"), MAIO(5, "Maio"), JUNHO(6, "Junho"), JULHO(7, "Julho"), AGOSTO(8, "Agosto"), SETEMBRO(9, "Setembro"), OUTUBRO(10, "Outubro"), NOVEMBRO(11, "Novembro"), DEZEMBRO(12, "Dezembro");

    private Integer codigo;
    private String valor;

    private EnumMes(Integer codigo, String valor) {
        this.codigo = codigo;
        this.valor = valor;
    }

    @Override
    public String getValor() {
        return this.valor;
    }

    public Integer getCodigo() {
        return this.codigo;
    }

    public String getCodigoTexto() {
        if (getCodigo() < 10) {
            return "0" + getCodigo();
        }
        return getCodigo().toString();
    }

    public static EnumMes getBuscarPorCodigo(Integer codigo) {
        for (EnumMes mes : EnumMes.values()) {
            if (mes.getCodigo() == codigo) {
                return mes;
            }
        }
        return null;
    }
}
