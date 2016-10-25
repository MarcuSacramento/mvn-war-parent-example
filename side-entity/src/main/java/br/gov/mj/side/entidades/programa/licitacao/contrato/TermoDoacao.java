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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusTermoDoacao;

@Entity
@Table(name = "tb_tda_termo_doacao", schema = "side")
public class TermoDoacao extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "tda_id_termo_doacao")
    @SequenceGenerator(name = "seq_tb_tda_termo_doacao_generator", sequenceName = "side.seq_tb_tda_termo_doacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_tda_termo_doacao_generator")
    @NotNull
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "tda_fk_ent_id_entidade")
    private Entidade entidade;

    @Column(name = "tda_no_nome_anexo")
    private String nomeAnexo;
    
    @Column(name = "tda_no_nome_beneficiario")
    private String nomeBeneficiario;
    
    @Column(name = "tda_no_numero_cnpj")
    private String numeroCnpj;
    
    @Column(name = "tda_no_usuario_criacao_termo")
    @NotNull
    private String usuarioCriacao;
    
    @Column(name = "tda_ct_termo_recebimento_definitivo_gerado")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] termoDoacao;
    
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "tda_dt_data_geracao_documento")
    @NotNull
    private LocalDateTime dataGeracao;
    
    @Column(name = "tda_nu_numero_processo_sei")
    @NotNull
    private String numeroProcessoSEI;
    
    @Column(name = "tda_nu_numero_documento_sei")
    private String numeroDocumentoSei;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusTermoDoacao", name = "enumClassName"))
    @Column(name = "tda_st_status_termo_doacao")
    private EnumStatusTermoDoacao statusTermoDoacao;
    
    @OneToMany(mappedBy = "termoDoacao", cascade = { CascadeType.MERGE }, orphanRemoval = true)
    private List<ObjetoFornecimentoContrato> objetosFornecimentoContrato = new ArrayList<ObjetoFornecimentoContrato>();
    
    @Override
    public Long getId() {
        return id;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public String getNomeBeneficiario() {
        return nomeBeneficiario;
    }

    public void setNomeBeneficiario(String nomeBeneficiario) {
        this.nomeBeneficiario = nomeBeneficiario;
    }

    public String getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public void setUsuarioCriacao(String usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }

    public byte[] getTermoDoacao() {
        return termoDoacao;
    }

    public void setTermoDoacao(byte[] termoDoacao) {
        this.termoDoacao = termoDoacao;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public List<ObjetoFornecimentoContrato> getObjetosFornecimentoContrato() {
        return objetosFornecimentoContrato;
    }

    public void setObjetosFornecimentoContrato(List<ObjetoFornecimentoContrato> objetosFornecimentoContrato) {
        this.objetosFornecimentoContrato = objetosFornecimentoContrato;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getNumeroCnpj() {
        return numeroCnpj;
    }

    public void setNumeroCnpj(String numeroCnpj) {
        this.numeroCnpj = numeroCnpj;
    }

    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }

    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }

    public String getNumeroDocumentoSei() {
        return numeroDocumentoSei;
    }

    public void setNumeroDocumentoSei(String numeroDocumentoSei) {
        this.numeroDocumentoSei = numeroDocumentoSei;
    }

    public EnumStatusTermoDoacao getStatusTermoDoacao() {
        return statusTermoDoacao;
    }

    public void setStatusTermoDoacao(EnumStatusTermoDoacao statusTermoDoacao) {
        this.statusTermoDoacao = statusTermoDoacao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }
    
    public static Comparator<TermoDoacao> getComparator(int order, String coluna) {
        return new Comparator<TermoDoacao>() {
            @Override
            public int compare(TermoDoacao o1, TermoDoacao o2) {
                int valor = 0;

                if ("nomeBeneficiario".equalsIgnoreCase(coluna)) {
                    valor = o1.getNomeBeneficiario().toUpperCase().compareTo(o2.getNomeBeneficiario().toUpperCase()) * order;
                } else if ("numeroCnpj".equalsIgnoreCase(coluna)) {
                    valor = o1.getNumeroCnpj().toUpperCase().compareTo(o2.getNumeroCnpj().toUpperCase()) * order;
                } else if ("numeroProcessoSEI".equalsIgnoreCase(coluna)) {
                    valor = o1.getNumeroProcessoSEI().compareTo(o2.getNumeroProcessoSEI()) * order;
                }
                else if ("numeroDocumentoSei".equalsIgnoreCase(coluna)) {
                    valor = o1.getNumeroDocumentoSei().compareTo(o2.getNumeroDocumentoSei()) * order;
                }
                return valor;
            }
        };
    }
}
