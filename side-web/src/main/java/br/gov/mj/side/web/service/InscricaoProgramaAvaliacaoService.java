package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseAvaliacao;
import br.gov.mj.side.web.dao.InscricaoProgramaAvaliacaoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InscricaoProgramaAvaliacaoService {

    @Inject
    private InscricaoProgramaAvaliacaoDAO inscricaoProgramaAvaliacaoDAO;

    public List<HistoricoAnaliseAvaliacao> buscarHistoricoAnaliseAvaliacao(InscricaoPrograma inscricaoPrograma) {

        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return inscricaoProgramaAvaliacaoDAO.buscarHistoricoAnaliseAvaliacao(inscricaoPrograma.getId());
    }

    public InscricaoPrograma salvar(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {
        inscricaoPrograma.setStatusInscricao(EnumStatusInscricao.ANALISE_AVALIACAO);
        return alterar(false, inscricaoPrograma, usuarioLogado);
    }

    public InscricaoPrograma concluir(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {
        inscricaoPrograma.setStatusInscricao(EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO);
        return alterar(true, inscricaoPrograma, usuarioLogado);
    }

    private InscricaoPrograma alterar(boolean possuiHistorico, InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaRetorno = null;

        validarObjetoNulo(inscricaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }
        inscricaoProgramaRetorno = inscricaoProgramaAvaliacaoDAO.alterar(possuiHistorico, inscricaoPrograma, usuarioLogado);
        return inscricaoProgramaRetorno;
    }

    private void validarObjetoNulo(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }
    }

    public HistoricoAnaliseAvaliacao buscarUltimoHistoricoAnaliseAvaliacao(InscricaoPrograma inscricaoPrograma) {
        return inscricaoProgramaAvaliacaoDAO.buscarUltimoHistoricoAnaliseAvaliacao(inscricaoPrograma);

    }

}
