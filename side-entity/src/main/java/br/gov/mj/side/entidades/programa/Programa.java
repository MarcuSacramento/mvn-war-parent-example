package br.gov.mj.side.entidades.programa;

import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;

@Entity
@Table(name = "tb_prg_programa", schema = "side")
public class Programa extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prg_id_programa")
    @SequenceGenerator(name = "TB_PRG_PROGRAMA_GENERATOR", sequenceName = "SIDE.SEQ_TB_PRG_PROGRAMA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_PRG_PROGRAMA_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "prg_no_nome_programa")
    @NotNull
    private String nomePrograma;

    @Column(name = "prg_no_nome_fantasia_programa")
    @NotNull
    private String nomeFantasiaPrograma;

    @Column(name = "prg_ds_descricao_programa")
    @NotNull
    private String descricaoPrograma;

    @ManyToOne
    @JoinColumn(name = "prg_fk_suf_id_subfuncao")
    @NotNull
    private SubFuncao subFuncao;

    @ManyToOne
    @JoinColumn(name = "prg_fk_uex_id_unidade_executora")
    @NotNull
    private UnidadeExecutora unidadeExecutora;

    @Column(name = "prg_nu_numero_processo_sei")
    @NotNull
    private String numeroProcessoSEI;

    @Column(name = "prg_bo_possui_limitacao_geografica")
    @NotNull
    private Boolean possuiLimitacaoGeografica;

    @Column(name = "prg_bo_possui_limitacao_municipal_especifica")
    @NotNull
    private Boolean possuiLimitacaoMunicipalEspecifica;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica", name = "enumClassName"))
    @Column(name = "prg_tp_tipo_personalidade_juridica")
    @NotNull
    private EnumPersonalidadeJuridica tipoPersonalidadeJuridica;

    @Column(name = "prg_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "prg_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "prg_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "prg_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusPrograma", name = "enumClassName"))
    @Column(name = "prg_st_status_programa")
    @NotNull
    private EnumStatusPrograma statusPrograma;

    @Column(name = "prg_an_ano_programa")
    @NotNull
    private Integer anoPrograma;

    @Column(name = "prg_id_programa_publicado")
    private Integer identificadorProgramaPublicado;

    @Column(name = "prg_vl_valor_maximo_proposta")
    private BigDecimal valorMaximoProposta;

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaAnexo> anexos = new ArrayList<ProgramaAnexo>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaCriterioAcompanhamento> criteriosAcompanhamento = new ArrayList<ProgramaCriterioAcompanhamento>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaCriterioElegibilidade> criteriosElegibilidade = new ArrayList<ProgramaCriterioElegibilidade>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaKit> programaKits = new ArrayList<ProgramaKit>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaBem> programaBens = new ArrayList<ProgramaBem>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaRecursoFinanceiro> recursosFinanceiros = new ArrayList<ProgramaRecursoFinanceiro>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaPotencialBeneficiarioUf> potenciaisBeneficiariosUf = new ArrayList<ProgramaPotencialBeneficiarioUf>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaHistoricoPublicizacao> historicoPublicizacao = new ArrayList<ProgramaHistoricoPublicizacao>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaCriterioAvaliacao> criteriosAvaliacao = new ArrayList<ProgramaCriterioAvaliacao>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ListaElegibilidadePublicado> listaElegibilidadePublicado = new ArrayList<ListaElegibilidadePublicado>();

    @OneToMany(mappedBy = "programa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ListaAvaliacaoPublicado> listaAvaliacaoPublicado = new ArrayList<ListaAvaliacaoPublicado>();

    public Programa() {
    }

    @Transient
    private String codigoIdentificadorProgramaPublicadoTemp;

    @Transient
    public String getCodigoIdentificadorProgramaPublicado() {

        if (identificadorProgramaPublicado != null && unidadeExecutora != null && unidadeExecutora.getOrgao() != null && unidadeExecutora.getOrgao().getSiglaOrgao() != null && anoPrograma != null) {
            return String.format("%06d", identificadorProgramaPublicado) + "/" + unidadeExecutora.getOrgao().getSiglaOrgao() + "/" + anoPrograma;
        } else {
            return "";
        }
    }
    
    @Transient
    public String getCodigoIdentificadorProgramaPublicadoENomePrograma(){
        if (identificadorProgramaPublicado != null && unidadeExecutora != null && unidadeExecutora.getOrgao() != null && unidadeExecutora.getOrgao().getSiglaOrgao() != null && anoPrograma != null) {
            return String.format("%06d", identificadorProgramaPublicado) + "/" + unidadeExecutora.getOrgao().getSiglaOrgao() + "/" + anoPrograma + " - "+getNomePrograma();
        } else {
            return "";
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public String getNomeFantasiaPrograma() {
        return nomeFantasiaPrograma;
    }

    public void setNomeFantasiaPrograma(String nomeFantasiaPrograma) {
        this.nomeFantasiaPrograma = nomeFantasiaPrograma;
    }

    public String getDescricaoPrograma() {
        return descricaoPrograma;
    }

    public void setDescricaoPrograma(String descricaoPrograma) {
        this.descricaoPrograma = descricaoPrograma;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public UnidadeExecutora getUnidadeExecutora() {
        return unidadeExecutora;
    }

    public void setUnidadeExecutora(UnidadeExecutora unidadeExecutora) {
        this.unidadeExecutora = unidadeExecutora;
    }

    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }

    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }

    public List<ProgramaAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ProgramaAnexo> anexos) {
        this.anexos = anexos;
    }

    public List<ProgramaCriterioAcompanhamento> getCriteriosAcompanhamento() {
        return criteriosAcompanhamento;
    }

    public void setCriteriosAcompanhamento(List<ProgramaCriterioAcompanhamento> criteriosAcompanhamento) {
        this.criteriosAcompanhamento = criteriosAcompanhamento;
    }

    public List<ProgramaCriterioElegibilidade> getCriteriosElegibilidade() {
        return criteriosElegibilidade;
    }

    public void setCriteriosElegibilidade(List<ProgramaCriterioElegibilidade> criteriosElegibilidade) {
        this.criteriosElegibilidade = criteriosElegibilidade;
    }

    public List<ProgramaKit> getProgramaKits() {
        return programaKits;
    }

    public void setProgramaKits(List<ProgramaKit> programaKits) {
        this.programaKits = programaKits;
    }

    public Boolean getPossuiLimitacaoGeografica() {
        return possuiLimitacaoGeografica;
    }

    public void setPossuiLimitacaoGeografica(Boolean possuiLimitacaoGeografica) {
        this.possuiLimitacaoGeografica = possuiLimitacaoGeografica;
    }

    public Boolean getPossuiLimitacaoMunicipalEspecifica() {
        return possuiLimitacaoMunicipalEspecifica;
    }

    public void setPossuiLimitacaoMunicipalEspecifica(Boolean possuiLimitacaoMunicipalEspecifica) {
        this.possuiLimitacaoMunicipalEspecifica = possuiLimitacaoMunicipalEspecifica;
    }

    public EnumPersonalidadeJuridica getTipoPersonalidadeJuridica() {
        return tipoPersonalidadeJuridica;
    }

    public void setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica tipoPersonalidadeJuridica) {
        this.tipoPersonalidadeJuridica = tipoPersonalidadeJuridica;
    }

    public List<ProgramaRecursoFinanceiro> getRecursosFinanceiros() {
        return recursosFinanceiros;
    }

    public void setRecursosFinanceiros(List<ProgramaRecursoFinanceiro> recursosFinanceiros) {
        this.recursosFinanceiros = recursosFinanceiros;
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

    public List<ProgramaBem> getProgramaBens() {
        return programaBens;
    }

    public void setProgramaBens(List<ProgramaBem> programaBens) {
        this.programaBens = programaBens;
    }

    public List<ProgramaPotencialBeneficiarioUf> getPotenciaisBeneficiariosUf() {
        return potenciaisBeneficiariosUf;
    }

    public void setPotenciaisBeneficiariosUf(List<ProgramaPotencialBeneficiarioUf> potenciaisBeneficiariosUf) {
        this.potenciaisBeneficiariosUf = potenciaisBeneficiariosUf;
    }

    public EnumStatusPrograma getStatusPrograma() {
        return statusPrograma;
    }

    public void setStatusPrograma(EnumStatusPrograma statusPrograma) {
        this.statusPrograma = statusPrograma;
    }

    public Integer getAnoPrograma() {
        return anoPrograma;
    }

    public void setAnoPrograma(Integer anoPrograma) {
        this.anoPrograma = anoPrograma;
    }

    public List<ProgramaHistoricoPublicizacao> getHistoricoPublicizacao() {
        return historicoPublicizacao;
    }

    public void setHistoricoPublicizacao(List<ProgramaHistoricoPublicizacao> historicoPublicizacao) {
        this.historicoPublicizacao = historicoPublicizacao;
    }

    public Integer getIdentificadorProgramaPublicado() {
        return identificadorProgramaPublicado;
    }

    public void setIdentificadorProgramaPublicado(Integer identificadorProgramaPublicado) {
        this.identificadorProgramaPublicado = identificadorProgramaPublicado;
    }

    public BigDecimal getValorMaximoProposta() {
        return valorMaximoProposta;
    }

    public void setValorMaximoProposta(BigDecimal valorMaximoProposta) {
        this.valorMaximoProposta = valorMaximoProposta;
    }

    public List<ProgramaCriterioAvaliacao> getCriteriosAvaliacao() {
        return criteriosAvaliacao;
    }

    public void setCriteriosAvaliacao(List<ProgramaCriterioAvaliacao> criteriosAvaliacao) {
        this.criteriosAvaliacao = criteriosAvaliacao;
    }

    public String getCodigoIdentificadorProgramaPublicadoTemp() {
        return codigoIdentificadorProgramaPublicadoTemp;
    }

    public void setCodigoIdentificadorProgramaPublicadoTemp(String codigoIdentificadorProgramaPublicadoTemp) {
        this.codigoIdentificadorProgramaPublicadoTemp = codigoIdentificadorProgramaPublicadoTemp;
    }

    public List<ListaElegibilidadePublicado> getListaElegibilidadePublicado() {
        return listaElegibilidadePublicado;
    }

    public void setListaElegibilidadePublicado(List<ListaElegibilidadePublicado> listaElegibilidadePublicado) {
        this.listaElegibilidadePublicado = listaElegibilidadePublicado;
    }

    public List<ListaAvaliacaoPublicado> getListaAvaliacaoPublicado() {
        return listaAvaliacaoPublicado;
    }

    public void setListaAvaliacaoPublicado(List<ListaAvaliacaoPublicado> listaAvaliacaoPublicado) {
        this.listaAvaliacaoPublicado = listaAvaliacaoPublicado;
    }
}