package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.gov.mj.side.entidades.AcaoOrcamentaria;

public class AcaoEmendaComSaldoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private AcaoOrcamentaria acaoOrcamentaria;
    private List<EmendaComSaldoDto> listaSaldoEmenda;
    private BigDecimal saldoAcaoOrcamentaria;
    private BigDecimal valorUtilizar;

    public AcaoEmendaComSaldoDto() {

    }

    public AcaoEmendaComSaldoDto(List<EmendaComSaldoDto> listaSaldoEmenda, BigDecimal saldoAcaoOrcamentaria) {
        super();
        this.listaSaldoEmenda = listaSaldoEmenda;
        this.saldoAcaoOrcamentaria = saldoAcaoOrcamentaria;
    }

    public List<EmendaComSaldoDto> getListaSaldoEmenda() {
        return listaSaldoEmenda;
    }

    public void setListaSaldoEmenda(List<EmendaComSaldoDto> listaSaldoEmenda) {
        this.listaSaldoEmenda = listaSaldoEmenda;
    }

    public BigDecimal getSaldoAcaoOrcamentaria() {
        return saldoAcaoOrcamentaria;
    }

    public void setSaldoAcaoOrcamentaria(BigDecimal saldoAcaoOrcamentaria) {
        this.saldoAcaoOrcamentaria = saldoAcaoOrcamentaria;
    }

    public AcaoOrcamentaria getAcaoOrcamentaria() {
        return acaoOrcamentaria;
    }

    public void setAcaoOrcamentaria(AcaoOrcamentaria acaoOrcamentaria) {
        this.acaoOrcamentaria = acaoOrcamentaria;
    }

    public BigDecimal getValorUtilizar() {
        return valorUtilizar;
    }

    public void setValorUtilizar(BigDecimal valorUtilizar) {
        this.valorUtilizar = valorUtilizar;
    }

    /**
     * Somatório de valor a utilizar de todas as emendas
     */
    public BigDecimal getValorTotalEmendas() {
        BigDecimal total = BigDecimal.ZERO;
        for (EmendaComSaldoDto emendaComSaldoDto : listaSaldoEmenda) {
            total = total.add(emendaComSaldoDto.getValorUtilizar() == null ? BigDecimal.ZERO : emendaComSaldoDto.getValorUtilizar());
        }
        return total;
    }

    /**
     * Somatório do valor a utilizar da ação orçamentária + somatório todas as
     * emendas.
     */
    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(valorUtilizar == null ? BigDecimal.ZERO : valorUtilizar);
        total = total.add(getValorTotalEmendas());
        return total;
    }

}
