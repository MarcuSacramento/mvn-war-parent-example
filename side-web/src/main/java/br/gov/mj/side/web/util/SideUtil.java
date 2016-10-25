package br.gov.mj.side.web.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;

import br.gov.mj.side.entidades.AnexoBem;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.programa.ProgramaAnexo;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoAnexo;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.InscricaoAnexoAnalise;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ContratoAnexo;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.CaminhoCompletoRelatoriosDto;
import br.gov.mj.side.web.dto.FormatacaoObjetoFornecimentoAmbosDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoObjetoFornecimentoDto;

public class SideUtil {

    public static void download(byte[] conteudo, String nomeAnexo) {
        AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {
            private static final long serialVersionUID = 1L;

            @Override
            public void write(OutputStream output) throws IOException {
                output.write(conteudo);
            }
        };
        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, nomeAnexo);
        RequestCycle request = RequestCycle.get();
        request.scheduleRequestHandlerAfterCurrent(handler);
    }

    public static List<AnexoBem> convertAnexoDtoToEntityAnexoBem(List<AnexoDto> lista) {
        List<AnexoBem> listaRetorno = new ArrayList<AnexoBem>();
        for (AnexoDto anexoDto : lista) {
            AnexoBem obj = new AnexoBem();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<ProgramaAnexo> convertAnexoDtoToEntityProgramaAnexo(List<AnexoDto> lista) {
        List<ProgramaAnexo> listaRetorno = new ArrayList<ProgramaAnexo>();
        for (AnexoDto anexoDto : lista) {
            ProgramaAnexo obj = new ProgramaAnexo();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setDataDocumento(anexoDto.getDataDocumento());
            obj.setTipoArquivo(anexoDto.getTipoArquivo());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<EntidadeAnexo> convertAnexoDtoToEntityEntidadeAnexo(List<AnexoDto> lista) {
        List<EntidadeAnexo> listaRetorno = new ArrayList<EntidadeAnexo>();
        for (AnexoDto anexoDto : lista) {
            EntidadeAnexo obj = new EntidadeAnexo();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setDataDocumento(anexoDto.getDataDocumento());
            obj.setTipoArquivo(anexoDto.getTipoArquivoEntidade());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<InscricaoAnexoAnalise> convertAnexoDtoToEntityInscricaoAnexoAnalise(List<AnexoDto> lista) {
        List<InscricaoAnexoAnalise> listaRetorno = new ArrayList<InscricaoAnexoAnalise>();
        for (AnexoDto anexoDto : lista) {
            InscricaoAnexoAnalise obj = new InscricaoAnexoAnalise();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setDataDocumento(anexoDto.getDataDocumento());
            obj.setTipoArquivo(anexoDto.getTipoArquivo());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<InscricaoAnexoAvaliacao> convertAnexoDtoToEntityInscricaoAnexoAvaliacao(List<AnexoDto> lista) {
        List<InscricaoAnexoAvaliacao> listaRetorno = new ArrayList<InscricaoAnexoAvaliacao>();
        for (AnexoDto anexoDto : lista) {
            InscricaoAnexoAvaliacao obj = new InscricaoAnexoAvaliacao();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<InscricaoAnexoElegibilidade> convertAnexoDtoToEntityInscricaoAnexoElegibilidade(List<AnexoDto> lista) {
        List<InscricaoAnexoElegibilidade> listaRetorno = new ArrayList<InscricaoAnexoElegibilidade>();
        for (AnexoDto anexoDto : lista) {
            InscricaoAnexoElegibilidade obj = new InscricaoAnexoElegibilidade();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<ComissaoAnexo> convertAnexoDtoToEntityComissaoAnexo(List<AnexoDto> lista) {
        List<ComissaoAnexo> listaRetorno = new ArrayList<ComissaoAnexo>();
        for (AnexoDto anexoDto : lista) {
            ComissaoAnexo obj = new ComissaoAnexo();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setTamanho(anexoDto.getTamanho());
            obj.setDescricaoAnexo(anexoDto.getDescricaoAnexo());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<ContratoAnexo> convertAnexoDtoToEntityContratoAnexo(List<AnexoDto> lista) {
        List<ContratoAnexo> listaRetorno = new ArrayList<ContratoAnexo>();
        for (AnexoDto anexoDto : lista) {
            ContratoAnexo obj = new ContratoAnexo();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setDataDocumento(anexoDto.getDataDocumento());
            obj.setTipoArquivoContrato(anexoDto.getTipoArquivoContrato());
            obj.setTamanho(anexoDto.getTamanho());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    // Converterá a lista de AnexoDto para uma lista de AnexoNotaRemessa.
    public static List<AnexoNotaRemessa> convertAnexoDtoToEntityAnexoNotaRemessa(List<AnexoDto> lista) {
        List<AnexoNotaRemessa> listaRetorno = new ArrayList<AnexoNotaRemessa>();
        for (AnexoDto anexoDto : lista) {
            AnexoNotaRemessa obj = new AnexoNotaRemessa();
            obj.setId(anexoDto.getId());
            obj.setNomeAnexo(anexoDto.getNomeAnexo());
            obj.setTipoArquivoTermoEntrega(anexoDto.getTipoArquivoTermoEntrega());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<ListaAvaliacaoPublicado> convertAnexoDtoToEntityListaAvaliacaoPublicado(List<AnexoDto> lista) {
        List<ListaAvaliacaoPublicado> listaRetorno = new ArrayList<ListaAvaliacaoPublicado>();
        for (AnexoDto anexoDto : lista) {
            ListaAvaliacaoPublicado obj = new ListaAvaliacaoPublicado();
            obj.setId(anexoDto.getId());
            obj.setNomeArquivo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setTipoLista(anexoDto.getTipoLista());
            obj.setTamanho(anexoDto.getTamanho());
            obj.setUsuarioCadastro(anexoDto.getUsuarioCadastro());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    public static List<ListaElegibilidadePublicado> convertAnexoDtoToEntityListaElegibilidadePublicado(List<AnexoDto> lista) {
        List<ListaElegibilidadePublicado> listaRetorno = new ArrayList<ListaElegibilidadePublicado>();
        for (AnexoDto anexoDto : lista) {
            ListaElegibilidadePublicado obj = new ListaElegibilidadePublicado();
            obj.setId(anexoDto.getId());
            obj.setNomeArquivo(anexoDto.getNomeAnexo());
            obj.setConteudo(anexoDto.getConteudo());
            obj.setDataCadastro(anexoDto.getDataCadastro());
            obj.setTipoLista(anexoDto.getTipoLista());
            obj.setTamanho(anexoDto.getTamanho());
            obj.setUsuarioCadastro(anexoDto.getUsuarioCadastro());
            listaRetorno.add(obj);
        }
        return listaRetorno;
    }

    /*
     * Se a variável responsávelPreencher vier nula então irá organizar toda a lista independente do perfil
     */
    public static List<FormatacaoObjetoFornecimentoAmbosDto> convertListaFofPorFormatacaoItem(List<FormatacaoObjetoFornecimento> listaFof, EnumResponsavelPreencherFormatacaoItem responsavelPreencher){
        
        FormatacaoItensContrato ficTemp = null;
        FormatacaoObjetoFornecimento fofTemp = null;
        
        List<FormatacaoObjetoFornecimentoAmbosDto> listaDividida = new ArrayList<FormatacaoObjetoFornecimentoAmbosDto>();
        
        for (FormatacaoObjetoFornecimento fof : listaFof) {
            
            FormatacaoObjetoFornecimentoAmbosDto dto = new FormatacaoObjetoFornecimentoAmbosDto();
            
            if(responsavelPreencher == null || responsavelPreencher == EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO){
                if(fof.getFormatacao().getResponsavelFormatacao() == EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO){
                    dto.setFormatacaoBeneficiario(fof);
                    dto.setResponsavelFormacatao(EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO);
                    listaDividida.add(dto);
                    continue;
                }
            }
                        
            if(responsavelPreencher == null || responsavelPreencher == EnumResponsavelPreencherFormatacaoItem.FORNECEDOR){
                if(fof.getFormatacao().getResponsavelFormatacao() == EnumResponsavelPreencherFormatacaoItem.FORNECEDOR){
                    dto.setFormatacaoFornecedor(fof);
                    dto.setResponsavelFormacatao(EnumResponsavelPreencherFormatacaoItem.FORNECEDOR);
                    listaDividida.add(dto);
                    continue;
                }
            }
            
            if(responsavelPreencher == null || responsavelPreencher == EnumResponsavelPreencherFormatacaoItem.AMBOS){
                if(fof.getFormatacao().getResponsavelFormatacao() == EnumResponsavelPreencherFormatacaoItem.AMBOS){
                    
                    if(ficTemp != null && ficTemp.getId().equals(fof.getFormatacao().getId())){
                        if(ficTemp.getResponsavelFormatacao() == EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO){
                            dto.setFormatacaoBeneficiario(fof);
                            dto.setFormatacaoFornecedor(fofTemp);
                        }else{
                            dto.setFormatacaoBeneficiario(fofTemp);
                            dto.setFormatacaoFornecedor(fof);
                        }
                        dto.setResponsavelFormacatao(EnumResponsavelPreencherFormatacaoItem.AMBOS);
                        listaDividida.add(dto);                
                    }else{
                        ficTemp = fof.getFormatacao();
                        fofTemp = fof;
                    }
                }
            }
        }        
        return listaDividida;
    }
    
    public static List<FormatacaoObjetoFornecimento> convertDtoToEntityFormatacaoObjetoFornecimentoDto(List<FormatacaoObjetoFornecimentoDto> lista) {
        List<FormatacaoObjetoFornecimento> listaRetorno = new ArrayList<FormatacaoObjetoFornecimento>();
        for (FormatacaoObjetoFornecimentoDto dto : lista) {
            FormatacaoObjetoFornecimento objeto = new FormatacaoObjetoFornecimento();

            FormatacaoItensContrato formatacao = null;
            if (dto.getFormatacao() != null) {
                formatacao = new FormatacaoItensContrato();
                formatacao.setId(dto.getFormatacao().getId());
                formatacao.setFormaVerificacao(dto.getFormatacao().getFormaVerificacao());
                formatacao.setTipoCampo(dto.getFormatacao().getTipoCampo());
                formatacao.setTituloQuesito(dto.getFormatacao().getTituloQuesito());
                formatacao.setOrientacaoFornecedores(dto.getFormatacao().getOrientacaoFornecedores());
                formatacao.setPossuiIdentificadorUnico(dto.getFormatacao().getPossuiIdentificadorUnico());
                formatacao.setPossuiInformacaoOpcional(dto.getFormatacao().getPossuiInformacaoOpcional());
                formatacao.setPossuiDispositivoMovel(dto.getFormatacao().getPossuiDispositivoMovel());
                formatacao.setPossuiGPS(dto.getFormatacao().getPossuiGPS());
                formatacao.setPossuiData(dto.getFormatacao().getPossuiData());
                formatacao.setResponsavelFormatacao(dto.getFormatacao().getResponsavelFormatacao());
            }

            FormatacaoItensContratoResposta formatacaoResposta = null;
            if (dto.getFormatacaoResposta() != null) {
                formatacaoResposta = new FormatacaoItensContratoResposta();

                formatacaoResposta.setId(dto.getFormatacaoResposta().getId());
                formatacaoResposta.setFormatacao(formatacao);
                formatacaoResposta.setRespostaAlfanumerico(dto.getFormatacaoResposta().getRespostaAlfanumerico());
                formatacaoResposta.setConteudo(dto.getFormatacaoResposta().getConteudo());
                formatacaoResposta.setNomeAnexo(dto.getFormatacaoResposta().getNomeAnexo());
                formatacaoResposta.setRespostaBooleana(dto.getFormatacaoResposta().getRespostaBooleana());
                formatacaoResposta.setRespostaData(dto.getFormatacaoResposta().getRespostaData());
                formatacaoResposta.setDataFoto(dto.getFormatacaoResposta().getDataFoto());
                formatacaoResposta.setLatitudeLongitudeFoto(dto.getFormatacaoResposta().getLatitudeLongitudeFoto());
                formatacaoResposta.setRespostaTexto(dto.getFormatacaoResposta().getRespostaTexto());
                formatacaoResposta.setTamanho(dto.getFormatacaoResposta().getTamanho());
                formatacaoResposta.setStatusFormatacao(dto.getFormatacaoResposta().getStatusFormatacaoResposta());
                formatacaoResposta.setMotivoNaoConformidade(dto.getFormatacaoResposta().getMotivoNaoConformidade());
                formatacaoResposta.setArquivoUnico(dto.getFormatacaoResposta().getArquivoUnico());
            }

            objeto.setId(dto.getId());
            objeto.setFormatacao(formatacao);
            objeto.setFormatacaoResposta(formatacaoResposta);
            objeto.setResponsavelFormatacao(dto.getResponsavelFormatacao());

            listaRetorno.add(objeto);
        }
        return listaRetorno;
    }

    private static String geraAleatorio(int tamanho, char tipo) {
        Random rand = new Random();
        char[] letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] numeros = "0123456789".toCharArray();

        char[] utilizado = null;

        if (tipo == 'N') {// numeros
            utilizado = numeros;
        } else if (tipo == 'L') {// letras
            utilizado = letras;

        } else if (tipo == 'Q') {// qualquer tipo
            utilizado = new char[letras.length + numeros.length];
            System.arraycopy(letras, 0, utilizado, 0, letras.length);
            System.arraycopy(numeros, 0, utilizado, letras.length, numeros.length);
        }

        String s = "";
        for (int i = 0; i < tamanho; i++) {
            int ch = rand.nextInt(utilizado.length);
            s += utilizado[ch];
        }
        return s;
    }

    public static String geraCodigoAleatorioDeVerificacao() {
        return geraAleatorio(4, 'L').concat("-").concat(geraAleatorio(4, 'N'));
    }

    public static CaminhoCompletoRelatoriosDto getCaminhoCompletoRelatorios(ServletContext servletContext) {
        String file_separator = System.getProperty("file.separator");
        String caminhoCompleto = getCaminhoFisico(servletContext) + file_separator + "WEB-INF" + file_separator + "classes" + file_separator + "reports" + file_separator;
        return new CaminhoCompletoRelatoriosDto(caminhoCompleto);
    }

    public static String getCaminhoFisico(ServletContext servletContext) {
        String caminhoFisico = servletContext.getRealPath("/");
        return caminhoFisico;
    }
    
    public static String adicionarZerosAString(String valor, int quantidadeZeros){
        StringBuilder stringMontada = new StringBuilder();
        
        int quantidadeLetras = valor.length();
        int quantidadeZerosAdicionar = quantidadeZeros - quantidadeLetras;
        
        for(int i=0; i<quantidadeZerosAdicionar;i++){
            stringMontada.append("0");
        }
        stringMontada.append(valor);        
        return stringMontada.toString();
    }
    
    public static String retornarHashDoArquivo(byte[] arrayByte){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            s.append(arrayByte[i]);
        }
        return s.toString();
    }
    
    public static String retornarHashDoArquivoMD5(byte[] arrayByte){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arrayByte.length; i++) {
            s.append(arrayByte[i]);
        }
        return s.toString();
    }
    
    
    public static Integer devolveNumeroAleatório(Integer faixa){
        Integer identificadorDesteItem = (int) (Math.random() * faixa);
        return identificadorDesteItem;
    }

}
