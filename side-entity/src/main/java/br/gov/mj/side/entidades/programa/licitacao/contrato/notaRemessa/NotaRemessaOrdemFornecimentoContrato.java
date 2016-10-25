package br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaContratante;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaFornecedor;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;

@Entity
@Table(name = "tb_nrc_nota_remessa_ordem_fornecimento_contrato", schema = "side")
public class NotaRemessaOrdemFornecimentoContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "nrc_id_nota_remessa_ordem_fornecimento_contrato")
    @SequenceGenerator(name = "seq_tb_nrc_nota_remessa_ordem_fornecimento_contrato_generator", sequenceName = "side.seq_tb_nrc_nota_remessa_ordem_fornecimento_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_nrc_nota_remessa_ordem_fornecimento_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nrc_fk_ofc_id_ordem_fornecimento_contrato")
    @NotNull
    private OrdemFornecimentoContrato ordemFornecimento;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "nrc_dt_data_prevista_entrega")
    @NotNull
    private LocalDate dataPrevistaEntrega;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "nrc_dt_data_efetiva_entrega")
    private LocalDate dataEfetivaEntrega;

    @Column(name = "nrc_nu_numero_nota_remessa")
    @NotNull
    private String numeroNotaRemessa;

    @Column(name = "nrc_co_codigo_gerado")
    private String codigoGerado;
    
    @Column(name = "nrc_co_codigo_informado_fornecedor")
    private String codigoInformadoPeloFornecedor;

    @OneToMany(mappedBy = "notaRemessaOrdemFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ItensNotaRemessaOrdemFornecimentoContrato> listaItensNotaRemessaOrdemFornecimentoContratos = new ArrayList<ItensNotaRemessaOrdemFornecimentoContrato>();
    
    @OneToMany(mappedBy = "notaRemessaOrdemFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<AnexoNotaRemessa> listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();

    @OneToMany(mappedBy = "notaRemessaOrdemFornecimentoContrato", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<TermoRecebimentoDefinitivo> listaTermoRecebimentoDefinitivo = new ArrayList<TermoRecebimentoDefinitivo>();
    
    @OneToMany(mappedBy = "notaRemessaOrdemFornecimentoContrato")
    private List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContrato = new ArrayList<ObjetoFornecimentoContrato>();

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaFornecedor", name = "enumClassName"))
    @Column(name = "nrc_st_status_execucao_fornecedor")
    private EnumStatusExecucaoNotaRemessaFornecedor statusExecucaoFornecedor;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario", name = "enumClassName"))
    @Column(name = "nrc_st_status_execucao_beneficiario")
    private EnumStatusExecucaoNotaRemessaBeneficiario statusExecucaoBeneficiario;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaContratante", name = "enumClassName"))
    @Column(name = "nrc_st_status_execucao_contratante")
    private EnumStatusExecucaoNotaRemessaContratante statusExecucaoContratante;
    
    @Column(name = "nrc_bo_nota_de_devolucao")
    private Boolean notaDevolucao;    

    @Override
    public Long getId() {
        return id;
    }

    public OrdemFornecimentoContrato getOrdemFornecimento() {
        return ordemFornecimento;
    }

    public void setOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        this.ordemFornecimento = ordemFornecimento;
    }

    public LocalDate getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) {
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    public LocalDate getDataEfetivaEntrega() {
        return dataEfetivaEntrega;
    }

    public void setDataEfetivaEntrega(LocalDate dataEfetivaEntrega) {
        this.dataEfetivaEntrega = dataEfetivaEntrega;
    }

    public String getNumeroNotaRemessa() {
        return numeroNotaRemessa;
    }

    public void setNumeroNotaRemessa(String numeroNotaRemessa) {
        this.numeroNotaRemessa = numeroNotaRemessa;
    }

    public String getCodigoGerado() {
        return codigoGerado;
    }

    public void setCodigoGerado(String codigoGerado) {
        this.codigoGerado = codigoGerado;
    }

    public List<AnexoNotaRemessa> getListaAnexosNotaRemessa() {
        return listaAnexosNotaRemessa;
    }

    public void setListaAnexosNotaRemessa(List<AnexoNotaRemessa> listaAnexosNotaRemessa) {
        this.listaAnexosNotaRemessa = listaAnexosNotaRemessa;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItensNotaRemessaOrdemFornecimentoContrato> getListaItensNotaRemessaOrdemFornecimentoContratos() {
        return listaItensNotaRemessaOrdemFornecimentoContratos;
    }

    public void setListaItensNotaRemessaOrdemFornecimentoContratos(List<ItensNotaRemessaOrdemFornecimentoContrato> listaItensNotaRemessaOrdemFornecimentoContratos) {
        this.listaItensNotaRemessaOrdemFornecimentoContratos = listaItensNotaRemessaOrdemFornecimentoContratos;
    }

    public static Comparator<NotaRemessaOrdemFornecimentoContrato> getComparator(int order, String coluna) {
        return new Comparator<NotaRemessaOrdemFornecimentoContrato>() {
            @Override
            public int compare(NotaRemessaOrdemFornecimentoContrato o1, NotaRemessaOrdemFornecimentoContrato o2) {
                int valor = 0;

                if ("notaRemessaOrdemFornecimento.numeroNotaRemessa".equalsIgnoreCase(coluna)) {
                    valor = o1.getNumeroNotaRemessa().toUpperCase().compareTo(o2.getNumeroNotaRemessa().toUpperCase()) * order;
                } else if ("notaRemessaOrdemFornecimento.dataPrevistaEntrega".equalsIgnoreCase(coluna)) {
                    valor = o1.getDataPrevistaEntrega().compareTo(o2.getDataPrevistaEntrega()) * order;
                }
                return valor;
            }
        };
    }

    public EnumStatusExecucaoNotaRemessaFornecedor getStatusExecucaoFornecedor() {
        return statusExecucaoFornecedor;
    }

    public void setStatusExecucaoFornecedor(EnumStatusExecucaoNotaRemessaFornecedor statusExecucaoFornecedor) {
        this.statusExecucaoFornecedor = statusExecucaoFornecedor;
    }

    public EnumStatusExecucaoNotaRemessaBeneficiario getStatusExecucaoBeneficiario() {
        return statusExecucaoBeneficiario;
    }

    public void setStatusExecucaoBeneficiario(EnumStatusExecucaoNotaRemessaBeneficiario statusExecucaoBeneficiario) {
        this.statusExecucaoBeneficiario = statusExecucaoBeneficiario;
    }

    public EnumStatusExecucaoNotaRemessaContratante getStatusExecucaoContratante() {
        return statusExecucaoContratante;
    }

    public void setStatusExecucaoContratante(EnumStatusExecucaoNotaRemessaContratante statusExecucaoContratante) {
        this.statusExecucaoContratante = statusExecucaoContratante;
    }

    public List<TermoRecebimentoDefinitivo> getListaTermoRecebimentoDefinitivo() {
        return listaTermoRecebimentoDefinitivo;
    }

    public void setListaTermoRecebimentoDefinitivo(List<TermoRecebimentoDefinitivo> listaTermoRecebimentoDefinitivo) {
        this.listaTermoRecebimentoDefinitivo = listaTermoRecebimentoDefinitivo;
    }

    public String getCodigoInformadoPeloFornecedor() {
        return codigoInformadoPeloFornecedor;
    }

    public void setCodigoInformadoPeloFornecedor(String codigoInformadoPeloFornecedor) {
        this.codigoInformadoPeloFornecedor = codigoInformadoPeloFornecedor;
    }

    public Boolean getNotaDevolucao() {
        return notaDevolucao;
    }

    public void setNotaDevolucao(Boolean notaDevolucao) {
        this.notaDevolucao = notaDevolucao;
    }

    public List<ObjetoFornecimentoContrato> getListaObjetoFornecimentoContrato() {
        return listaObjetoFornecimentoContrato;
    }

    public void setListaObjetoFornecimentoContrato(List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContrato) {
        this.listaObjetoFornecimentoContrato = listaObjetoFornecimentoContrato;
    }
}
