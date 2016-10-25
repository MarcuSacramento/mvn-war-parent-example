package br.gov.mj.side.web.service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import br.gov.mj.side.entidades.enums.EnumStatusContrato;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dao.ContratoDAO;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.AgrupamentoDto;
import br.gov.mj.side.web.dto.enderecoLicitacao.BemUfLicitacaoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ContratoService {

    private Map<String, BigDecimal> mapaBensDoContrato;

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private LicitacaoProgramaService licitacaoProgramaService;

    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    @Inject
    private ContratoDAO contratoDAO;

    public List<AgrupamentoLicitacao> buscarAgrupamentoLicitacao(Contrato contrato) {
        if (contrato == null || contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return contratoDAO.buscarAgrupamentoLicitacao(contrato.getId());
    }

    public List<Contrato> buscarPaginado(ContratoDto contratoDto, int first, int size, EnumOrder order, String propertyOrder) {

        if (contratoDto == null) {
            throw new IllegalArgumentException("Parâmetro contratoDto não pode ser null");
        }

        return contratoDAO.buscarPaginado(contratoDto, first, size, order, propertyOrder);
    }

    public List<Contrato> buscarSemPaginacao(ContratoDto contratoDto) {

        if (contratoDto == null) {
            throw new IllegalArgumentException("Parâmetro contratoDto não pode ser null");
        }

        return contratoDAO.buscarSemPaginacao(contratoDto);
    }

    public List<Contrato> buscarContratosForaDaVigencia() {

        return contratoDAO.buscarContratosForaDaVigencia();
    }

    public List<Contrato> buscarSemPaginacaoOrdenado(ContratoDto contratoDto, EnumOrder order, String propertyOrder) {

        if (contratoDto == null) {
            throw new IllegalArgumentException("Parâmetro contratoDto não pode ser null");
        }

        return contratoDAO.buscarSemPaginacaoOrdenado(contratoDto, order, propertyOrder);
    }

    public Contrato buscarPeloId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Contrato com esse id cadastrado */

        Contrato c = contratoDAO.buscarPeloId(id);

        if (c == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN049", id);
            throw ex;
        }
        return c;
    }

    public Contrato alterarStatusDoContrato(Contrato contrato) {
        if (contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do contrato não pode ser null");
        }
        return contratoDAO.alterarStatusDoContrato(contrato);
    }

    public Contrato incluirAlterar(Contrato contrato, String usuarioLogado) {

        Contrato contratoRetorno = null;

        validarObjetoNulo(contrato);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (contrato.getPrograma() == null || contrato.getPrograma().getId() == null) {
            ex.addErrorMessage("Escolha do Programa obrigatória.");
            throw ex;
        }

        if (contrato.getNumeroContrato() == null || contrato.getNumeroContrato().equals("")) {
            ex.addErrorMessage("Número do contrato obrigatório.");
            throw ex;
        }

        if (contrato.getDataVigenciaInicioContrato() == null || contrato.getDataVigenciaFimContrato() == null) {
            ex.addErrorMessage("Período de vigência obrigatório.");
            throw ex;
        }

        if (contrato.getDataVigenciaInicioContrato().isAfter(contrato.getDataVigenciaFimContrato())) {
            ex.addErrorMessage("Data inicial deve ser menor que data final do Período de vigência.");
            throw ex;
        }

        if (contrato.getNumeroProcessoSEI() == null || contrato.getNumeroProcessoSEI().equals("")) {
            ex.addErrorMessage("NUP - SEI obrigatório.");
            throw ex;
        }

        if (contrato.getListaAgrupamentosLicitacao() == null || contrato.getListaAgrupamentosLicitacao().isEmpty()) {
            ex.addErrorMessage("Objeto(s) do contrato obrigatório");
            throw ex;
        }

        if (contrato.getFornecedor() == null || contrato.getFornecedor().getId() == null) {
            ex.addErrorMessage("Escolha do Fornecedor obrigatória.");
            throw ex;
        }

        if (contrato.getRepresentanteLegal() == null || contrato.getRepresentanteLegal().getId() == null) {
            ex.addErrorMessage("Escolha do Representante obrigatória.");
            throw ex;
        }

        if (contrato.getPreposto() == null || contrato.getPreposto().getId() == null) {
            ex.addErrorMessage("Escolha do Preposto obrigatória.");
            throw ex;
        }

        if (contrato.getId() == null) {
            contratoRetorno = contratoDAO.incluir(contrato, usuarioLogado);
        } else {
            contratoRetorno = contratoDAO.alterar(contrato, usuarioLogado, isPeriodoDeVigenciaAlterado(contrato));
        }
        return contratoRetorno;
    }

    private boolean isPeriodoDeVigenciaAlterado(Contrato contrato) {
        Contrato contratoAtual = contratoDAO.buscarPeloId(contrato.getId());
        return (!(contrato.getDataVigenciaInicioContrato().isEqual(contratoAtual.getDataVigenciaInicioContrato()) && contrato.getDataVigenciaFimContrato().isEqual(contratoAtual.getDataVigenciaFimContrato())));
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe contrato com esse id cadastrado */

        Contrato c = genericPersister.findByUniqueProperty(Contrato.class, "id", id);
        if (c == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN049", id);
            throw ex;
        }

        contratoDAO.excluir(c);

    }

    private void validarObjetoNulo(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }
    }

    public List<AgrupamentoDto> buscarEnderecosDoContrato(Contrato contrato) {

        List<BemUfLicitacaoDto> lista = buscarListaEnderecosDesagrupado(contrato);
        Map<Long, AgrupamentoDto> mapa = new HashMap<Long, AgrupamentoDto>();

        for (BemUfLicitacaoDto bemUfLicitacaoDto : lista) {

            if (bemUfLicitacaoDto.getAgrupamento() != null) {

                Long chave = bemUfLicitacaoDto.getAgrupamento().getId();

                if (mapa.get(chave) != null) {
                    mapa.get(chave).getBensUfs().add(bemUfLicitacaoDto);

                } else {
                    AgrupamentoDto dto = new AgrupamentoDto();
                    dto.setId(bemUfLicitacaoDto.getAgrupamento().getId());
                    dto.setNome(bemUfLicitacaoDto.getAgrupamento().getNomeAgrupamento());
                    dto.setTipoAgrupamentoLicitacao(bemUfLicitacaoDto.getAgrupamento().getTipoAgrupamentoLicitacao());
                    dto.getBensUfs().add(bemUfLicitacaoDto);
                    mapa.put(chave, dto);
                }
            }
        }

        return licitacaoProgramaService.transformaMapEmListaAgrupamentoDto(mapa);

    }

    private List<BemUfLicitacaoDto> buscarListaEnderecosDesagrupado(Contrato contrato) {
        Contrato contratoRetorno = buscarPeloId(contrato.getId());
        Map<String, BemUfLicitacaoDto> mapa = licitacaoProgramaService.buscarMapaEnderecosPorBemUf(contrato.getPrograma());

        for (AgrupamentoLicitacao agrupamentoLicitacao : contratoRetorno.getListaAgrupamentosLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {

                    String chaveIdBem = bemUf.getBem().getId().toString();
                    String chaveIdUf = bemUf.getUf().getSiglaUf();
                    String chave = chaveIdBem + "-" + chaveIdUf;

                    if (mapa.get(chave) != null) {
                        mapa.get(chave).setAgrupamento(agrupamentoLicitacao);
                    }

                }
            }
        }

        return licitacaoProgramaService.transformaMapEmListaBemUfLicitacaoDto(mapa);
    }

    public BigDecimal buscarValorDoContrato(Contrato contrato) {
        Contrato contratoRetorno = buscarPeloId(contrato.getId());
        BigDecimal total = BigDecimal.ZERO;

        for (AgrupamentoLicitacao agrupamentoLicitacao : contratoRetorno.getListaAgrupamentosLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                total = total.add(selecaoItem.getValorTotalARegistrar());

            }
        }
        return total;
    }

    public BigDecimal buscarSaldoExecutadoDoContrato(Contrato contrato, boolean buscarSomenteOfComunicada) {
        if (contrato == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return buscarSaldo(contrato, null, buscarSomenteOfComunicada);
    }

    public BigDecimal buscarValorTotalDaOrdemDeFornecimento(OrdemFornecimentoContrato ordemFornecimentoContrato) {

        if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro ordemFornecimentoContrato não pode ser null");
        }

        if (ordemFornecimentoContrato.getContrato() == null) {
            throw new IllegalArgumentException("Parâmetro contrato não pode ser null");
        }

        return buscarSaldo(ordemFornecimentoContrato.getContrato(), ordemFornecimentoContrato, false);

    }

    private void buscaValorItem(Contrato contrato) {
        mapaBensDoContrato = new HashMap<String, BigDecimal>();
        for (AgrupamentoLicitacao agrupamentoLicitacao : contrato.getListaAgrupamentosLicitacao()) {
            for (SelecaoItem selecaoItem : agrupamentoLicitacao.getListaSelecaoItem()) {
                for (BemUf bemUf : selecaoItem.getListaBemUf()) {
                    Long idBem = bemUf.getBem().getId();
                    Long idUf = bemUf.getUf().getId();
                    String chave = idBem + "#" + idUf;
                    mapaBensDoContrato.put(chave, selecaoItem.getValorUnitario());
                }
            }
        }

    }

    private BigDecimal buscarSaldo(Contrato contrato, OrdemFornecimentoContrato ordemFornecimentoContrato, boolean buscarSomenteOfComunicada) {
        Contrato contratoRetorno = buscarPeloId(contrato.getId());
        BigDecimal executado = BigDecimal.ZERO;
        BigDecimal totalParcial = null;

        buscaValorItem(contratoRetorno);
        for (OrdemFornecimentoContrato ofContrato : contratoRetorno.getListaOrdemFornecimento()) {

            // Se a O.F. foi cancelada então o valor não será contabilizado.
            HistoricoComunicacaoGeracaoOrdemFornecimentoContrato ultimoHistorico = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ofContrato, false);
            if (ultimoHistorico != null && ultimoHistorico.getPossuiCancelamento()) {
                continue;
            }

            // Se for buscar somente as comunicadas então se a O.F. não tiver
            // histórico comunicado
            // Significa que não foi comunicada ainda e este registro será
            // ignorado.
            if (buscarSomenteOfComunicada) {
                if (ultimoHistorico == null || !ultimoHistorico.getPossuiComunicado()) {
                    continue;
                }
            }

            if (ordemFornecimentoContrato == null || ordemFornecimentoContrato.getId().equals(ofContrato.getId())) {
                for (ItensOrdemFornecimentoContrato itensOrdemFornecimentoContrato : ofContrato.getListaItensOrdemFornecimento()) {
                    totalParcial = BigDecimal.ZERO;
                    Long idBem = itensOrdemFornecimentoContrato.getItem().getId();
                    Long idUf = itensOrdemFornecimentoContrato.getLocalEntrega().getMunicipio().getUf().getId();
                    String chave = idBem + "#" + idUf;

                    totalParcial = mapaBensDoContrato.get(chave).multiply(new BigDecimal(itensOrdemFornecimentoContrato.getQuantidade()));
                    executado = executado.add(totalParcial);
                }
            }

        }
        return executado;
    }

    public void alterarStatusContratosForaVigencia() {

        List<Contrato> listaContrato = buscarContratosForaDaVigencia();
        for (Contrato contrato : listaContrato) {

            contrato.setStatusContrato(EnumStatusContrato.FINALIZADO);
            alterarStatusDoContrato(contrato);
        }
    }

}
