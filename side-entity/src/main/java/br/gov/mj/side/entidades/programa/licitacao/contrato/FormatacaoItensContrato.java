package br.gov.mj.side.entidades.programa.licitacao.contrato;

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
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao;

@Entity
@Table(name = "tb_fic_formatacao_itens_contrato", schema = "side")
public class FormatacaoItensContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "fic_id_formatacao_itens_contrato")
    @SequenceGenerator(name = "seq_tb_fic_formatacao_itens_contrato_generator", sequenceName = "side.seq_tb_fic_formatacao_itens_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_fic_formatacao_itens_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fic_fk_foc_id_formatacao_contrato")
    @NotNull
    private FormatacaoContrato formatacao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao", name = "enumClassName"))
    @Column(name = "fic_tp_tipo_forma_verificacao_formatacao")
    @NotNull
    private EnumFormaVerificacaoFormatacao formaVerificacao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao", name = "enumClassName"))
    @Column(name = "fic_tp_tipo_campo_formatacao")
    @NotNull
    private EnumTipoCampoFormatacao tipoCampo;

    @Column(name = "fic_ds_titulo_quesito")
    @NotNull
    private String tituloQuesito;

    @Column(name = "fic_ds_orientacao_fornecedores")
    @NotNull
    private String orientacaoFornecedores;

    @Column(name = "fic_bo_possui_identificador_unico")
    @NotNull
    private Boolean possuiIdentificadorUnico;

    @Column(name = "fic_bo_possui_informacao_opcional")
    @NotNull
    private Boolean possuiInformacaoOpcional;

    @Column(name = "fic_bo_possui_dispositivo_movel")
    @NotNull
    private Boolean possuiDispositivoMovel;

    @Column(name = "fic_bo_possui_gps")
    @NotNull
    private Boolean possuiGPS;

    @Column(name = "fic_bo_possui_data")
    @NotNull
    private Boolean possuiData;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem", name = "enumClassName"))
    @Column(name = "fic_tp_responsavel_formatacao")
    @NotNull
    private EnumResponsavelPreencherFormatacaoItem responsavelFormatacao;

    @Override
    public Long getId() {
        return id;
    }

    public EnumFormaVerificacaoFormatacao getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(EnumFormaVerificacaoFormatacao formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public EnumTipoCampoFormatacao getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(EnumTipoCampoFormatacao tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public String getTituloQuesito() {
        return tituloQuesito;
    }

    public void setTituloQuesito(String tituloQuesito) {
        this.tituloQuesito = tituloQuesito;
    }

    public String getOrientacaoFornecedores() {
        return orientacaoFornecedores;
    }

    public void setOrientacaoFornecedores(String orientacaoFornecedores) {
        this.orientacaoFornecedores = orientacaoFornecedores;
    }

    public Boolean getPossuiIdentificadorUnico() {
        return possuiIdentificadorUnico;
    }

    public void setPossuiIdentificadorUnico(Boolean possuiIdentificadorUnico) {
        this.possuiIdentificadorUnico = possuiIdentificadorUnico;
    }

    public Boolean getPossuiInformacaoOpcional() {
        return possuiInformacaoOpcional;
    }

    public void setPossuiInformacaoOpcional(Boolean possuiInformacaoOpcional) {
        this.possuiInformacaoOpcional = possuiInformacaoOpcional;
    }

    public Boolean getPossuiDispositivoMovel() {
        return possuiDispositivoMovel;
    }

    public void setPossuiDispositivoMovel(Boolean possuiDispositivoMovel) {
        this.possuiDispositivoMovel = possuiDispositivoMovel;
    }

    public Boolean getPossuiGPS() {
        return possuiGPS;
    }

    public void setPossuiGPS(Boolean possuiGPS) {
        this.possuiGPS = possuiGPS;
    }

    public Boolean getPossuiData() {
        return possuiData;
    }

    public void setPossuiData(Boolean possuiData) {
        this.possuiData = possuiData;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormatacaoContrato getFormatacao() {
        return formatacao;
    }

    public void setFormatacao(FormatacaoContrato formatacao) {
        this.formatacao = formatacao;
    }

    public EnumResponsavelPreencherFormatacaoItem getResponsavelFormatacao() {
        return responsavelFormatacao;
    }

    public void setResponsavelFormatacao(EnumResponsavelPreencherFormatacaoItem responsavelFormatacao) {
        this.responsavelFormatacao = responsavelFormatacao;
    }

}
