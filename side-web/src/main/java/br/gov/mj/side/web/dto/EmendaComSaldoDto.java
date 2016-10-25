package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mj.side.entidades.EmendaParlamentar;

public class EmendaComSaldoDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private EmendaParlamentar emendaParlamentar;
    private BigDecimal saldo;
    private BigDecimal valorUtilizar;

    public EmendaComSaldoDto() {

    }

    public EmendaComSaldoDto(EmendaParlamentar emendaParlamentar, BigDecimal saldo) {
        this.emendaParlamentar = emendaParlamentar;
        this.saldo = saldo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public EmendaParlamentar getEmendaParlamentar() {
        return emendaParlamentar;
    }

    public void setEmendaParlamentar(EmendaParlamentar emendaParlamentar) {
        this.emendaParlamentar = emendaParlamentar;
    }

    public BigDecimal getValorUtilizar() {
        return valorUtilizar;
    }

    public void setValorUtilizar(BigDecimal valorUtilizar) {
        this.valorUtilizar = valorUtilizar;
    }

    public String getNomePartidoUF() {
        return emendaParlamentar.getNomeParlamentar() + " - " + emendaParlamentar.getPartidoPolitico().getSiglaPartido() + " / " + emendaParlamentar.getUf().getSiglaUf();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((emendaParlamentar == null) ? 0 : emendaParlamentar.hashCode());
        result = prime * result + ((saldo == null) ? 0 : saldo.hashCode());
        result = prime * result + ((valorUtilizar == null) ? 0 : valorUtilizar.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmendaComSaldoDto other = (EmendaComSaldoDto) obj;
        if (emendaParlamentar == null) {
            if (other.emendaParlamentar != null)
                return false;
        } else if (!emendaParlamentar.equals(other.emendaParlamentar))
            return false;
        if (saldo == null) {
            if (other.saldo != null)
                return false;
        } else if (!saldo.equals(other.saldo))
            return false;
        if (valorUtilizar == null) {
            if (other.valorUtilizar != null)
                return false;
        } else if (!valorUtilizar.equals(other.valorUtilizar))
            return false;
        return true;
    }

}
