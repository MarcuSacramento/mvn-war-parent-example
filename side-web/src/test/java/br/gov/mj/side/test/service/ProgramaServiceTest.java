package br.gov.mj.side.test.service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.PotencialBeneficiarioMunicipio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.web.dao.ProgramaDAO;
import br.gov.mj.side.web.dao.PublicizacaoDAO;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;
import br.gov.mj.side.web.util.UtilDAO;

@RunWith(Arquillian.class)
public class ProgramaServiceTest {

    private static final String USUARIO_LOGADO = "usuario.logado";

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction tx;

    @Inject
    IGenericPersister genericPersister;

    @Inject
    private ProgramaService programaService;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default").resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946", "org.jadira.usertype:usertype.extended:3.2.0.GA")
                .withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.seg.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.side.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("ufs.csv", "ufs.csv").addAsResource("municipios.csv", "municipios.csv").addAsResource("funcao.csv", "funcao.csv").addAsResource("subfuncao.csv", "subfuncao.csv").addAsResource("orgao.csv", "orgao.csv").addAsResource("unidadeexecutora.csv", "unidadeexecutora.csv")
                .addAsResource("bem.csv", "bem.csv").addAsResource("subelementos.csv", "subelementos.csv").addAsResource("elementos.csv", "elementos.csv").addAsResource("beneficiarioemenda.csv", "beneficiarioemenda.csv").addAsResource("emendaparlamentar.csv", "emendaparlamentar.csv")
                .addAsResource("acaoorcamentaria.csv", "acaoorcamentaria.csv").addAsResource("partidos.csv", "partidos.csv").addAsResource("init.txt", "init.txt").addAsResource("kit.csv", "kit.csv").addAsResource("kitbem.csv", "kitbem.csv").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml").addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(ProgramaDAO.class).addClass(PublicizacaoDAO.class).addClass(UtilDAO.class).addClass(ProgramaPesquisaDto.class)
                .addClass(ProgramaService.class).addClass(Constants.class).addAsLibraries(jars);
        System.out.println(file.toString(true));
        return file;
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1)
    public void testPersistirInserindoProgramaVazio() throws Throwable {
        Programa programa = new Programa();
        try {
            programaService.incluirAlterar(programa, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }

    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(2)
    public void testIncluirNull() throws Throwable {
        try {
            programaService.incluirAlterar(null, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(3)
    public void testIncluirRegistroNomeDuplicado() {

        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        Programa programa2 = new Programa();

        programa2.setNomePrograma("nome programa");
        programa2.setNomeFantasiaPrograma("nome fantasia programa");
        programa2.setDescricaoPrograma("descricao programa");
        programa2.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa2.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa2.setNumeroProcessoSEI("11111");
        programa2.setPossuiLimitacaoGeografica(false);
        programa2.setPossuiLimitacaoMunicipalEspecifica(false);
        programa2.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa2.setUsuarioCadastro(USUARIO_LOGADO);
        programa2.setDataCadastro(LocalDateTime.now());
        programa2.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa2.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        programaService.incluirAlterar(programa2, USUARIO_LOGADO);

    }

    @Test
    @InSequence(4)
    public void testIncluirComPotenciaisBeneficiariosUfELimitacaoFalse() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        /* lista de ufs */
        List<ProgramaPotencialBeneficiarioUf> listaUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        ProgramaPotencialBeneficiarioUf ufs = new ProgramaPotencialBeneficiarioUf();
        ufs.setPrograma(programa);
        ufs.setUf(em.find(Uf.class, 1l));
        listaUfs.add(ufs);
        programa.setPotenciaisBeneficiariosUf(listaUfs);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        Assert.assertTrue(programa.getPotenciaisBeneficiariosUf().isEmpty());
    }

    @Test(expected = BusinessException.class)
    @InSequence(5)
    public void testIncluirSemPotenciaisBeneficiariosUfELimitacaoTrue() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(true);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        Assert.assertTrue(programa.getPotenciaisBeneficiariosUf().isEmpty());
    }

    @Test
    @InSequence(6)
    public void testIncluirComPotenciaisBeneficiariosUfELimitacaoTrue() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(true);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        /* lista de ufs */
        List<ProgramaPotencialBeneficiarioUf> listaUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        ProgramaPotencialBeneficiarioUf ufs = new ProgramaPotencialBeneficiarioUf();
        ufs.setPrograma(programa);
        ufs.setUf(em.find(Uf.class, 1l));
        listaUfs.add(ufs);
        programa.setPotenciaisBeneficiariosUf(listaUfs);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        Assert.assertTrue(!programa.getPotenciaisBeneficiariosUf().isEmpty());
    }

    @Test(expected = BusinessException.class)
    @InSequence(7)
    public void testIncluirSemPotenciaisBeneficiariosMunicipiosELimitacaoMunicipalTrue() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(true);
        programa.setPossuiLimitacaoMunicipalEspecifica(true);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);

        /* lista de ufs */
        List<ProgramaPotencialBeneficiarioUf> listaUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        ProgramaPotencialBeneficiarioUf ufs = new ProgramaPotencialBeneficiarioUf();
        ufs.setPrograma(programa);
        ufs.setUf(em.find(Uf.class, 1l));
        listaUfs.add(ufs);
        programa.setPotenciaisBeneficiariosUf(listaUfs);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);

    }

    @Test
    @InSequence(8)
    public void testIncluirComPotenciaisBeneficiariosMunicipiosELimitacaoMunicipalFalse() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(true);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        /* lista de ufs */
        List<ProgramaPotencialBeneficiarioUf> listaUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        ProgramaPotencialBeneficiarioUf uf = new ProgramaPotencialBeneficiarioUf();
        uf.setPrograma(programa);
        uf.setUf(em.find(Uf.class, 1l));

        /* Lista de Municípios */
        List<PotencialBeneficiarioMunicipio> listaMunicipios = new ArrayList<PotencialBeneficiarioMunicipio>();
        PotencialBeneficiarioMunicipio municipio = new PotencialBeneficiarioMunicipio();
        municipio.setPotencialBeneficiarioUF(uf);
        municipio.setMunicipio(em.find(Municipio.class, 1200013l));
        listaMunicipios.add(municipio);
        uf.setPotencialBeneficiarioMunicipios(listaMunicipios);

        listaUfs.add(uf);
        programa.setPotenciaisBeneficiariosUf(listaUfs);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        Assert.assertTrue(programa.getPotenciaisBeneficiariosUf().get(0).getPotencialBeneficiarioMunicipios().isEmpty());

    }

    @Test
    @InSequence(9)
    public void testIncluirComPotenciaisBeneficiariosMunicipiosELimitacaoMunicipalTrue() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(true);
        programa.setPossuiLimitacaoMunicipalEspecifica(true);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        /* lista de ufs */
        List<ProgramaPotencialBeneficiarioUf> listaUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        ProgramaPotencialBeneficiarioUf uf = new ProgramaPotencialBeneficiarioUf();
        uf.setPrograma(programa);
        uf.setUf(em.find(Uf.class, 1l));

        /* Lista de Municípios */
        List<PotencialBeneficiarioMunicipio> listaMunicipios = new ArrayList<PotencialBeneficiarioMunicipio>();
        PotencialBeneficiarioMunicipio municipio = new PotencialBeneficiarioMunicipio();
        municipio.setPotencialBeneficiarioUF(uf);
        municipio.setMunicipio(em.find(Municipio.class, 1200013l));
        listaMunicipios.add(municipio);
        uf.setPotencialBeneficiarioMunicipios(listaMunicipios);

        listaUfs.add(uf);
        programa.setPotenciaisBeneficiariosUf(listaUfs);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        Assert.assertTrue(!programa.getPotenciaisBeneficiariosUf().get(0).getPotencialBeneficiarioMunicipios().isEmpty());

    }

    @Test(expected = BusinessException.class)
    @InSequence(10)
    public void testAlterarSemNenhumaListaComStatusFinalizado() throws Throwable {

        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);

        Programa programaAlteracao = em.find(Programa.class, 1l);

        programaAlteracao.setUsuarioAlteracao(USUARIO_LOGADO);
        programaAlteracao.setDataAlteracao(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.FORMULADO);

        try {
            programaService.incluirAlterar(programa, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(11)
    public void testIncluirSemQuantidadeKit() throws Throwable {

        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);

        List<ProgramaKit> listaProgramaKit = new ArrayList<ProgramaKit>();
        ProgramaKit kit = new ProgramaKit();
        kit.setKit(em.find(Kit.class, 1l));
        listaProgramaKit.add(kit);

        programa.setProgramaKits(listaProgramaKit);

        try {
            programaService.incluirAlterar(programa, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test(expected = BusinessException.class)
    @InSequence(12)
    public void testIncluirSemQuantidadeBem() throws Throwable {

        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);

        List<ProgramaBem> listaProgramaBem = new ArrayList<ProgramaBem>();
        ProgramaBem bem = new ProgramaBem();
        bem.setBem(em.find(Bem.class, 1l));
        bem.setQuantidade(0);
        listaProgramaBem.add(bem);

        programa.setProgramaBens(listaProgramaBem);

        try {
            programaService.incluirAlterar(programa, USUARIO_LOGADO);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test
    @InSequence(13)
    public void testIncluirSemNenhumaLista() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        Assert.assertNotNull(programa.getId());
    }

    @Test(expected = BusinessException.class)
    @InSequence(14)
    public void testExcluir() {
        Programa programa = new Programa();

        programa.setNomePrograma("nome programa");
        programa.setNomeFantasiaPrograma("nome fantasia programa");
        programa.setDescricaoPrograma("descricao programa");
        programa.setSubFuncao(em.find(SubFuncao.class, 31l));
        programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
        programa.setNumeroProcessoSEI("11111");
        programa.setPossuiLimitacaoGeografica(false);
        programa.setPossuiLimitacaoMunicipalEspecifica(false);
        programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        programa.setUsuarioCadastro(USUARIO_LOGADO);
        programa.setDataCadastro(LocalDateTime.now());
        programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
        programa.setAnoPrograma(2015);
        programa.setValorMaximoProposta(BigDecimal.ONE);

        programaService.incluirAlterar(programa, USUARIO_LOGADO);
        programaService.excluir(programa.getId());
        programaService.buscarPeloId(programa.getId());
    }

    @Test
    @InSequence(15)
    public void testBuscarPaginado() {

        for (int i = 0; i < 11; i++) {
            Programa programa = new Programa();

            programa.setNomePrograma("nome programa" + i);
            programa.setNomeFantasiaPrograma("nome fantasia programa" + i);
            programa.setDescricaoPrograma("descricao programa" + i);
            programa.setSubFuncao(em.find(SubFuncao.class, 31l));
            programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
            programa.setNumeroProcessoSEI("11111");
            programa.setPossuiLimitacaoGeografica(false);
            programa.setPossuiLimitacaoMunicipalEspecifica(false);
            programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
            programa.setUsuarioCadastro(USUARIO_LOGADO);
            programa.setDataCadastro(LocalDateTime.now());
            programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
            programa.setAnoPrograma(2015);
            programa.setValorMaximoProposta(BigDecimal.ONE);

            programaService.incluirAlterar(programa, USUARIO_LOGADO);

        }

        ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();

        List<Programa> lista = programaService.buscarPaginado(programaPesquisaDto, 1, 5, EnumOrder.ASC, "nomePrograma");
        Assert.assertEquals(5, lista.size());
    }

    @Test
    @InSequence(16)
    public void testContarPaginado() {

        for (int i = 0; i < 11; i++) {
            Programa programa = new Programa();

            programa.setNomePrograma("nome programa" + i);
            programa.setNomeFantasiaPrograma("nome fantasia programa" + i);
            programa.setDescricaoPrograma("descricao programa" + i);
            programa.setSubFuncao(em.find(SubFuncao.class, 31l));
            programa.setUnidadeExecutora(em.find(UnidadeExecutora.class, 1l));
            programa.setNumeroProcessoSEI("11111");
            programa.setPossuiLimitacaoGeografica(false);
            programa.setPossuiLimitacaoMunicipalEspecifica(false);
            programa.setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
            programa.setUsuarioCadastro(USUARIO_LOGADO);
            programa.setDataCadastro(LocalDateTime.now());
            programa.setStatusPrograma(EnumStatusPrograma.EM_ELABORACAO);
            programa.setAnoPrograma(2015);
            programa.setValorMaximoProposta(BigDecimal.ONE);

            programaService.incluirAlterar(programa, USUARIO_LOGADO);

        }

        ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();

        Long retorno = programaService.contarPaginado(programaPesquisaDto);
        Assert.assertTrue(retorno == 11);
    }

    @Before
    public void createData() throws Exception {

        // tx.begin();
        //
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_ABM_ANEXO_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_ACO_ACAO_ORCAMENTARIA").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_BEM_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_EPA_EMENDA_PARLAMENTAR").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_KIB_KIT_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_KIT_KIT").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_PAN_PROGRAMA_ANEXO").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_PRG_PROGRAMA").executeUpdate();
        // em.createNativeQuery("drop sequence SIDE.SEQ_TB_TBM_TAG_BEM").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_pca_programa_criterio_acompanhamento").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_pce_programa_criterio_elegibilidade").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_ppm_programa_potencial_beneficiario_municipio").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_ppu_programa_potencial_beneficiario_uf").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_prb_programa_bem").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_prf_programa_recurso_financeiro").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_prk_programa_kit").executeUpdate();
        // em.createNativeQuery("drop sequence side.seq_tb_rfe_recurso_financeiro_emenda_parlamentar").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_ABM_ANEXO_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_ACO_ACAO_ORCAMENTARIA start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_BEM_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_BEP_BENEFICIARIO_EMENDA_PARLAMENTAR start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_EPA_EMENDA_PARLAMENTAR start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_KIB_KIT_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_KIT_KIT start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_PAN_PROGRAMA_ANEXO start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_PRG_PROGRAMA start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence SIDE.SEQ_TB_TBM_TAG_BEM start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_pca_programa_criterio_acompanhamento start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_pce_programa_criterio_elegibilidade start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_ppm_programa_potencial_beneficiario_municipio start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_ppu_programa_potencial_beneficiario_uf start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_prb_programa_bem start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_prf_programa_recurso_financeiro start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_prk_programa_kit start with 1 increment by 1").executeUpdate();
        // em.createNativeQuery("create sequence side.seq_tb_rfe_recurso_financeiro_emenda_parlamentar start with 1 increment by 1").executeUpdate();
        //
        // em.createQuery("delete from PotencialBeneficiarioMunicipio").executeUpdate();
        // em.createQuery("delete from ProgramaPotencialBeneficiarioUf").executeUpdate();
        // em.createQuery("delete from Programa").executeUpdate();
        // em.createQuery("delete from KitBem").executeUpdate();
        // em.createQuery("delete from Kit").executeUpdate();
        // em.createQuery("delete from Bem").executeUpdate();
        // em.createQuery("delete from SubElemento").executeUpdate();
        // em.createQuery("delete from Elemento").executeUpdate();
        // em.createQuery("delete from BeneficiarioEmendaParlamentar").executeUpdate();
        // em.createQuery("delete from EmendaParlamentar").executeUpdate();
        // em.createQuery("delete from AcaoOrcamentaria").executeUpdate();
        // em.createQuery("delete from PartidoPolitico").executeUpdate();
        // em.createQuery("delete from UnidadeExecutora").executeUpdate();
        // em.createQuery("delete from Orgao").executeUpdate();
        // em.createQuery("delete from SubFuncao").executeUpdate();
        // em.createQuery("delete from Funcao").executeUpdate();
        // em.createQuery("delete from Municipio").executeUpdate();
        // em.createQuery("delete from Uf").executeUpdate();
        // tx.commit();
        //
        // String line = "";
        //
        // /* Uf */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/ufs.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Uf uf = new Uf();
        // uf.setId(new Long(values[0]));
        // uf.setNomeUf(values[1]);
        // uf.setSiglaUf(values[2]);
        // genericPersister.persist(uf);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Municipio */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/municipios.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Municipio municipio = new Municipio();
        // municipio.setId(new Long(values[0]));
        // municipio.setUf(genericPersister.findById(Uf.class, new
        // Long(values[1])));
        // municipio.setNomeMunicipio(values[2]);
        // genericPersister.persist(municipio);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Funcao */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/funcao.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Funcao f = new Funcao();
        // f.setId(new Long(values[0]));
        // f.setNomeFuncao(values[1]);
        // genericPersister.persist(f);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* SubFuncao */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/subfuncao.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // SubFuncao sf = new SubFuncao();
        // sf.setId(new Long(values[0]));
        // sf.setNomeSubFuncao(values[1]);
        // sf.setFuncao(genericPersister.findById(Funcao.class, new
        // Long(values[2])));
        // genericPersister.persist(sf);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Orgao */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/orgao.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Orgao o = new Orgao();
        // o.setId(new Long(values[0]));
        // o.setNomeOrgao(values[1]);
        // o.setSiglaOrgao(values[2]);
        // genericPersister.persist(o);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Unidade Executora */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/unidadeexecutora.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // UnidadeExecutora uex = new UnidadeExecutora();
        // uex.setId(new Long(values[0]));
        // uex.setNomeUnidadeExecutora(values[1]);
        // uex.setOrgao(genericPersister.findById(Orgao.class, new
        // Long(values[2])));
        // genericPersister.persist(uex);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* partidos */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/partidos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // PartidoPolitico pp = new PartidoPolitico();
        // pp.setId(new Long(values[0]));
        // pp.setNomePartido(values[1]);
        // pp.setSiglaPartido(values[2]);
        // genericPersister.persist(pp);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* acaoorcamentaria */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/acaoorcamentaria.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // AcaoOrcamentaria ao = new AcaoOrcamentaria();
        // ao.setNumeroProgramaPPA(values[1]);
        // ao.setNomeProgramaPPA(values[2]);
        // ao.setNumeroAcaoOrcamentaria(values[3]);
        // ao.setNomeAcaoOrcamentaria(values[4]);
        // ao.setValorPrevisto(new BigDecimal(values[5]));
        // ao.setUsuarioCadastro(USUARIO_LOGADO);
        // ao.setDataCadastro(LocalDateTime.now());
        // ao.setUsuarioAlteracao(USUARIO_LOGADO);
        // ao.setDataAlteracao(LocalDateTime.now());
        // ao.setAnoAcaoOrcamentaria(2015);
        // genericPersister.persist(ao);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* emendaParlamentar */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/emendaparlamentar.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // EmendaParlamentar ep = new EmendaParlamentar();
        // ep.setAcaoOrcamentaria(genericPersister.findById(AcaoOrcamentaria.class,
        // new Long(values[1])));
        // ep.setNumeroEmendaParlamantar(values[2]);
        // ep.setNomeEmendaParlamentar(values[3]);
        // ep.setPartidoPolitico(genericPersister.findById(PartidoPolitico.class,
        // new Long(values[4])));
        // ep.setUf(genericPersister.findById(Uf.class, new Long(values[5])));
        // ep.setNomeParlamentar(values[6]);
        // ep.setNomeCargoParlamentar(values[7]);
        // ep.setPossuiLiberacao(false);
        // ep.setValorPrevisto(new BigDecimal(values[9]));
        // ep.setTipoEmenda(EnumTipoEmenda.INDIVIDUAL);
        // genericPersister.persist(ep);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* beneficiarioEmendaParlamentar */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/beneficiarioemenda.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // BeneficiarioEmendaParlamentar bep = new
        // BeneficiarioEmendaParlamentar();
        // bep.setEmendaParlamentar(genericPersister.findById(EmendaParlamentar.class,
        // new Long(values[1])));
        // bep.setNumeroCnpjBeneficiario(values[2]);
        // bep.setNomeBeneficiario(values[3]);
        // genericPersister.persist(bep);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* elementos */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/elementos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // Elemento e = new Elemento();
        // e.setId(new Long(values[0]));
        // e.setNomeElemento(values[1]);
        // genericPersister.persist(e);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* subelementos */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/subelementos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // SubElemento se = new SubElemento();
        // se.setId(new Long(values[0]));
        // se.setNomeSubElemento(values[1]);
        // Elemento e = new Elemento();
        // e = genericPersister.findById(Elemento.class, new Long(values[2]));
        // se.setElemento(e);
        // genericPersister.persist(se);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Bem */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/bem.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // Bem b = new Bem();
        // b.setNomeBem(values[1]);
        // b.setDescricaoBem(values[2]);
        // b.setValorEstimadoBem(new BigDecimal(values[3]));
        // b.setSubElemento(genericPersister.findById(SubElemento.class, new
        // Long(values[4])));
        // genericPersister.persist(b);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* Kit */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/kit.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // Kit k = new Kit();
        // k.setNomeKit(values[1]);
        // k.setDescricaoKit(values[2]);
        // genericPersister.persist(k);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
        //
        // /* KitBem */
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/kitbem.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        // KitBem kib = new KitBem();
        // kib.setQuantidade(Integer.parseInt(values[1]));
        // kib.setKit(genericPersister.findById(Kit.class, new
        // Long(values[2])));
        // kib.setBem(genericPersister.findById(Bem.class, new
        // Long(values[3])));
        // genericPersister.persist(kib);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }

    }

}
