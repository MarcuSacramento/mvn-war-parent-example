package br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa;

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
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;

@Entity
@Table(name = "tb_anr_anexo_nota_remessa", schema = "side")
public class AnexoNotaRemessa extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "anr_id_item_nota_remessa_of_contrato")
    @SequenceGenerator(name = "seq_tb_anr_anexo_nota_remessa_generator", sequenceName = "side.seq_tb_anr_anexo_nota_remessa", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_anr_anexo_nota_remessa_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "anr_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato")
    @NotNull
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimento;

    @Column(name = "anr_no_nome_anexo")
    @NotNull
    private String nomeAnexo;

    @Column(name = "anr_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] conteudo;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega", name = "enumClassName"))
    @Column(name = "anr_tp_tipo_arquivo_termo_entrega")
    private EnumTipoArquivoTermoEntrega tipoArquivoTermoEntrega;

    @Override
    public Long getId() {
        return id;
    }

    public NotaRemessaOrdemFornecimentoContrato getNotaRemessaOrdemFornecimento() {
        return notaRemessaOrdemFornecimento;
    }

    public void setNotaRemessaOrdemFornecimento(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimento) {
        this.notaRemessaOrdemFornecimento = notaRemessaOrdemFornecimento;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public EnumTipoArquivoTermoEntrega getTipoArquivoTermoEntrega() {
        return tipoArquivoTermoEntrega;
    }

    public void setTipoArquivoTermoEntrega(EnumTipoArquivoTermoEntrega tipoArquivoTermoEntrega) {
        this.tipoArquivoTermoEntrega = tipoArquivoTermoEntrega;
    }

}
