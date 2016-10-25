package br.gov.mj.side.web.dto.formatacaoObjetoFornecimento;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;

public class FormatacaoObjetoFornecimentoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private FormatacaoItensContratoDtot formatacao;
    private FormatacaoItensContratoRespostaDto formatacaoResposta;
    private Long idObjetoFornecimentoContrato;
    private EnumPerfilEntidade responsavelFormatacao;
    private boolean isValorOpcional;

    public FormatacaoObjetoFornecimentoDto(){
        
    }
    
    public FormatacaoObjetoFornecimentoDto(Long id, Long idFormatacaoItensContratoResposta, Long idFormatacaoItensContrato, String respostaAlfanumerica, String nomeAnexo,
            Boolean respostaBooleana, LocalDate respostaData, LocalDateTime dataFoto, String latitudeLongitudeFoto,
            String respostaTexto){
        this.id = id;
        formatacaoResposta = new FormatacaoItensContratoRespostaDto();
        formatacaoResposta.setId(idFormatacaoItensContratoResposta);
        
        formatacao = new FormatacaoItensContratoDtot();
        formatacao.setId(idFormatacaoItensContrato);
        formatacaoResposta.setFormatacao(formatacao);
        
        if(respostaAlfanumerica != null){
            formatacaoResposta.setRespostaAlfanumerico(respostaAlfanumerica);
        }
        
        if(nomeAnexo != null){
            formatacaoResposta.setNomeAnexo(nomeAnexo);
        }
        
        if(respostaBooleana != null){
            formatacaoResposta.setRespostaBooleana(respostaBooleana);
        }
        
        if(respostaData != null){
            formatacaoResposta.setRespostaData(respostaData);
        }
        
        if(dataFoto != null){
            formatacaoResposta.setDataFoto(dataFoto);
        }
        
        if(respostaAlfanumerica != null){
            formatacaoResposta.setLatitudeLongitudeFoto(latitudeLongitudeFoto);
        }
        
        if(respostaTexto != null){
            formatacaoResposta.setRespostaTexto(respostaTexto);
        }
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormatacaoItensContratoDtot getFormatacao() {
        return formatacao;
    }

    public void setFormatacao(FormatacaoItensContratoDtot formatacao) {
        this.formatacao = formatacao;
    }

    public FormatacaoItensContratoRespostaDto getFormatacaoResposta() {
        return formatacaoResposta;
    }

    public void setFormatacaoResposta(FormatacaoItensContratoRespostaDto formatacaoResposta) {
        this.formatacaoResposta = formatacaoResposta;
    }

    public EnumPerfilEntidade getResponsavelFormatacao() {
        return responsavelFormatacao;
    }

    public void setResponsavelFormatacao(EnumPerfilEntidade responsavelFormatacao) {
        this.responsavelFormatacao = responsavelFormatacao;
    }

    public boolean isValorOpcional() {
        return isValorOpcional;
    }

    public void setValorOpcional(boolean isValorOpcional) {
        this.isValorOpcional = isValorOpcional;
    }

    public Long getIdObjetoFornecimentoContrato() {
        return idObjetoFornecimentoContrato;
    }

    public void setIdObjetoFornecimentoContrato(Long idObjetoFornecimentoContrato) {
        this.idObjetoFornecimentoContrato = idObjetoFornecimentoContrato;
    }

}
