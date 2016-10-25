package br.gov.mj.side.entidades.programa.licitacao.contrato;

import java.time.LocalDateTime;
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
import br.gov.mj.side.entidades.enums.EnumStatusComunicacaoOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;

@Entity
@Table(name = "tb_ofc_ordem_fornecimento_contrato", schema = "side")
public class OrdemFornecimentoContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ofc_id_ordem_fornecimento_contrato")
    @SequenceGenerator(name = "seq_tb_ofc_ordem_fornecimento_contrato_generator", sequenceName = "side.seq_tb_ofc_ordem_fornecimento_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_ofc_ordem_fornecimento_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ofc_fk_con_id_contrato")
    @NotNull
    private Contrato contrato;

    @Column(name = "ofc_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "ofc_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ofc_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ofc_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ofc_dt_data_comunicacao")
    private LocalDateTime dataComunicacao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento", name = "enumClassName"))
    @Column(name = "ofc_st_status_ordem_fornecimento")
    private EnumStatusOrdemFornecimento statusOrdemFornecimento;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusComunicacaoOrdemFornecimento", name = "enumClassName"))
    @Column(name = "ofc_st_status_comunicacao_ordem_fornecimento")
    private EnumStatusComunicacaoOrdemFornecimento statusComunicacaoOrdemFornecimento;

    @OneToMany(mappedBy = "ordemFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ItensOrdemFornecimentoContrato> listaItensOrdemFornecimento = new ArrayList<ItensOrdemFornecimentoContrato>();

    @OneToMany(mappedBy = "ordemFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaHistoricoComunicacaoGeracao = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();

    @OneToMany(mappedBy = "ordemFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContrato = new ArrayList<ObjetoFornecimentoContrato>();

    @OneToMany(mappedBy = "ordemFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<NotaRemessaOrdemFornecimentoContrato> listaNotaRemessaOrdemFornecimento = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();

    @Override
    public Long getId() {
        return id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public List<ItensOrdemFornecimentoContrato> getListaItensOrdemFornecimento() {
        return listaItensOrdemFornecimento;
    }

    public void setListaItensOrdemFornecimento(List<ItensOrdemFornecimentoContrato> listaItensOrdemFornecimento) {
        this.listaItensOrdemFornecimento = listaItensOrdemFornecimento;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> getListaHistoricoComunicacaoGeracao() {
        return listaHistoricoComunicacaoGeracao;
    }

    public void setListaHistoricoComunicacaoGeracao(List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaHistoricoComunicacaoGeracao) {
        this.listaHistoricoComunicacaoGeracao = listaHistoricoComunicacaoGeracao;
    }

    public List<ObjetoFornecimentoContrato> getListaObjetoFornecimentoContrato() {
        return listaObjetoFornecimentoContrato;
    }

    public void setListaObjetoFornecimentoContrato(List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContrato) {
        this.listaObjetoFornecimentoContrato = listaObjetoFornecimentoContrato;
    }

    public static Comparator<OrdemFornecimentoContrato> getComparator(String coluna) {
        return new Comparator<OrdemFornecimentoContrato>() {
            @Override
            public int compare(OrdemFornecimentoContrato o1, OrdemFornecimentoContrato o2) {
                int valor = 0;
                valor = o1.getId().compareTo(o2.getId()) * 1;
                return valor;
            }
        };
    }

    public List<NotaRemessaOrdemFornecimentoContrato> getListaNotaRemessaOrdemFornecimento() {
        return listaNotaRemessaOrdemFornecimento;
    }

    public void setListaNotaRemessaOrdemFornecimento(List<NotaRemessaOrdemFornecimentoContrato> listaNotaRemessaOrdemFornecimento) {
        this.listaNotaRemessaOrdemFornecimento = listaNotaRemessaOrdemFornecimento;
    }

    public EnumStatusOrdemFornecimento getStatusOrdemFornecimento() {
        return statusOrdemFornecimento;
    }

    public void setStatusOrdemFornecimento(EnumStatusOrdemFornecimento statusOrdemFornecimento) {
        this.statusOrdemFornecimento = statusOrdemFornecimento;
    }

    public EnumStatusComunicacaoOrdemFornecimento getStatusComunicacaoOrdemFornecimento() {
        return statusComunicacaoOrdemFornecimento;
    }

    public void setStatusComunicacaoOrdemFornecimento(EnumStatusComunicacaoOrdemFornecimento statusComunicacaoOrdemFornecimento) {
        this.statusComunicacaoOrdemFornecimento = statusComunicacaoOrdemFornecimento;
    }

    public LocalDateTime getDataComunicacao() {
        return dataComunicacao;
    }

    public void setDataComunicacao(LocalDateTime dataComunicacao) {
        this.dataComunicacao = dataComunicacao;
    }
}
