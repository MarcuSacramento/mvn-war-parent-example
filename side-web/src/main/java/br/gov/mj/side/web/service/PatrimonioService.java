package br.gov.mj.side.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.patrimoniamento.PatrimonioObjetoFornecimento;
import br.gov.mj.side.web.dao.PatrimonioDAO;
import br.gov.mj.side.web.dto.PatrimoniamentoResultadoPesquisaDto;
import br.gov.mj.side.web.dto.PatrimonioObjetoFornecimentoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PatrimonioService {

    @Inject
    private PatrimonioDAO patrimonioDAO;

    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public List<PatrimonioObjetoFornecimento> buscarPaginado(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, int first, int size, EnumOrder order, String propertyOrder) {

        if (patrimonioObjetoFornecimentoDto == null) {
            throw new IllegalArgumentException("Parâmetro patrimonioObjetoFornecimentoDto não pode ser null");
        }

        return patrimonioDAO.buscarPaginado(patrimonioObjetoFornecimentoDto, first, size, order, propertyOrder);
    }

    public List<PatrimonioObjetoFornecimento> buscarSemPaginacao(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto) {

        if (patrimonioObjetoFornecimentoDto == null) {
            throw new IllegalArgumentException("Parâmetro patrimonioObjetoFornecimentoDto não pode ser null");
        }

        return patrimonioDAO.buscarSemPaginacao(patrimonioObjetoFornecimentoDto);
    }

    public List<PatrimonioObjetoFornecimento> buscarSemPaginacaoOrdenado(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto, EnumOrder order, String propertyOrder) {

        if (patrimonioObjetoFornecimentoDto == null) {
            throw new IllegalArgumentException("Parâmetro patrimonioObjetoFornecimentoDto não pode ser null");
        }

        return patrimonioDAO.buscarSemPaginacaoOrdenado(patrimonioObjetoFornecimentoDto, order, propertyOrder);
    }

    public PatrimonioObjetoFornecimento buscarPeloId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe PatrimonioObjetoFornecimento com esse id cadastrado */

        PatrimonioObjetoFornecimento c = patrimonioDAO.buscarPeloId(id);

        if (c == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("Não foi encontrado patrimonio com o id informado");
            throw ex;
        }
        return c;
    }

    public List<PatrimonioObjetoFornecimento> incluirAlterar(List<PatrimonioObjetoFornecimento> listaPatrimoniamentoAtualizar, List<PatrimonioObjetoFornecimento> listaPatrimoniamentoAnterior) {
        if (listaPatrimoniamentoAtualizar == null) {
            throw new IllegalArgumentException("A lista de patrimônio para atualizar não pode ser nula");
        }

        if (listaPatrimoniamentoAnterior == null) {
            throw new IllegalArgumentException("A lista anterior de patrimonio não pode ser nula");
        }

        return patrimonioDAO.incluirAlterar(listaPatrimoniamentoAtualizar, listaPatrimoniamentoAnterior);
    }

    public List<PatrimonioObjetoFornecimento> incluirAlterar(List<PatrimonioObjetoFornecimento> listaPatrimonio, String usuarioLogado) {

        validarObjetoNulo(listaPatrimonio);

        List<PatrimonioObjetoFornecimento> listaRetornar = new ArrayList<PatrimonioObjetoFornecimento>();

        boolean alteradoStatusObjetoFornecimento = false;
        for (PatrimonioObjetoFornecimento pof : listaPatrimonio) {

            if (pof.getObjetoFornecimentoContrato() == null || pof.getObjetoFornecimentoContrato().getId() == null) {
                throw new IllegalArgumentException("O ObjetoFornecimentoContrato não pode ser nulo");
            }

            // Irá alterar informações do objetoFornecimento contrato
            if (!alteradoStatusObjetoFornecimento) {
                ordemFornecimentoContratoService.alterarStatusObjetoFornecimentoAoInformarPatrimoniamento(pof.getObjetoFornecimentoContrato());
                alteradoStatusObjetoFornecimento = true;
            }

            if (pof.getId() == null) {
                listaRetornar.add(patrimonioDAO.incluir(pof));
            } else {
                listaRetornar.add(patrimonioDAO.alterar(pof));
            }
        }

        return listaRetornar;
    }

    public PatrimonioObjetoFornecimento incluir(List<PatrimonioObjetoFornecimento> listaPatrimonio, String usuarioLogado) {

        PatrimonioObjetoFornecimento patrimonioObjetoFornecimentoRetorno = null;

        validarObjetoNulo(listaPatrimonio);

        ObjetoFornecimentoContrato objetoFornecimentoContrato = null;

        for (PatrimonioObjetoFornecimento pof : listaPatrimonio) {

            if (pof.getId() == null) {

            } else {
                patrimonioDAO.alterar(pof);
            }

            if (objetoFornecimentoContrato == null) {

                // Irá setar a informar se este bem é patrimoniável ou não
                ordemFornecimentoContratoService.alterarStatusObjetoFornecimentoAoInformarPatrimoniamento(pof.getObjetoFornecimentoContrato());
                objetoFornecimentoContrato = ordemFornecimentoContratoService.buscarObjetoFornecimentoContrato(pof.getObjetoFornecimentoContrato().getId());
            }

            pof.setObjetoFornecimentoContrato(objetoFornecimentoContrato);
            patrimonioObjetoFornecimentoRetorno = patrimonioDAO.incluir(pof);
        }

        return patrimonioObjetoFornecimentoRetorno;
    }

    public void excluir(PatrimonioObjetoFornecimento patrimonioObjetoFornecimento) {
        if (patrimonioObjetoFornecimento == null || patrimonioObjetoFornecimento.getId() == null) {
            throw new IllegalArgumentException("O patrimonio não pode ser nulo");
        }
        patrimonioDAO.excluir(patrimonioObjetoFornecimento);
    }

    public List<PatrimoniamentoResultadoPesquisaDto> pesquisarListaDeItens(PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto) {
        if (patrimonioObjetoFornecimentoDto == null) {
            throw new IllegalArgumentException("Parâmetro patrimonioObjetoFornecimentoDto não pode ser null");
        }

        if (patrimonioObjetoFornecimentoDto.getEntidade() == null || patrimonioObjetoFornecimentoDto.getEntidade().getId() == null) {
            throw new IllegalArgumentException("Parâmetro entidade do patrimonioObjetoFornecimentoDto não pode ser null");
        }
        return patrimonioDAO.pesquisarListaDeItens(patrimonioObjetoFornecimentoDto);
    }

    private void validarObjetoNulo(List<PatrimonioObjetoFornecimento> listaPatrimonio) {

        if (listaPatrimonio == null || listaPatrimonio.isEmpty()) {
            throw new IllegalArgumentException("A lista de patrimônio não pode ser vazia");
        }
    }

    //Objetos que podem ser patrimoniados... se um objeto tiver formatações unitárias e em lote então o Lote não pode ser patrimoniado pois não é um item, é apenas uma formatação.
    // Se um objteto não possui formatação unitária e somente em Lote então neste caso o Lote representa todos os itens doados e poderá ser patrimoniado.
    public List<PatrimoniamentoResultadoPesquisaDto> verificarObjetosPassiveisDePatrimoniamento(List<PatrimoniamentoResultadoPesquisaDto> listaObjetoFornecimentoContratos) {

        String chave = "";
        Map<String, List<PatrimoniamentoResultadoPesquisaDto>> mapa = new HashMap<String, List<PatrimoniamentoResultadoPesquisaDto>>();

        for (PatrimoniamentoResultadoPesquisaDto ofo : listaObjetoFornecimentoContratos) {
            chave = ofo.getObjetoFornecimentoContrato().getItem().getId().toString() + "-" + ofo.getObjetoFornecimentoContrato().getOrdemFornecimento().getId().toString() + "-" + ofo.getObjetoFornecimentoContrato().getLocalEntrega().getId().toString();

            if (mapa.containsKey(chave)) {
                mapa.get(chave).add(ofo);
            } else {

                List<PatrimoniamentoResultadoPesquisaDto> lista = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();
                lista.add(ofo);
                mapa.put(chave, lista);
            }
        }
        List<PatrimoniamentoResultadoPesquisaDto> listaRetornar = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();
        listaRetornar = montarListaObjeto(mapa);
        return listaRetornar;
    }

    private List<PatrimoniamentoResultadoPesquisaDto> montarListaObjeto(Map<String, List<PatrimoniamentoResultadoPesquisaDto>> mapa) {

        List<PatrimoniamentoResultadoPesquisaDto> listaRetornar = new ArrayList<PatrimoniamentoResultadoPesquisaDto>();

        for (Map.Entry<String, List<PatrimoniamentoResultadoPesquisaDto>> pair : mapa.entrySet()) {
            for (PatrimoniamentoResultadoPesquisaDto patrimoniamento : pair.getValue()) {
                if (pair.getValue().size() == 1) {
                    listaRetornar.add(patrimoniamento);
                } else {
                    for (PatrimoniamentoResultadoPesquisaDto ofo : pair.getValue()) {
                        if (ofo.getObjetoFornecimentoContrato().getFormaVerificacao() != EnumFormaVerificacaoFormatacao.LOTE) {
                            listaRetornar.add(ofo);
                        }
                    }
                    break;
                }
            }
        }
        return listaRetornar;
    }
}
