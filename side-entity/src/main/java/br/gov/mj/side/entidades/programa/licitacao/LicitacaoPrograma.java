package br.gov.mj.side.entidades.programa.licitacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import br.gov.mj.side.entidades.enums.EnumStatusLicitacao;
import br.gov.mj.side.entidades.programa.Programa;

@Entity
@Table(name = "tb_lip_licitacao_programa", schema = "side")
public class LicitacaoPrograma extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "lip_id_licitacao_programa")
    @SequenceGenerator(name = "tb_lip_licitacao_programa_generator", sequenceName = "side.seq_tb_lip_licitacao_programa", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_lip_licitacao_programa_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lip_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "lip_ds_objeto")
    @NotNull
    private String objeto;

    @Column(name = "lip_ds_justificativa")
    @NotNull
    private String justificativa;

    @Column(name = "lip_ds_espec_qtd_objeto")
    @NotNull
    private String especificacoesEQuantidadeDoObjeto;

    @Column(name = "lip_ds_recebimento_act_materiais")
    @NotNull
    private String recebimentoEAceitacaoDosMateriais;

    @Column(name = "lip_ds_prazo_local_forma_entrega")
    @NotNull
    private String prazoLocalEFormaDeEntrega;

    @Column(name = "lip_ds_metodologia_avaliacao_act_materiais")
    @NotNull
    private String metodologiaDeAvaliacaoEAceiteDosMateriais;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "lip_dt_data_inicial_periodo_execucao")
    @NotNull
    private LocalDate dataInicialPeriodoExecucao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "lip_dt_data_final_periodo_execucao")
    @NotNull
    private LocalDate dataFinalPeriodoExecucao;

    @Column(name = "lip_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "lip_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "lip_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "lip_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusLicitacao", name = "enumClassName"))
    @Column(name = "lip_st_status_licitacao_programa")
    @NotNull
    private EnumStatusLicitacao statusLicitacao;

    @OneToMany(mappedBy = "licitacaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<AgrupamentoLicitacao> listaAgrupamentoLicitacao = new ArrayList<AgrupamentoLicitacao>();

    @Column(name = "lip_ct_conteudo_minuta_gerada")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] minutaGerada;

    public LicitacaoPrograma() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getEspecificacoesEQuantidadeDoObjeto() {
        return especificacoesEQuantidadeDoObjeto;
    }

    public void setEspecificacoesEQuantidadeDoObjeto(String especificacoesEQuantidadeDoObjeto) {
        this.especificacoesEQuantidadeDoObjeto = especificacoesEQuantidadeDoObjeto;
    }

    public String getRecebimentoEAceitacaoDosMateriais() {
        return recebimentoEAceitacaoDosMateriais;
    }

    public void setRecebimentoEAceitacaoDosMateriais(String recebimentoEAceitacaoDosMateriais) {
        this.recebimentoEAceitacaoDosMateriais = recebimentoEAceitacaoDosMateriais;
    }

    public String getPrazoLocalEFormaDeEntrega() {
        return prazoLocalEFormaDeEntrega;
    }

    public void setPrazoLocalEFormaDeEntrega(String prazoLocalEFormaDeEntrega) {
        this.prazoLocalEFormaDeEntrega = prazoLocalEFormaDeEntrega;
    }

    public String getMetodologiaDeAvaliacaoEAceiteDosMateriais() {
        return metodologiaDeAvaliacaoEAceiteDosMateriais;
    }

    public void setMetodologiaDeAvaliacaoEAceiteDosMateriais(String metodologiaDeAvaliacaoEAceiteDosMateriais) {
        this.metodologiaDeAvaliacaoEAceiteDosMateriais = metodologiaDeAvaliacaoEAceiteDosMateriais;
    }

    public LocalDate getDataInicialPeriodoExecucao() {
        return dataInicialPeriodoExecucao;
    }

    public void setDataInicialPeriodoExecucao(LocalDate dataInicialPeriodoExecucao) {
        this.dataInicialPeriodoExecucao = dataInicialPeriodoExecucao;
    }

    public LocalDate getDataFinalPeriodoExecucao() {
        return dataFinalPeriodoExecucao;
    }

    public void setDataFinalPeriodoExecucao(LocalDate dataFinalPeriodoExecucao) {
        this.dataFinalPeriodoExecucao = dataFinalPeriodoExecucao;
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

    public EnumStatusLicitacao getStatusLicitacao() {
        return statusLicitacao;
    }

    public void setStatusLicitacao(EnumStatusLicitacao statusLicitacao) {
        this.statusLicitacao = statusLicitacao;
    }

    public List<AgrupamentoLicitacao> getListaAgrupamentoLicitacao() {
        return listaAgrupamentoLicitacao;
    }

    public void setListaAgrupamentoLicitacao(List<AgrupamentoLicitacao> listaAgrupamentoLicitacao) {
        this.listaAgrupamentoLicitacao = listaAgrupamentoLicitacao;
    }

    public byte[] getMinutaGerada() {
        return minutaGerada;
    }

    public void setMinutaGerada(byte[] minutaGerada) {
        this.minutaGerada = minutaGerada;
    }

}