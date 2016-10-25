package br.gov.mj.side.entidades.programa;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;

@Entity
@Table(name = "tb_hpb_historico_publicizacao", schema = "side")
public class ProgramaHistoricoPublicizacao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "hpb_id_historico_publicizacao")
    @SequenceGenerator(name = "tb_hpb_historico_publicizacao_generator", sequenceName = "side.seq_tb_hpb_historico_publicizacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_hpb_historico_publicizacao_generator")
    @NotNull
    private Long id;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_publicacao_dou")
    private LocalDate dataPublicacaoDOU;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_inicial_proposta")
    private LocalDate dataInicialProposta;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_final_proposta")
    private LocalDate dataFinalProposta;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoPrograma", name = "enumClassName"))
    @Column(name = "hpb_tp_tipo_programa")
    @NotNull
    private EnumTipoPrograma tipoPrograma;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "hpb_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "hpb_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @ManyToOne
    @JoinColumn(name = "hpb_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusPrograma", name = "enumClassName"))
    @Column(name = "hpb_st_status_programa")
    @NotNull
    private EnumStatusPrograma statusPrograma;

    @Column(name = "hpb_ds_descricao_motivo")
    private String motivo;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_inicial_analise")
    private LocalDate dataInicialAnalise;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_final_analise")
    private LocalDate dataFinalAnalise;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_inicial_recurso_elegibilidade")
    private LocalDate dataInicialRecursoElegibilidade;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_final_recurso_elegibilidade")
    private LocalDate dataFinalRecursoElegibilidade;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_inicial_recurso_avaliacao")
    private LocalDate dataInicialRecursoAvaliacao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_final_recurso_avaliacao")
    private LocalDate dataFinalRecursoAvaliacao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_inicial_cadastro_local_entrega")
    private LocalDate dataInicialCadastroLocalEntrega;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_final_cadastro_local_entrega")
    private LocalDate dataFinalCadastroLocalEntrega;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_inicial_vigencia_contrato")
    private LocalDate dataInicialVigenciaContrato;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_final_vigencia_contrato")
    private LocalDate dataFinalVigenciaContrato;
    
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_comunicacao_ordem_fornecimento")
    private LocalDate dataComunicacaoOrdemFornecimento;
    
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hpb_dt_data_cancelamento_ordem_fornecimento")
    private LocalDate dataCancelamentoOrdemFornecimento;

    @ManyToOne
    @JoinColumn(name = "hpb_fk_con_id_contrato")
    private Contrato contrato;
    
    @ManyToOne
    @JoinColumn(name = "hpb_fk_ofc_id_ordem_fornecimento_contrato")
    private OrdemFornecimentoContrato ordemFornecimento;

    public ProgramaHistoricoPublicizacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public LocalDate getDataPublicacaoDOU() {
        return dataPublicacaoDOU;
    }

    public void setDataPublicacaoDOU(LocalDate dataPublicacaoDOU) {
        this.dataPublicacaoDOU = dataPublicacaoDOU;
    }

    public LocalDate getDataInicialProposta() {
        return dataInicialProposta;
    }

    public void setDataInicialProposta(LocalDate dataInicialProposta) {
        this.dataInicialProposta = dataInicialProposta;
    }

    public LocalDate getDataFinalProposta() {
        return dataFinalProposta;
    }

    public void setDataFinalProposta(LocalDate dataFinalProposta) {
        this.dataFinalProposta = dataFinalProposta;
    }

    public EnumTipoPrograma getTipoPrograma() {
        return tipoPrograma;
    }

    public void setTipoPrograma(EnumTipoPrograma tipoPrograma) {
        this.tipoPrograma = tipoPrograma;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumStatusPrograma getStatusPrograma() {
        return statusPrograma;
    }

    public void setStatusPrograma(EnumStatusPrograma statusPrograma) {
        this.statusPrograma = statusPrograma;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getDataInicialAnalise() {
        return dataInicialAnalise;
    }

    public void setDataInicialAnalise(LocalDate dataInicialAnalise) {
        this.dataInicialAnalise = dataInicialAnalise;
    }

    public LocalDate getDataFinalAnalise() {
        return dataFinalAnalise;
    }

    public void setDataFinalAnalise(LocalDate dataFinalAnalise) {
        this.dataFinalAnalise = dataFinalAnalise;
    }

    public LocalDate getDataInicialRecursoElegibilidade() {
        return dataInicialRecursoElegibilidade;
    }

    public void setDataInicialRecursoElegibilidade(LocalDate dataInicialRecursoElegibilidade) {
        this.dataInicialRecursoElegibilidade = dataInicialRecursoElegibilidade;
    }

    public LocalDate getDataFinalRecursoElegibilidade() {
        return dataFinalRecursoElegibilidade;
    }

    public void setDataFinalRecursoElegibilidade(LocalDate dataFinalRecursoElegibilidade) {
        this.dataFinalRecursoElegibilidade = dataFinalRecursoElegibilidade;
    }

    public LocalDate getDataInicialRecursoAvaliacao() {
        return dataInicialRecursoAvaliacao;
    }

    public void setDataInicialRecursoAvaliacao(LocalDate dataInicialRecursoAvaliacao) {
        this.dataInicialRecursoAvaliacao = dataInicialRecursoAvaliacao;
    }

    public LocalDate getDataFinalRecursoAvaliacao() {
        return dataFinalRecursoAvaliacao;
    }

    public void setDataFinalRecursoAvaliacao(LocalDate dataFinalRecursoAvaliacao) {
        this.dataFinalRecursoAvaliacao = dataFinalRecursoAvaliacao;
    }

    public LocalDate getDataInicialCadastroLocalEntrega() {
        return dataInicialCadastroLocalEntrega;
    }

    public void setDataInicialCadastroLocalEntrega(LocalDate dataInicialCadastroLocalEntrega) {
        this.dataInicialCadastroLocalEntrega = dataInicialCadastroLocalEntrega;
    }

    public LocalDate getDataFinalCadastroLocalEntrega() {
        return dataFinalCadastroLocalEntrega;
    }

    public void setDataFinalCadastroLocalEntrega(LocalDate dataFinalCadastroLocalEntrega) {
        this.dataFinalCadastroLocalEntrega = dataFinalCadastroLocalEntrega;
    }

    public LocalDate getDataInicialVigenciaContrato() {
        return dataInicialVigenciaContrato;
    }

    public void setDataInicialVigenciaContrato(LocalDate dataInicialVigenciaContrato) {
        this.dataInicialVigenciaContrato = dataInicialVigenciaContrato;
    }

    public LocalDate getDataFinalVigenciaContrato() {
        return dataFinalVigenciaContrato;
    }

    public void setDataFinalVigenciaContrato(LocalDate dataFinalVigenciaContrato) {
        this.dataFinalVigenciaContrato = dataFinalVigenciaContrato;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public OrdemFornecimentoContrato getOrdemFornecimento() {
        return ordemFornecimento;
    }

    public void setOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        this.ordemFornecimento = ordemFornecimento;
    }

    public LocalDate getDataComunicacaoOrdemFornecimento() {
        return dataComunicacaoOrdemFornecimento;
    }

    public void setDataComunicacaoOrdemFornecimento(LocalDate dataComunicacaoOrdemFornecimento) {
        this.dataComunicacaoOrdemFornecimento = dataComunicacaoOrdemFornecimento;
    }

    public LocalDate getDataCancelamentoOrdemFornecimento() {
        return dataCancelamentoOrdemFornecimento;
    }

    public void setDataCancelamentoOrdemFornecimento(LocalDate dataCancelamentoOrdemFornecimento) {
        this.dataCancelamentoOrdemFornecimento = dataCancelamentoOrdemFornecimento;
    }
}
