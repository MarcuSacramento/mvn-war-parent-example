package br.gov.mj.side.web.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dao.TermoDoacaoDAO;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.DoacoesConcluidasDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TermoDoacaoService {
    
    @Inject
    private TermoDoacaoDAO termoDoacaoDao;
    
    @Inject
    private TermoRecebimentoDefinitivoService termoRecebimentoDefinitivoService;
    
    @Inject
    private ProgramaService programaService;
    
    public List<TermoDoacao> buscarPaginado(TermoDoacaoDto termoDoacaoDto, int first, int size, EnumOrder order, String propertyOrder) {
        
        if (termoDoacaoDto == null || termoDoacaoDto.getPrograma() == null || termoDoacaoDto.getPrograma().getId() == null) {
            throw new IllegalArgumentException("O id do programa não pode ser nulo.");
        }
        return termoDoacaoDao.buscarPaginado(termoDoacaoDto, first, size, order, propertyOrder);
    }

    public List<TermoDoacao> buscarSemPaginacao(TermoDoacaoDto termoDoacaoDto) {
        if (termoDoacaoDto == null || termoDoacaoDto.getPrograma() == null || termoDoacaoDto.getPrograma().getId() == null) {
            throw new IllegalArgumentException("O id do programa não pode ser nulo.");
        }
        
        return termoDoacaoDao.buscarSemPaginacao(termoDoacaoDto);
    }

    public List<TermoDoacao> buscarSemPaginacaoOrdenado(TermoDoacaoDto termoDoacaoDto, EnumOrder order, String propertyOrder) {
        if (termoDoacaoDto == null || termoDoacaoDto.getPrograma() == null || termoDoacaoDto.getPrograma().getId() == null) {
            throw new IllegalArgumentException("O id do programa não pode ser nulo.");
        }
        return termoDoacaoDao.buscarSemPaginacaoOrdenado(termoDoacaoDto, order, propertyOrder);
    }
    
    //Irá atualizar o status do termo de doação para 'DOACAO_CONCLUIDA' e irá retornar true caso tudo ocorra bem
    public boolean concluirDoacao(TermoDoacao termoDoacao, Programa programa, String usuarioLogado){
        if (termoDoacao == null || termoDoacao.getId() == null) {
            throw new IllegalArgumentException("O parametro termo de doação não pode ser nulo.");
        }        
        
      //Se o status do programa já for "ACOMPANHAMENTO" não será preciso alterar esta informação novamente.
        if(programa.getStatusPrograma() != EnumStatusPrograma.ACOMPANHAMENTO){
            programa.setStatusPrograma(EnumStatusPrograma.ACOMPANHAMENTO);
            programaService.atualizarInformacoesBasicasPrograma(programa, usuarioLogado);
        }
        
        return termoDoacaoDao.concluirDoacao(termoDoacao);
    }
    
    //Irá retornar todos os itens doados divididos por entidade.
    public List<DoacoesConcluidasDto> buscarTodosItensDoados(TermoDoacaoDto termoDoacaoDto) {
        if (termoDoacaoDto == null) {
            throw new IllegalArgumentException("O parametro termoDoacaoDto não pode ser nulo.");
        }
        return termoDoacaoDao.buscarTodosItensDoados(termoDoacaoDto);
    }
    
    /*
     * Este metodo inclui termos de doação deforma individual, passar como parametro para o metodo os seguintes atributos:
     * 1 - programa
     * 2 - usuário logado
     * 3 - lista de objetos fornecimento contrato
     * 4 - número do processo sei
     */
    public TermoDoacao incluir(TermoDoacaoDto termoDoacaoDto) {

        validarCampos(termoDoacaoDto);
        
        TermoDoacao termoRetornar = termoDoacaoDao.incluir(termoDoacaoDto);
        
        TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto = new TermoRecebimentoDefinitivoDto();
        termoRecebimentoDefinitivoDto.setPrograma(termoDoacaoDto.getPrograma());
        termoRecebimentoDefinitivoDto.setEntidade(termoRetornar.getEntidade());
        termoRecebimentoDefinitivoDto.setNumeroProcessoSei(termoDoacaoDto.getTermoDoacao().getNumeroProcessoSEI());
        termoRecebimentoDefinitivoService.atualizarNumeroProcessoSeiPorTermoDeRecebimento(termoRecebimentoDefinitivoDto);
        
        return termoRetornar;
    }
    
    public void gerarTermoDoacao(List<TermoDoacaoDto> listaTermoDoacaoDto, List<TermoRecebimentoDefinitivo> listaTRDSelecionados){
        if(listaTRDSelecionados == null || listaTRDSelecionados.isEmpty()){
            throw new IllegalArgumentException("A lista de termo de recebimento não pode ser vazia.");
        }
        
        if(listaTermoDoacaoDto == null || listaTermoDoacaoDto.isEmpty()){
            throw new IllegalArgumentException("A lista de termos de doação não pode ser vazia.");
        }
        
        for(TermoDoacaoDto termo:listaTermoDoacaoDto ){
            atualizarNumeroProcessoSeiPorTermoDoacao(termo);
            incluir(termo);
        }
        
        for(TermoRecebimentoDefinitivo termoRecebimento:listaTRDSelecionados){
            termoRecebimento.setStatusTermoRecebimento(EnumStatusGeracaoTermoDoacao.TERMO_DOACAO_GERADO);
            termoRecebimentoDefinitivoService.alterar(termoRecebimento);
        }
    }

    public List<TermoDoacao> buscarTermosDoacaoPorPrograma(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("O id da nota fiscal não pode ser nula.");
        }
        return termoDoacaoDao.buscarTermosDoacaoPorPrograma(programa);
    }
    
    public void validarCampos(TermoDoacaoDto termoDoacaoDto){
        if (termoDoacaoDto.getPrograma() == null || termoDoacaoDto.getPrograma().getId() == null) {
            throw new IllegalArgumentException("Informe um programa para a inclusão do termo de doação.");
        }
        
        if (StringUtils.isBlank(termoDoacaoDto.getUsuarioLogado())) {
            throw new IllegalArgumentException("Informe o usuario logado.");
        }
        
        if (termoDoacaoDto.getTermoDoacao().getTermoDoacao() == null) {
            throw new IllegalArgumentException("Anexe ao termo de doação o pdf gerado.");
        }
        
        if (termoDoacaoDto.getTermoDoacao().getObjetosFornecimentoContrato() == null || termoDoacaoDto.getTermoDoacao().getObjetosFornecimentoContrato().isEmpty()) {
            throw new IllegalArgumentException("Informe a lista de objetos fornecimento contrato.");
        }
        
        if (termoDoacaoDto.getTermoDoacao().getNumeroProcessoSEI() == null || "".equalsIgnoreCase(termoDoacaoDto.getTermoDoacao().getNumeroProcessoSEI())) {
            throw new IllegalArgumentException("O número do processo Sei não pode ser vazio");
        }
    }
    
    public List<TermoDoacao> montarTermoDeDoacaoComListaObjetoFornecimento(List<TermoRecebimentoDefinitivo> listaTermosRecebimento){
        Map<Long,TermoDoacao> map = new HashMap<Long,TermoDoacao>();
        
        //Irá pegar a lista com todos os termos definitivos e separa-los por beneficiario
        //Todos os termos de recebimento do mesmo beneficiário irá ser salvos como o mesmo termo de doação
        for(TermoRecebimentoDefinitivo trd:listaTermosRecebimento){
            for(ObjetoFornecimentoContrato ofc:trd.getObjetosFornecimentoContrato()){
                if(!map.containsKey(ofc.getLocalEntrega().getEntidade().getId())){
                    
                    List<TermoDoacao> td = new ArrayList<TermoDoacao>();
                    TermoDoacao termoNovo = new TermoDoacao();
                    termoNovo.setObjetosFornecimentoContrato(new ArrayList<ObjetoFornecimentoContrato>());
                    termoNovo.getObjetosFornecimentoContrato().add(ofc);
                    termoNovo.setNomeBeneficiario(ofc.getLocalEntrega().getEntidade().getNomeEntidade());
                    termoNovo.setEntidade(ofc.getLocalEntrega().getEntidade());
                    termoNovo.setNumeroCnpj(ofc.getLocalEntrega().getEntidade().getNumeroCnpj());
                    termoNovo.setNumeroProcessoSEI(trd.getNumeroProcessoSEI());
                    td.add(termoNovo);
                    
                    map.put(ofc.getLocalEntrega().getEntidade().getId(), termoNovo);
                }else{
                    TermoDoacao td = map.get(ofc.getLocalEntrega().getEntidade().getId());
                    td.getObjetosFornecimentoContrato().add(ofc);
                    
                    map.put(ofc.getLocalEntrega().getEntidade().getId(), td);
                }
            }
        }
        
        List<TermoDoacao> lista = new ArrayList<TermoDoacao>();
        lista.addAll( (Collection<? extends TermoDoacao>) map.values());
        return lista;
    }
    
    public void atualizarNumeroProcessoSeiPorTermoDoacao(TermoDoacaoDto termoDoacaoDto){
        termoDoacaoDao.atualizarNumeroProcessoSeiPorTermoDoacao(termoDoacaoDto);
    }
}
