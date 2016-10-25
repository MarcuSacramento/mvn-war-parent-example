package br.gov.mj.side.entidades.programa.licitacao.contrato;

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
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.enums.EnumStatusContrato;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;

@Entity
@Table(name = "tb_con_contrato", schema = "side")
public class Contrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "con_id_contrato")
    @SequenceGenerator(name = "seq_tb_con_contrato", sequenceName = "side.seq_tb_con_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_con_contrato")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "con_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "con_nu_numero")
    @NotNull
    private String numeroContrato;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "con_dt_vigencia_inicio")
    @NotNull
    private LocalDate dataVigenciaInicioContrato;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "con_dt_vigencia_fim")
    @NotNull
    private LocalDate dataVigenciaFimContrato;

    @Column(name = "con_nu_numero_processo_sei")
    @NotNull
    private String numeroProcessoSEI;

    @OneToMany(mappedBy = "contrato")
    private List<AgrupamentoLicitacao> listaAgrupamentosLicitacao = new ArrayList<AgrupamentoLicitacao>();

    @ManyToOne
    @JoinColumn(name = "con_id_fk_ent_entidade")
    @NotNull
    private Entidade fornecedor;

    @ManyToOne
    @JoinColumn(name = "con_id_fk_pso_id_pessoa_representante")
    @NotNull
    private Pessoa representanteLegal;

    @ManyToOne
    @JoinColumn(name = "con_id_fk_pso_id_pessoa_preposto")
    @NotNull
    private Pessoa preposto;

    @OneToMany(mappedBy = "contrato", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ContratoAnexo> anexos = new ArrayList<ContratoAnexo>();

    @Column(name = "con_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "con_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "con_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "con_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @OneToMany(mappedBy = "contrato", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<FormatacaoContrato> listaFormatacao = new ArrayList<FormatacaoContrato>();

    @OneToMany(mappedBy = "contrato", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<OrdemFornecimentoContrato> listaOrdemFornecimento = new ArrayList<OrdemFornecimentoContrato>();
    
    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusContrato", name = "enumClassName"))
    @Column(name = "con_st_status_contrato")
    private EnumStatusContrato statusContrato;

    @Override
    public Long getId() {
        return id;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }

    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }

    public List<AgrupamentoLicitacao> getListaAgrupamentosLicitacao() {
        return listaAgrupamentosLicitacao;
    }

    public void setListaAgrupamentosLicitacao(List<AgrupamentoLicitacao> listaAgrupamentosLicitacao) {
        this.listaAgrupamentosLicitacao = listaAgrupamentosLicitacao;
    }

    public Entidade getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Entidade fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Pessoa getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(Pessoa representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public Pessoa getPreposto() {
        return preposto;
    }

    public void setPreposto(Pessoa preposto) {
        this.preposto = preposto;
    }

    public List<ContratoAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ContratoAnexo> anexos) {
        this.anexos = anexos;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataVigenciaInicioContrato() {
        return dataVigenciaInicioContrato;
    }

    public void setDataVigenciaInicioContrato(LocalDate dataVigenciaInicioContrato) {
        this.dataVigenciaInicioContrato = dataVigenciaInicioContrato;
    }

    public LocalDate getDataVigenciaFimContrato() {
        return dataVigenciaFimContrato;
    }

    public void setDataVigenciaFimContrato(LocalDate dataVigenciaFimContrato) {
        this.dataVigenciaFimContrato = dataVigenciaFimContrato;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
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

    public List<FormatacaoContrato> getListaFormatacao() {
        return listaFormatacao;
    }

    public void setListaFormatacao(List<FormatacaoContrato> listaFormatacao) {
        this.listaFormatacao = listaFormatacao;
    }

    public List<OrdemFornecimentoContrato> getListaOrdemFornecimento() {
        return listaOrdemFornecimento;
    }

    public void setListaOrdemFornecimento(List<OrdemFornecimentoContrato> listaOrdemFornecimento) {
        this.listaOrdemFornecimento = listaOrdemFornecimento;
    }

    public EnumStatusContrato getStatusContrato() {
        return statusContrato;
    }

    public void setStatusContrato(EnumStatusContrato statusContrato) {
        this.statusContrato = statusContrato;
    }

}
