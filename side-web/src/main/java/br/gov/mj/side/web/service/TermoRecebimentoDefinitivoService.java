package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dao.TermoRecebimentoDefinitivoDAO;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TermoRecebimentoDefinitivoService {

    @Inject
    private TermoRecebimentoDefinitivoDAO termoRecebimentoDefinitivoDAO;
    
    @Inject
    private NotaRemessaService notaRemessaService;

    // FORNECEDOR

    public TermoRecebimentoDefinitivo buscarPeloId(Long idTermoRecebimento) {
        
        if (idTermoRecebimento == null) {
            throw new IllegalArgumentException("o id não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.buscarPeloId(idTermoRecebimento);
    }

    public List<TermoRecebimentoDefinitivo> buscarSemPaginacao(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto) {

        if (termoRecebimentoDefinitivoDto == null || termoRecebimentoDefinitivoDto.getPrograma() == null || termoRecebimentoDefinitivoDto.getPrograma().getId() == null) {
            throw new IllegalArgumentException("o programa não pode ser nulo.");
        }
        
        return termoRecebimentoDefinitivoDAO.buscarSemPaginacao(termoRecebimentoDefinitivoDto);
    }

    public List<TermoRecebimentoDefinitivo> buscarPaginado(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto, int first, int size, EnumOrder order, String propertyOrder) {

        if (termoRecebimentoDefinitivoDto == null || termoRecebimentoDefinitivoDto.getPrograma() == null || termoRecebimentoDefinitivoDto.getPrograma().getId() == null) {
            throw new IllegalArgumentException("o programa não pode ser nulo.");
        }
        
        return termoRecebimentoDefinitivoDAO.buscarPaginado(termoRecebimentoDefinitivoDto, first, size, order, propertyOrder);
    }
    
    public List<Object> retornaObjetoFornecimentoContratoPorTermoRecebimento(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){
        if (termoRecebimentoDefinitivoDto == null) {
            throw new IllegalArgumentException("O termoRecebimentoDefinitivoDto não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.retornaObjetoFornecimentoContratoPorTermoRecebimento(termoRecebimentoDefinitivoDto);
    }
    
    //O número do documento SEI é único
    public boolean verificarSeNumeroDocumentoSeiEstaEmUso(String numeroSei){
        if (numeroSei == null || "".equalsIgnoreCase(numeroSei)) {
            throw new IllegalArgumentException("O numero do Documento Sei não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.verificarSeNumeroDocumentoSeiEstaEmUso(numeroSei);
    }

    public TermoRecebimentoDefinitivo incluir(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo, String usuarioLogado, List<ObjetoFornecimentoContrato> listaDeObjetosFornecimentoContrato) {
        
        if (listaDeObjetosFornecimentoContrato == null || listaDeObjetosFornecimentoContrato.isEmpty()) {
            throw new IllegalArgumentException("A lista de Objeto Fornecimento Contrato não pode estar vazia.");
        }
        
        if (termoRecebimentoDefinitivo == null || termoRecebimentoDefinitivo.getTermoRecebimentoDefinitivoGerado() == null) {
            throw new IllegalArgumentException("Não é possível criar um novo termo sem anexar um PDF.");
        }
        
        if (StringUtils.isBlank(usuarioLogado)) {
            throw new IllegalArgumentException("Usuario logado obrigatório");
        }

        TermoRecebimentoDefinitivo termoGerado = termoRecebimentoDefinitivoDAO.incluir(termoRecebimentoDefinitivo, listaDeObjetosFornecimentoContrato, usuarioLogado);
        notaRemessaService.verificarFaseDaOrdemDeFornecimento(termoGerado.getNotaRemessaOrdemFornecimentoContrato().getOrdemFornecimento());
        return termoGerado;
    }
    
    public TermoRecebimentoDefinitivo alterar(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        if (termoRecebimentoDefinitivo == null || termoRecebimentoDefinitivo.getId() == null) {
            throw new IllegalArgumentException("O termoRecebimentoDefinitivo não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.alterar(termoRecebimentoDefinitivo);
    }
    
    public TermoRecebimentoDefinitivo atualizar(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo){
        if (termoRecebimentoDefinitivo == null || termoRecebimentoDefinitivo.getId() == null) {
            throw new IllegalArgumentException("O termoRecebimentoDefinitivo não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.atualizar(termoRecebimentoDefinitivo);
    }
    
    /*
     * passar como parametro
     * 1 - Termo de recebimento definitivo
     * 2 - o pdf gerado para o termo de doação
     * 3 - o usuário logado
     * 4 - o número do processo sei
     */
    public TermoRecebimentoDefinitivo gerarTermoDeDoacao(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){
        if (termoRecebimentoDefinitivoDto.getListaDeObjetosFornecimentoContrato() == null || termoRecebimentoDefinitivoDto.getListaDeObjetosFornecimentoContrato().isEmpty()) {
            throw new IllegalArgumentException("A lista de Objeto Fornecimento Contrato não pode estar vazia.");
        }
        
        if (termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo() == null || termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo().getTermoRecebimentoDefinitivoGerado() == null) {
            throw new IllegalArgumentException("Não é possível criar um novo termo sem anexar um PDF.");
        }
        
        if (StringUtils.isBlank(termoRecebimentoDefinitivoDto.getUsuarioLogado())) {
            throw new IllegalArgumentException("Usuario logado obrigatório");
        }
        
        if (StringUtils.isBlank(termoRecebimentoDefinitivoDto.getNumeroProcessoSei())) {
            throw new IllegalArgumentException("O número do processo sei é obrigatório");
        }
        
        TermoRecebimentoDefinitivo termoGerado = termoRecebimentoDefinitivoDAO.incluir(termoRecebimentoDefinitivoDto.getTermoRecebimentoDefinitivo(), termoRecebimentoDefinitivoDto.getListaDeObjetosFornecimentoContrato(), termoRecebimentoDefinitivoDto.getUsuarioLogado());
        termoRecebimentoDefinitivoDto.setNumeroProcessoSei("11155111");
        termoRecebimentoDefinitivoDAO.atualizarNumeroProcessoSeiPorTermoDeRecebimento(termoRecebimentoDefinitivoDto);
        return termoGerado;
    }
    
    public void atualizarNumeroProcessoSeiPorTermoDeRecebimento(TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto){
        termoRecebimentoDefinitivoDAO.atualizarNumeroProcessoSeiPorTermoDeRecebimento(termoRecebimentoDefinitivoDto);
    }
    
    public List<TermoRecebimentoDefinitivo> buscarTermosRecebimentoDefinitivoPorPrograma(Programa programa){
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("O programa não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.buscarTermosRecebimentoDefinitivoPorPrograma(programa);
    }
    
    public List<TermoRecebimentoDefinitivo> buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(Long idNotaRemessa){
        if (idNotaRemessa == null) {
            throw new IllegalArgumentException("O id da nota fiscal não pode ser nula.");
        }
        return termoRecebimentoDefinitivoDAO.buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(idNotaRemessa);
    }
    
    public List<ObjetoFornecimentoContrato> buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(Long idTermoRecebimentoDefinitivo){
        if (idTermoRecebimentoDefinitivo == null) {
            throw new IllegalArgumentException("O id do termo de recebimento não pode ser nulo.");
        }
        return termoRecebimentoDefinitivoDAO.buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(idTermoRecebimentoDefinitivo);
    }
    
    public void atualizarStatusGeracaoTermoDefinitivoPeloId(EnumStatusGeracaoTermoDoacao status, Long idTermoRecebimento){
        if (idTermoRecebimento == null) {
            throw new IllegalArgumentException("O id do termo de recebimento não pode ser nulo.");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("O status não pode ser nulo.");
        }
        
        termoRecebimentoDefinitivoDAO.atualizarStatusGeracaoTermoDefinitivoPeloId(status,idTermoRecebimento);
    }
}
