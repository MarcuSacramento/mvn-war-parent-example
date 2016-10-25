package br.gov.mj.side.test.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.web.dao.BemDAO;
import br.gov.mj.side.web.dao.KitDAO;
import br.gov.mj.side.web.dao.ProgramaDAO;
import br.gov.mj.side.web.dao.PublicizacaoDAO;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.KitService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;

@RunWith(Arquillian.class)
public class KitServiceTest {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private KitService kitService;

    @Inject
    private BemService bemService;

    @Inject
    private UserTransaction tx;

    @PersistenceContext
    private EntityManager em;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default").resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946","org.jadira.usertype:usertype.extended:3.2.0.GA").withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.side.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.seg.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml").addAsResource("elementos.csv", "elementos.csv").addAsResource("init.txt", "init.txt").addAsResource("subelementos.csv", "subelementos.csv")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml").addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(KitDAO.class).addClass(BemDAO.class).addClass(PublicizacaoDAO.class).addClass(KitService.class)
                .addClass(BemService.class).addClass(ProgramaService.class).addClass(ProgramaDAO.class).addClass(ProgramaPesquisaDto.class).addClass(Constants.class).addAsLibraries(jars);
        return file;
    }

    /*
     * TESTA O METODO Kit incluirAlterar(Kit kit)
     */

    // Testa se esta persistindo um kit simples
    @Test
    @InSequence(1)
    public void testPersist() {
        Kit kit = new Kit();
        kit.setNomeKit("Kit de Teste Unitário");
        kit.setDescricaoKit("Descrição do kit de teste unitário");
        Kit kitRetorno = kitService.incluirAlterar(kit);

        Assert.assertTrue((!kitRetorno.getId().equals(null) && (kitRetorno.getNomeKit().equals("Kit de Teste Unitário"))));
    }

    // Testa se está disparando IllegalArgumentException ao ser passado um
    // parametro null
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2)
    public void testeIncluirAlterarKitNull() throws Throwable {
        try {
            kitService.incluirAlterar(null);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    // Testa se um BusinessException é lançado ao tentar gravar um kit com um
    // nome já cadastrado
    @Test(expected = BusinessException.class)
    @InSequence(3)
    public void testIncluirAlterarKitJaCadastrado() {
        try {
            Kit kit = new Kit();
            kit.setNomeKit("Testando Nome Kit");
            kit.setDescricaoKit("Descrição do kit");
            kitService.incluirAlterar(kit);

            Kit kit2 = new Kit();
            kit2.setNomeKit("Testando Nome Kit");
            kit2.setDescricaoKit("Descrição do kit");
            kitService.incluirAlterar(kit2);
        } catch (Exception ex) {
            ex.getCause();
            throw ex;
        }
    }

    // Irá testar se o metodo IncluirAlterar esta editando um kit já salvo
    @Test
    @InSequence(4)
    public void testarEdicaoDoKit() {
        // Salva o kit
        Kit kit = new Kit();
        kit.setNomeKit("Teste_Unitário_Editar");
        kit.setDescricaoKit("Descrição_teste_unitário");
        Kit kitRetornado = kitService.incluirAlterar(kit);

        // A partir do kit salvo acima, faço uma busca pelo id dele, altero e
        // salvo novamente
        kitRetornado.setNomeKit("Teste_Unitário_Editar_Alterado");
        kitRetornado.setDescricaoKit("Descrição_teste_unitário_alterado");
        kitService.incluirAlterar(kitRetornado);

        // Realizo uma busca pelo id do kit alterado e verifico se foi feita a
        // edição
        Long idKitAlterado = kitRetornado.getId();
        Kit KitSalvo = kitService.buscarPeloId(idKitAlterado);
        Assert.assertTrue(KitSalvo.getNomeKit().equals("Teste_Unitário_Editar_Alterado"));
        Assert.assertTrue(KitSalvo.getDescricaoKit().equals("Descrição_teste_unitário_alterado"));
    }

    // Realiza um persist completo buscando-se um Bem já cadastrado e setando-o
    // a um kit.
    @Test
    @InSequence(5)
    public void testarSalvarKitComListaDeBens() {
        try {
            // Cria uma lista de SubElementos e Elementos
            createData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Inclui um Bem com o elemento='339014 - Diárias' e com o
        // subElemento='33901414 - DIARIAS NO PAIS';
        Bem bem = new Bem();
        bem.setNomeBem("Pneeu");
        bem.setDescricaoBem("Pneu teste");
        bem.setSubElemento(genericPersister.findById(SubElemento.class, 33901414l));
        bem.setValorEstimadoBem(new BigDecimal("10000.00"));
        bemService.incluirAlterar(bem);

        // Adiciona o Bem a 1 kit
        KitBem kit1 = new KitBem();
        kit1.setBem(bem);
        kit1.setQuantidade(10);

        // Adiciona-se o kit a uma lista de kits.
        List<KitBem> listaKits = new ArrayList<KitBem>();
        listaKits.add(kit1);

        // Adiciona-se a lista de Bens a 1 kit e é realizado o processo de
        // persistencia
        Kit kit = new Kit();
        kit.setNomeKit("Kit de teste Completo");
        kit.setDescricaoKit("Descrição do kit completo");
        kit.setKitsBens(listaKits);
        Kit kitSalvo = kitService.incluirAlterar(kit);

        // Verifica-se se o 'kitSalvo' foi salvo verificando se o id não é igual
        // a 'null'.
        Assert.assertTrue((!kitSalvo.getId().equals(null)) && (kitSalvo.getNomeKit().equals("Kit de teste Completo")));
    }

    /*
     * TESTA O METODO List<Kit> pesquisar(Kit kit, Bem bem, Elemento elemento)
     */

    // O metodo irá retornar uma lista de todos os elementos passados como
    // parametro
    // Caso seja tudo nulo irá voltar todos os kits independente do elemento,
    // bem ou subelemento
    @Test
    @InSequence(6)
    public void testarPesquisar() {
        // Cadastra 8 kits
        cadastrarKits();

        List<Kit> listaDeKits = kitService.pesquisar(null, null, null);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() > 8);
    }

    // Irá retornar um lista buscando-se por 1 kit especifico
    @Test
    @InSequence(7)
    public void testarPesquisarKit() {
        Kit kit = kitService.buscarPeloId(1L);

        List<Kit> listaDeKits = kitService.pesquisar(kit, null, null);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() == 1);
    }

    // Irá retornar um lista buscando-se por 1 Bem especifico
    @Test
    @InSequence(8)
    public void testarPesquisarBem() {

        Bem bem = bemService.buscarPeloId(1L);

        List<Kit> listaDeKits = kitService.pesquisar(null, bem, null);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() == 1);
    }

    // Irá retornar um lista buscando-se por 1 Elemento especifico
    @Test
    @InSequence(9)
    public void testarPesquisarElemento() {
        Elemento elemento = genericPersister.findById(Elemento.class, 339014l);

        // Existe um bem que foi salvo com este elemento no teste '5'
        List<Kit> listaDeKits = kitService.pesquisar(null, null, elemento);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() == 1);
    }

    /*
     * TESTA O METODO buscarPaginado(Kit kit, Bem bem, Elemento elemento, int
     * first, int size)
     */

    @Test
    @InSequence(10)
    public void testeBuscarPaginado() {
        List<Kit> listaDeKits = kitService.buscarPaginado(null, null, null, 0, 4, "", 0);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }
        Assert.assertTrue(listaDeKits.size() == 4);
    }

    // Irá retornar um lista buscando-se por 1 Kit especifico
    @Test
    @InSequence(11)
    public void testeBuscarPaginadoKit() {
        Kit kit = kitService.buscarPeloId(1L);

        List<Kit> listaDeKits = kitService.buscarPaginado(kit, null, null, 0, 4, "", 0);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() == 1);
    }

    // Irá retornar um lista buscando-se por 1 Bem especifico
    @Test
    @InSequence(12)
    public void testeBuscarPaginadoBem() {
        Bem bem = bemService.buscarPeloId(1L);

        List<Kit> listaDeKits = kitService.buscarPaginado(null, bem, null, 0, 4, "", 0);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() == 1);
    }

    // Irá retornar um lista buscando-se por 1 Elemento especifico
    @Test
    @InSequence(13)
    public void testeBuscarPaginadoElemento() {
        Elemento elemento = genericPersister.findById(Elemento.class, 339014l);

        // Existe um bem que foi salvo com este elemento no teste '5'
        List<Kit> listaDeKits = kitService.buscarPaginado(null, null, elemento, 0, 4, "", 0);

        if (listaDeKits.isEmpty()) {
            Assert.fail();
        }

        Assert.assertTrue(listaDeKits.size() == 1);
    }

    /*
     * TESTA O METODO Kit buscarpeloId(Long id)
     */

    // Realiza-se uma busca por um kit passando-se um id salvo previamente
    @Test
    @InSequence(14)
    public void testarBuscaKitPeloId() throws Throwable {
        // Salva o kit
        Kit kit = new Kit();
        kit.setNomeKit("Teste_Unitário");
        kit.setDescricaoKit("Descrição_teste_Busca_ID");
        Kit kitSalvo = kitService.incluirAlterar(kit);

        // Faz uma busca a partir do id do kit salvo anteriormente
        Kit kitRetornado = kitService.buscarPeloId(kitSalvo.getId());

        // Verifica-se se a descrição do objeto retornado é o mesmo do primeiro
        // objeto usado para persistir.
        Assert.assertTrue(kitRetornado.getDescricaoKit().equals("Descrição_teste_Busca_ID"));
    }

    // Realiza-se uma busca por um kit passando-se um id=null
    @Test(expected = EJBTransactionRolledbackException.class)
    @InSequence(15)
    public void testarBuscaKitNull() throws Throwable {
        try {
            kitService.buscarPeloId(null);
        } catch (EJBException ex) {
            throw ex;
        }
    }

    /*
     * TESTA O METODO List<Kit> buscar(Kit kit)
     */

    // A partir de todos os kits salvos verifica-se se o numero retornado é
    // superior a 8
    @Test
    @InSequence(16)
    public void testarBuscaListaDeKits() {
        // Salva os kits

        List<Kit> listaKits = kitService.buscar(null);
        Assert.assertTrue(listaKits.size() > 8);
    }

    // Irá salvar 1 kit para teste e verificar se será devolvida uma lista com
    // apenas o kit informado.
    @Test
    @InSequence(17)
    public void testarBuscaPorKitJaSalvo() {
        Kit kit = new Kit();
        kit.setNomeKit("Kit Teste lista");
        kit.setDescricaoKit("Descrição_Kit 1");
        Kit kitSalvo = kitService.incluirAlterar(kit);

        List<Kit> listaKits = kitService.buscar(kitSalvo);
        Assert.assertTrue((listaKits.size() == 1) && (listaKits.get(0).getId() == kitSalvo.getId()));
    }

    /*
     * TESTA O METODO void excluir(Long id)
     */

    // Ao tentar excluir um Kit passando id='null' um IllegalArgumentException
    // será lançado.
    @Test(expected = IllegalArgumentException.class)
    @InSequence(18)
    public void excluirPassandoIdNull() throws Throwable {
        try {
            kitService.excluir(null);
        } catch (Exception ex) {
            Throwable th = ex.getCause();
            throw th;
        }
    }

    // Ao tentar excluir um Kit cujo id não existe um 'BusinessException' é
    // lançado
    @Test(expected = BusinessException.class)
    @InSequence(19)
    public void excluirPassandoId() throws Throwable {
        try {
            kitService.excluir(100000000000L);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /*
     * METODOS AUXILIARES PARA O TESTE
     */

    public void createData() throws Exception {

        tx.begin();
        em.createQuery("delete from Bem").executeUpdate();
        em.createQuery("delete from SubElemento").executeUpdate();
        em.createQuery("delete from Elemento").executeUpdate();
        tx.commit();

        String line = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/elementos.csv"), "UTF-8"))) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                Elemento e = new Elemento();
                e.setId(new Long(values[0]));
                e.setNomeElemento(values[1]);
                genericPersister.persist(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/subelementos.csv"), "UTF-8"))) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                SubElemento se = new SubElemento();
                se.setId(new Long(values[0]));
                se.setNomeSubElemento(values[1]);
                Elemento e = new Elemento();
                e = genericPersister.findById(Elemento.class, new Long(values[2]));
                se.setElemento(e);
                genericPersister.persist(se);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cadastrarKits() {
        // Salva os kits
        Kit kit = new Kit();
        kit.setNomeKit("Kit 1 pesq");
        kit.setDescricaoKit("Descrição_Kit 1");

        Kit kit2 = new Kit();
        kit2.setNomeKit("Kit 2 pesq");
        kit2.setDescricaoKit("Descrição_Kit 2");

        Kit kit3 = new Kit();
        kit3.setNomeKit("Kit 3 pesq");
        kit3.setDescricaoKit("Descrição_Kit 3");

        Kit kit4 = new Kit();
        kit4.setNomeKit("Kit 4 pesq");
        kit4.setDescricaoKit("Descrição_Kit 4");

        Kit kit5 = new Kit();
        kit5.setNomeKit("Kit 5 pesq");
        kit5.setDescricaoKit("Descrição_Kit 5");

        Kit kit6 = new Kit();
        kit6.setNomeKit("Kit 6 pesq");
        kit6.setDescricaoKit("Descrição_Kit 6");

        Kit kit7 = new Kit();
        kit7.setNomeKit("Kit 7 pesq");
        kit7.setDescricaoKit("Descrição_Kit 7");

        Kit kit8 = new Kit();
        kit8.setNomeKit("Kit 8 pesq");
        kit8.setDescricaoKit("Descrição_Kit 8");

        kitService.incluirAlterar(kit);
        kitService.incluirAlterar(kit2);
        kitService.incluirAlterar(kit3);
        kitService.incluirAlterar(kit4);
        kitService.incluirAlterar(kit5);
        kitService.incluirAlterar(kit6);
        kitService.incluirAlterar(kit7);
        kitService.incluirAlterar(kit8);
    }
}
