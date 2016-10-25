package br.gov.mj.side.web.service;

import java.util.ArrayList;
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
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;
import br.gov.mj.side.web.dao.ContratoDAO;
import br.gov.mj.side.web.dao.FormatacaoItensContratoDAO;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoRespostaDto;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoObjetoFornecimentoDto;
import br.gov.mj.side.web.util.MapUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class FormatacaoItensContratoService {

    @Inject
    private FormatacaoItensContratoDAO formatacaoItensContratoDAO;

    @Inject
    private ContratoDAO contratoDAO;

    @Inject
    private IGenericPersister genericPersister;

    public List<Bem> buscarListaBensRemanescentes(Contrato contrato) {

        if (contrato == null || contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        Map<Long, Bem> mapaBensDoContrato = new HashMap<Long, Bem>();
        Map<Long, Bem> mapaBensDaFormatacao = new HashMap<Long, Bem>();

        List<AgrupamentoLicitacao> listaAgrupamento = contratoDAO.buscarAgrupamentoLicitacao(contrato.getId());

        List<FormatacaoContrato> listaFormatacao = buscarFormatacaoContrato(contrato);

        for (AgrupamentoLicitacao agrupamentoLicitacao : listaAgrupamento) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {
                    Long chave = bemUf.getBem().getId();
                    mapaBensDoContrato.put(chave, bemUf.getBem());
                }
            }
        }

        for (FormatacaoContrato formatacaoContrato : listaFormatacao) {
            for (ItensFormatacao itensFormatacao : formatacaoContrato.getItens()) {
                Long chave = itensFormatacao.getItem().getId();
                mapaBensDaFormatacao.put(chave, itensFormatacao.getItem());
            }
        }

        Map<Long, Bem> mapRetorno = (Map<Long, Bem>) MapUtil.getNaoUsados(mapaBensDoContrato, mapaBensDaFormatacao);
        return transformaMapEmListaBem(mapRetorno);
    }

    private List<Bem> transformaMapEmListaBem(Map<Long, Bem> mapa) {
        List<Bem> lista = new ArrayList<Bem>();
        lista.addAll(mapa.values());
        return lista;
    }

    public List<FormatacaoContrato> buscarFormatacaoContrato(Contrato contrato) {
        if (contrato == null || contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return formatacaoItensContratoDAO.buscarFormatacaoContrato(contrato.getId());
    }

    public List<FormatacaoItensContrato> buscarFormatacaoItensContrato(FormatacaoContrato formatacaoContrato) {
        if (formatacaoContrato == null || formatacaoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return formatacaoItensContratoDAO.buscarFormatacaoItensContrato(formatacaoContrato.getId());
    }

    public List<ItensFormatacao> buscarItens(FormatacaoContrato formatacaoContrato) {
        if (formatacaoContrato == null || formatacaoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return formatacaoItensContratoDAO.buscarItens(formatacaoContrato.getId());
    }

    public List<FormatacaoItensContrato> buscarPaginado(Contrato contrato, int first, int size, EnumOrder order, String propertyOrder) {

        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return formatacaoItensContratoDAO.buscarPaginado(contrato, first, size, order, propertyOrder);
    }

    public List<FormatacaoItensContrato> buscarSemPaginacao(Contrato contrato) {

        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return formatacaoItensContratoDAO.buscarSemPaginacao(contrato);
    }
    
    public List<FormatacaoObjetoFornecimentoDto> buscarFormatacaoItensResposta(FormatacaoItensContratoRespostaDto dto){
        
        return formatacaoItensContratoDAO.buscarFormatacaoItensResposta(dto);
    }

    public List<FormatacaoItensContrato> buscarSemPaginacaoOrdenado(Contrato contrato, EnumOrder order, String propertyOrder) {

        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return formatacaoItensContratoDAO.buscarSemPaginacaoOrdenado(contrato, order, propertyOrder);
    }

    public FormatacaoContrato incluirAlterar(FormatacaoContrato formatacaoContrato, String usuarioLogado) {

        FormatacaoContrato formatacaoContratoRetorno = null;

        if (formatacaoContrato == null) {
            throw new IllegalArgumentException("Parâmetro formatacaoContrato não pode ser null");
        }

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (formatacaoContrato.getId() == null) {
            formatacaoContratoRetorno = formatacaoItensContratoDAO.incluir(formatacaoContrato, usuarioLogado);
        } else {
            formatacaoContratoRetorno = formatacaoItensContratoDAO.alterar(formatacaoContrato, usuarioLogado);
        }

        return formatacaoContratoRetorno;
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe FormatacaoItensContrato com esse id cadastrado */

        FormatacaoContrato f = genericPersister.findByUniqueProperty(FormatacaoContrato.class, "id", id);
        if (f == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN050", id);
            throw ex;
        }

        formatacaoItensContratoDAO.excluir(f);

    }

}
