package br.gov.mj.side.web.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoLista;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaAnexo;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.RecursoFinanceiroEmenda;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dao.InscricaoProgramaDAO;
import br.gov.mj.side.web.dao.PublicizacaoDAO;
import br.gov.mj.side.web.dto.AcaoEmendaComSaldoDto;
import br.gov.mj.side.web.dto.EmendaComSaldoDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PublicizacaoService {

    @Inject
    private PublicizacaoDAO publicizacaoDAO;

    @Inject
    private InscricaoProgramaDAO inscricaoProgramaDAO;

    @Inject
    private ProgramaService programaService;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private RecursoFinanceiroService recursoFinanceiroService;

    @Inject
    private InscricaoProgramaElegibilidadeService inscricaoProgramaElegibilidadeService;

    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;

    PermissaoProgramaDto permissaoProgramaDto = new PermissaoProgramaDto();

    public List<ProgramaHistoricoPublicizacao> buscarHistoricoVigenciaContrato(Contrato contrato) {

        if (contrato == null || contrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do contrato não pode ser null");
        }

        return publicizacaoDAO.buscarHistoricoVigenciaContrato(contrato);
    }

    public void criaHistoricoVigenciaContrato(Contrato contrato, String usuarioLogado) {
        publicizacaoDAO.criaHistoricoVigenciaContrato(publicizacaoDAO.buscarUltimoProgramaHistoricoPublicizacao(contrato.getPrograma()), contrato, usuarioLogado);
    }
    
    public void criaHistoricoComunicacaoOrdemFornecimento(Programa programa, String usuarioLogado){
        ProgramaHistoricoPublicizacao ultimoHistorico = publicizacaoDAO.buscarUltimoProgramaHistoricoPublicizacao(programa);
        ultimoHistorico.setStatusPrograma(programa.getStatusPrograma());
        publicizacaoDAO.criaHistoricoComunicacaoOrdemFornecimento(ultimoHistorico, usuarioLogado);
    }
    
    public void criaHistoricoCancelamentoOrdemFornecimento(Programa programa, String usuarioLogado){
        ProgramaHistoricoPublicizacao ultimoHistorico = publicizacaoDAO.buscarUltimoProgramaHistoricoPublicizacao(programa);
        ultimoHistorico.setStatusPrograma(programa.getStatusPrograma());
        publicizacaoDAO.criaHistoricoCancelamentoOrdemFornecimento(ultimoHistorico, usuarioLogado);
    }

    public ProgramaHistoricoPublicizacao buscarUltimoProgramaHistoricoPublicizacao(Programa programa) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return publicizacaoDAO.buscarUltimoProgramaHistoricoPublicizacao(programa);
    }

    public Programa publicarListaAvaliacao(byte[] conteudo, LocalDate dataInicioRecursoAvaliacao, LocalDate dataFimRecursoAvaliacao, LocalDate dataInicialCadastroLocalEntrega, LocalDate dataFinalCadastroLocalEntrega, Programa programa, String usuarioLogado, EnumTipoLista tipoLista) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        ProgramaHistoricoPublicizacao historicoPublicizacao = buscarUltimoProgramaHistoricoPublicizacao(programa);
        if (EnumTipoLista.PRELIMINAR.equals(tipoLista)) {
            validarPeriodoRecursoListaAvaliacao(dataInicioRecursoAvaliacao, dataFimRecursoAvaliacao, ex, historicoPublicizacao);
        } else if (EnumTipoLista.DEFINITIVA.equals(tipoLista)) {
            validarPeriodoCadastroLocalEntrega(dataInicialCadastroLocalEntrega, dataFinalCadastroLocalEntrega, ex, historicoPublicizacao);
        }
        return publicizacaoDAO.publicarListaAvaliacao(conteudo, dataInicioRecursoAvaliacao, dataFimRecursoAvaliacao, dataInicialCadastroLocalEntrega, dataFinalCadastroLocalEntrega, programa, usuarioLogado);
    }

    private void validarPeriodoRecursoListaAvaliacao(LocalDate dataInicioRecursoAvaliacao, LocalDate dataFimRecursoAvaliacao, BusinessException ex, ProgramaHistoricoPublicizacao historicoPublicizacao) {
        if (dataFimRecursoAvaliacao.isBefore(dataInicioRecursoAvaliacao)) {
            ex.addErrorMessage("Data início maior que data fim");
            throw ex;
        }
        // Verificar se a data do recurso esta no prazo da data de análise
        if (dataInicioRecursoAvaliacao.isBefore(historicoPublicizacao.getDataInicialAnalise()) || dataFimRecursoAvaliacao.isBefore(historicoPublicizacao.getDataInicialAnalise()) || dataInicioRecursoAvaliacao.isAfter(historicoPublicizacao.getDataFinalAnalise())
                || dataFimRecursoAvaliacao.isAfter(historicoPublicizacao.getDataFinalAnalise())) {
            ex.addErrorMessage("Período de recurso da classificação não esta de acordo com o período de análise(" + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataInicialAnalise(), "dd/MM/yyyy") + " até "
                    + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataFinalAnalise(), "dd/MM/yyyy") + ")");
            throw ex;
        }

        // Verificar se a data do recurso da classificação é posterior a data de
        // recurso da elegibilidade
        if (dataInicioRecursoAvaliacao.isBefore(historicoPublicizacao.getDataInicialRecursoElegibilidade()) || dataFimRecursoAvaliacao.isBefore(historicoPublicizacao.getDataFinalRecursoElegibilidade())) {
            ex.addErrorMessage("Período de recurso da classificação deve ser após o período de recurso da elegibilidade(" + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataInicialRecursoElegibilidade(), "dd/MM/yyyy") + " até "
                    + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataFinalRecursoElegibilidade(), "dd/MM/yyyy") + ")");
            throw ex;
        }
    }

    private void validarPeriodoCadastroLocalEntrega(LocalDate dataInicialCadastroLocalEntrega, LocalDate dataFinalCadastroLocalEntrega, BusinessException ex, ProgramaHistoricoPublicizacao historicoPublicizacao) {
        if (dataFinalCadastroLocalEntrega.isBefore(dataInicialCadastroLocalEntrega)) {
            ex.addErrorMessage("Data início maior que data fim");
            throw ex;
        }

        // Verificar se a data para cadastro do locais de entrega é posterior a
        // data do período de analise
        if (dataInicialCadastroLocalEntrega.isBefore(historicoPublicizacao.getDataInicialAnalise()) || dataInicialCadastroLocalEntrega.isBefore(historicoPublicizacao.getDataFinalAnalise())) {
            ex.addErrorMessage("Período para cadastro de locais de entrega deve ser após o perído da fase de análise" + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataInicialAnalise(), "dd/MM/yyyy") + " até "
                    + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataFinalAnalise(), "dd/MM/yyyy") + ")");
            throw ex;
        }
    }

    public Programa publicarListaElegibilidade(byte[] conteudo, LocalDate dataInicioRecursoElegibilidade, LocalDate dataFimRecursoElegibilidade, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        List<ListaElegibilidadePublicado> listasPublicadasElegibilidade = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programa.getId()));
        if (listasPublicadasElegibilidade.isEmpty()) {
            if (dataFimRecursoElegibilidade.isBefore(dataInicioRecursoElegibilidade)) {
                ex.addErrorMessage("Data início maior que data fim");
                throw ex;
            }

            // Verificar se a data do recurso esta no prazo da data de análise
            ProgramaHistoricoPublicizacao historicoPublicizacao = buscarUltimoProgramaHistoricoPublicizacao(programa);
            if (dataInicioRecursoElegibilidade.isBefore(historicoPublicizacao.getDataInicialAnalise()) || dataFimRecursoElegibilidade.isBefore(historicoPublicizacao.getDataInicialAnalise()) || dataInicioRecursoElegibilidade.isAfter(historicoPublicizacao.getDataFinalAnalise())
                    || dataFimRecursoElegibilidade.isAfter(historicoPublicizacao.getDataFinalAnalise())) {
                ex.addErrorMessage("Período de recurso da classificação não esta de acordo com o período de análise(" + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataInicialAnalise(), "dd/MM/yyyy") + " até "
                        + DataUtil.converteDataDeLocalDateParaString(historicoPublicizacao.getDataFinalAnalise(), "dd/MM/yyyy") + ")");
                throw ex;
            }
        }

        return publicizacaoDAO.publicarListaElegibilidade(conteudo, dataInicioRecursoElegibilidade, dataFimRecursoElegibilidade, programa, usuarioLogado);
    }

    public Programa republicar(Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = programaService.buscarHistoricoPublicizacao(programa).get(0);

        ProgramaHistoricoPublicizacao programaHistoricoPublicizacaoRepublicar = new ProgramaHistoricoPublicizacao();
        programaHistoricoPublicizacaoRepublicar.setDataCadastro(LocalDateTime.now());
        programaHistoricoPublicizacaoRepublicar.setDataFinalProposta(programaHistoricoPublicizacao.getDataFinalProposta());
        programaHistoricoPublicizacaoRepublicar.setDataInicialProposta(programaHistoricoPublicizacao.getDataInicialProposta());
        programaHistoricoPublicizacaoRepublicar.setDataPublicacaoDOU(programaHistoricoPublicizacao.getDataPublicacaoDOU());
        programaHistoricoPublicizacaoRepublicar.setMotivo(programaHistoricoPublicizacao.getMotivo());
        programaHistoricoPublicizacaoRepublicar.setPrograma(programa);
        programaHistoricoPublicizacaoRepublicar.setStatusPrograma(EnumStatusPrograma.PUBLICADO);
        programaHistoricoPublicizacaoRepublicar.setTipoPrograma(programaHistoricoPublicizacao.getTipoPrograma());
        programaHistoricoPublicizacaoRepublicar.setUsuarioCadastro(usuarioLogado);
        programaHistoricoPublicizacaoRepublicar.setDataInicialAnalise(programaHistoricoPublicizacao.getDataInicialAnalise());
        programaHistoricoPublicizacaoRepublicar.setDataFinalAnalise(programaHistoricoPublicizacao.getDataFinalAnalise());

        return publicar(programaHistoricoPublicizacaoRepublicar, programa, usuarioLogado);
    }

    public Programa publicar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        List<ProgramaHistoricoPublicizacao> listaHistorico = new ArrayList<ProgramaHistoricoPublicizacao>();
        listaHistorico.add(programaHistoricoPublicizacao);

        if (publicizacaoDAO.passadoPrazoParaPropostas(listaHistorico)) {
            ex.addErrorMessage("MN038");
            throw ex;
        }

        if (!todasEmendasDosRescursosEstaoLiberadas(programa)) {
            ex.addErrorMessage("MN040");
            throw ex;
        }

        if (!todasEmendasDosRescursosPossuemPeloMenosUmBeneficiario(programa)) {
            ex.addErrorMessage("MN041");
            throw ex;
        }

        if (!todosRecursosPossuemSaldo(programa)) {
            ex.addErrorMessage("MN035");
            throw ex;

        }

        if (!recursosSupremGastosDeBensEKits(programa)) {
            ex.addErrorMessage("MN034");
            throw ex;
        }

        Integer id;
        if (programa.getIdentificadorProgramaPublicado() == null) {
            id = extrairIdDoProgramaPublicado(programa);
        } else {
            id = programa.getIdentificadorProgramaPublicado();
        }
        return publicizacaoDAO.publicar(programaHistoricoPublicizacao, programa, usuarioLogado, id);
    }

    public Programa suspender(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        if (!possuiNovosAnexos(programa)) {
            ex.addErrorMessage("MN036");
            throw ex;
        }

        return publicizacaoDAO.suspender(programaHistoricoPublicizacao, programa, usuarioLogado);
    }

    public Programa cancelar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        if (!possuiNovosAnexos(programa)) {
            ex.addErrorMessage("MN036");
            throw ex;
        }

        return publicizacaoDAO.cancelar(programaHistoricoPublicizacao, programa, usuarioLogado);
    }

    public Programa prorrogar(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        ProgramaHistoricoPublicizacao historico = programaService.buscarHistoricoPublicizacao(programa).get(0);

        if (!programaHistoricoPublicizacao.getDataFinalProposta().isAfter(historico.getDataFinalProposta())) {
            ex.addErrorMessage("MN037");
            throw ex;
        }
        programaHistoricoPublicizacao.setId(null);
        return publicizacaoDAO.prorrogar(programaHistoricoPublicizacao, programa, usuarioLogado);
    }

    public Programa prorrogarAnalise(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        ProgramaHistoricoPublicizacao historico = programaService.buscarHistoricoPublicizacao(programa).get(0);

        if (!programaHistoricoPublicizacao.getDataFinalAnalise().isAfter(historico.getDataFinalAnalise())) {
            ex.addErrorMessage("MN037");
            throw ex;
        }
        programaHistoricoPublicizacao.setId(null);
        return publicizacaoDAO.prorrogarAnalise(programaHistoricoPublicizacao, programa, usuarioLogado);
    }

    public Programa suspenderPrazo(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        programaHistoricoPublicizacao.setId(null);
        return publicizacaoDAO.suspenderPrazo(programaHistoricoPublicizacao, programa, usuarioLogado);
    }

    public Programa reabrirPrazo(ProgramaHistoricoPublicizacao programaHistoricoPublicizacao, Programa programa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        programaHistoricoPublicizacao.setId(null);
        Programa programaRetorno = publicizacaoDAO.reabrirPrazo(programaHistoricoPublicizacao, programa, usuarioLogado);

        if (programaHistoricoPublicizacao.getDataInicialProposta().isAfter(programaHistoricoPublicizacao.getDataFinalProposta()) || !(!programaRetorno.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_PROPOSTAS) || !programaRetorno.getStatusPrograma().equals(EnumStatusPrograma.PUBLICADO))) {
            ex.addErrorMessage("MN039");
            throw ex;
        }

        return programaRetorno;

    }

    private Integer extrairIdDoProgramaPublicado(Programa programa) {
        Programa p = new Programa();
        UnidadeExecutora unEx = new UnidadeExecutora();
        Orgao orgao = new Orgao();
        orgao.setId(programa.getUnidadeExecutora().getOrgao().getId());
        unEx.setOrgao(orgao);
        p.setUnidadeExecutora(unEx);
        p.setIdentificadorProgramaPublicado(0);

        ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();
        programaPesquisaDto.setPrograma(p);

        List<Programa> lista = programaService.buscarSemPaginacaoOrdenado(programaPesquisaDto, EnumOrder.DESC, "identificadorProgramaPublicado");

        if (lista.isEmpty() || lista.get(0).getIdentificadorProgramaPublicado() == null) {
            return 1;
        } else if (lista.get(0).getId().equals(programa.getId())) {
            return lista.get(0).getIdentificadorProgramaPublicado();
        } else {
            return lista.get(0).getIdentificadorProgramaPublicado() + 1;
        }
    }

    public PermissaoProgramaDto buscarPermissoesPrograma(Programa programa) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        permitePublicar(programa);
        permiteExcluir(programa);
        permiteCancelar(programa);
        permiteProrrogar(programa);
        permiteReabrirPrazoPrograma(programa);
        permiteSuspenderPrazo(programa);
        permiteProrrogarAnalise(programa);
        permiteSuspenderPrograma(programa);
        permiteAlterar(programa);
        permiteInscricao(programa);
        permiteVisualizarListaPropostas(programa);
        permitePublicarListaElegibilidade(programa);
        permitePublicarListaAvaliacao(programa);
        permiteVincularLocaisEntrega(programa);
        permiteVincularComissaoRecebmento(programa);

        return permissaoProgramaDto;
    }

    private void permiteVincularComissaoRecebmento(Programa p) {
            permissaoProgramaDto.setVincularComissaoRecebimento(Boolean.TRUE);
    }

    private void permiteVincularLocaisEntrega(Programa p) {
        if (p.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA)) {
            permissaoProgramaDto.setVincularLocaisDeEntrega(Boolean.TRUE);
        }

    }

    private void permitePublicar(Programa p) {
        if (p.getStatusPrograma().equals(EnumStatusPrograma.FORMULADO)) {
            permissaoProgramaDto.setPublicar(Boolean.TRUE);
        }

    }

    private void permiteExcluir(Programa p) {

        if (p.getStatusPrograma().equals(EnumStatusPrograma.EM_ELABORACAO) || p.getStatusPrograma().equals(EnumStatusPrograma.FORMULADO)) {
            permissaoProgramaDto.setExcluir(Boolean.TRUE);
        }

    }

    private void permiteAlterar(Programa p) {

        if ((p.getStatusPrograma().equals(EnumStatusPrograma.EM_ELABORACAO) || p.getStatusPrograma().equals(EnumStatusPrograma.FORMULADO)) || (p.getStatusPrograma().equals(EnumStatusPrograma.PUBLICADO) && anteriorPrazoParaPorpostas(p))) {
            permissaoProgramaDto.setAlterar(Boolean.TRUE);
        }

    }

    private void permiteCancelar(Programa p) {
        if (!(p.getStatusPrograma().equals(EnumStatusPrograma.EM_ELABORACAO) || p.getStatusPrograma().equals(EnumStatusPrograma.FORMULADO) || p.getStatusPrograma().equals(EnumStatusPrograma.CANCELADO))) {
            permissaoProgramaDto.setCancelar(Boolean.TRUE);
        }

    }

    private void permiteProrrogar(Programa p) {

        if (p.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_PROPOSTAS)) {
            permissaoProgramaDto.setProrrogar(Boolean.TRUE);
        }

    }

    private void permiteProrrogarAnalise(Programa p) {

        if (p.getStatusPrograma().equals(EnumStatusPrograma.EM_ANALISE)) {
            permissaoProgramaDto.setProrrogarAnalise(Boolean.TRUE);
        }

    }

    private void permiteReabrirPrazoPrograma(Programa p) {
        if (p.getStatusPrograma().equals(EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS)) {
            permissaoProgramaDto.setReabrirPrazo(Boolean.TRUE);
        }

    }

    private void permiteSuspenderPrazo(Programa p) {
        if (p.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_PROPOSTAS)) {
            permissaoProgramaDto.setSuspenderPrazo(Boolean.TRUE);
        }

    }

    private void permiteSuspenderPrograma(Programa p) {

        if (!(p.getStatusPrograma().equals(EnumStatusPrograma.EM_ELABORACAO) || p.getStatusPrograma().equals(EnumStatusPrograma.FORMULADO) || p.getStatusPrograma().equals(EnumStatusPrograma.CANCELADO) || p.getStatusPrograma().equals(EnumStatusPrograma.SUSPENSO))) {
            permissaoProgramaDto.setSuspenderPrograma(Boolean.TRUE);
        }
    }

    private void permiteInscricao(Programa p) {
        if (p.getStatusPrograma().equals(EnumStatusPrograma.ABERTO_REC_PROPOSTAS)) {
            permissaoProgramaDto.setInscrever(Boolean.TRUE);
        }
    }

    private void permiteVisualizarListaPropostas(Programa p) {
        if (p.getStatusPrograma().equals(EnumStatusPrograma.EM_ANALISE)) {
            permissaoProgramaDto.setVisualizarListaPropostas(true);
        }
    }

    private void permitePublicarListaElegibilidade(Programa p) {
        List<InscricaoPrograma> lista = new ArrayList<InscricaoPrograma>();
        lista = inscricaoProgramaDAO.buscarInscricaoProgramaPeloPrograma(p);
        for (InscricaoPrograma inscricaoPrograma : lista) {

            if (!inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.EM_ELABORACAO)) {

                if ((!inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.ENVIADA_ANALISE) && !inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.ANALISE_ELEGIBILIDADE))
                        && (inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE) && (!inscricaoPrograma.getEstaEmFaseRecursoElegibilidade() || (inscricaoPrograma.getEstaEmFaseRecursoElegibilidade() && inscricaoPrograma
                                .getFinalizadoRecursoElegibilidade())))) {
                    permissaoProgramaDto.setPublicarListaElegibilidade(true);
                } else {
                    permissaoProgramaDto.setPublicarListaElegibilidade(false);
                    return;
                }
            }
        }
    }

    private void permitePublicarListaAvaliacao(Programa p) {
        List<InscricaoPrograma> lista = new ArrayList<InscricaoPrograma>();
        lista = inscricaoProgramaDAO.buscarInscricaoProgramaPeloPrograma(p);
        for (InscricaoPrograma inscricaoPrograma : lista) {
            if (!inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.EM_ELABORACAO)) {
                if (SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(p.getId())).size() == 2) {

                    HistoricoAnaliseElegibilidade historicoAnaliseElegibilidade = inscricaoProgramaElegibilidadeService.buscarUltimoHistoricoAnaliseElegibilidade(inscricaoPrograma);

                    if (historicoAnaliseElegibilidade.getId() != null && historicoAnaliseElegibilidade.getResultadoFinalAnalise().equals(EnumResultadoFinalAnaliseElegibilidade.NAO_ELEGIVEL)) {
                        permissaoProgramaDto.setPublicarListaAvaliacao(true);
                    } else {

                        if ((!inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE) && !inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.ANALISE_AVALIACAO))
                                && (inscricaoPrograma.getStatusInscricao().equals(EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO) && (!inscricaoPrograma.getEstaEmFaseRecursoAvaliacao() || (inscricaoPrograma.getEstaEmFaseRecursoAvaliacao() && inscricaoPrograma.getFinalizadoRecursoAvaliacao())))) {
                            permissaoProgramaDto.setPublicarListaAvaliacao(true);
                        } else {
                            permissaoProgramaDto.setPublicarListaAvaliacao(false);
                            return;
                        }
                    }
                } else {
                    permissaoProgramaDto.setPublicarListaAvaliacao(false);
                    return;
                }
            }
        }
    }

    private Boolean anteriorPrazoParaPorpostas(Programa programa) {
        Boolean retorno = Boolean.FALSE;
        List<ProgramaHistoricoPublicizacao> listaHistorico = publicizacaoDAO.buscarHistoricoPublicizacao(programa.getId());
        if (!listaHistorico.isEmpty()) {
            ProgramaHistoricoPublicizacao hist = listaHistorico.get(0);
            LocalDate dataAtual = LocalDate.now();
            LocalDate dtInicial = hist.getDataInicialProposta();
            if (dataAtual.isBefore(dtInicial) && hist.getStatusPrograma().equals(EnumStatusPrograma.PUBLICADO)) {
                retorno = Boolean.TRUE;
            }
        }
        return retorno;
    }

    public void atualizarStatusProgramas() {

        publicizacaoDAO.atualizarStatusProgramas();
    }

    public boolean recursosSupremGastosDeBensEKits(Programa programa) {
        BigDecimal diferenca = BigDecimal.ZERO;
        diferenca = diferenca.add(buscarSomaFontesRecursoPrograma(programa));
        diferenca = diferenca.subtract(buscarSomaGastosPrograma(programa));
        return BigDecimal.ZERO.compareTo(diferenca) <= 0;
    }

    private boolean todosRecursosPossuemSaldo(Programa programa) {

        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : programa.getRecursosFinanceiros()) {
            AcaoEmendaComSaldoDto saldoDto = recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(programaRecursoFinanceiro.getAcaoOrcamentaria());

            if (programa.getStatusPrograma().equals(EnumStatusPrograma.PUBLICADO)) {
                if (saldoDto.getSaldoAcaoOrcamentaria().compareTo(BigDecimal.ZERO) < 0) {
                    return false;
                }

                if (!todasEmendasPossuemSaldo(saldoDto.getListaSaldoEmenda())) {
                    return false;
                }

            } else {
                if (saldoDto.getSaldoAcaoOrcamentaria().subtract(programaRecursoFinanceiro.getValorUtilizar()).compareTo(BigDecimal.ZERO) < 0) {
                    return false;
                }

                if (!todasEmendasPossuemSaldo(saldoDto.getListaSaldoEmenda(), programaRecursoFinanceiro.getRecursoFinanceiroEmendas())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean todasEmendasDosRescursosEstaoLiberadas(Programa programa) {
        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : programa.getRecursosFinanceiros()) {
            for (RecursoFinanceiroEmenda RecursoFinanceiroEmenda : programaRecursoFinanceiro.getRecursoFinanceiroEmendas()) {
                if (!RecursoFinanceiroEmenda.getEmendaParlamentar().getPossuiLiberacao()) {
                    return false;
                }
            }

        }
        return true;
    }

    private boolean todasEmendasDosRescursosPossuemPeloMenosUmBeneficiario(Programa programa) {

        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : programa.getRecursosFinanceiros()) {
            for (RecursoFinanceiroEmenda RecursoFinanceiroEmenda : programaRecursoFinanceiro.getRecursoFinanceiroEmendas()) {
                List<BeneficiarioEmendaParlamentar> lista = acaoOrcamentariaService.buscarBeneficiarioEmendaParlamentar(RecursoFinanceiroEmenda.getEmendaParlamentar());
                if (lista.isEmpty()) {
                    return false;
                }
            }

        }
        return true;
    }

    private boolean todasEmendasPossuemSaldo(List<EmendaComSaldoDto> lista) {
        for (EmendaComSaldoDto emendaComSaldoDto : lista) {
            if (emendaComSaldoDto.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }
        return true;

    }

    private boolean todasEmendasPossuemSaldo(List<EmendaComSaldoDto> listaEmendaSaldo, List<RecursoFinanceiroEmenda> listaRecursoEmenda) {
        for (EmendaComSaldoDto emendaComSaldoDto : listaEmendaSaldo) {
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : listaRecursoEmenda) {
                if (recursoFinanceiroEmenda.getEmendaParlamentar().getId().equals(emendaComSaldoDto.getEmendaParlamentar().getId()) && emendaComSaldoDto.getSaldo().subtract(recursoFinanceiroEmenda.getValorUtilizar()).compareTo(BigDecimal.ZERO) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private BigDecimal buscarSomaGastosPrograma(Programa programa) {

        BigDecimal totalGastos = BigDecimal.ZERO;
        totalGastos = totalGastos.add(pegarSomaBens(programa.getProgramaBens()));
        totalGastos = totalGastos.add(pegarSomaKits(programa.getProgramaKits()));
        return totalGastos;
    }

    private BigDecimal buscarSomaFontesRecursoPrograma(Programa programa) {

        BigDecimal totalFontes = BigDecimal.ZERO;
        totalFontes = totalFontes.add(pegarSomaRecursos(programa.getRecursosFinanceiros()));
        return totalFontes;
    }

    private BigDecimal pegarSomaRecursos(List<ProgramaRecursoFinanceiro> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : lista) {
            total = total.add(programaRecursoFinanceiro.getTotal());
        }
        return total;
    }

    private BigDecimal pegarSomaBens(List<ProgramaBem> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaBem programaBem : lista) {
            total = total.add(programaBem.getBem().getValorEstimadoBem().multiply(new BigDecimal(programaBem.getQuantidade().toString())));
        }
        return total;
    }

    private BigDecimal pegarSomaKits(List<ProgramaKit> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaKit programaKit : lista) {
            for (KitBem kitBem : programaKit.getKit().getKitsBens()) {
                total = total.add(kitBem.getBem().getValorEstimadoBem().multiply(new BigDecimal(kitBem.getQuantidade().toString()).multiply(new BigDecimal(programaKit.getQuantidade().toString()))));
            }

        }
        return total;
    }

    private boolean possuiNovosAnexos(Programa programa) {
        for (ProgramaAnexo programaAnexo : programa.getAnexos()) {
            if (programaAnexo.getId() == null) {
                return true;
            }
        }
        return false;

    }

}
