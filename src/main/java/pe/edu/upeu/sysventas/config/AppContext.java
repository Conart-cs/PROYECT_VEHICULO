package pe.edu.upeu.sysventas.config;

import pe.edu.upeu.sysventas.service.IMarcaService;
import pe.edu.upeu.sysventas.service.IPropietarioService;
import pe.edu.upeu.sysventas.service.IVehiculoService;
import pe.edu.upeu.sysventas.controller.*;
import pe.edu.upeu.sysventas.repository.*;
import pe.edu.upeu.sysventas.service.*;
import pe.edu.upeu.sysventas.service.impl.*;
import pe.edu.upeu.sysventas.controller.*;
import java.util.HashMap;
import java.util.Map;

import pe.edu.upeu.sysventas.controller.InfraccionController;
import pe.edu.upeu.sysventas.controller.MarcaController;
import pe.edu.upeu.sysventas.controller.PropietarioController;
import pe.edu.upeu.sysventas.controller.VehiculoController;
import pe.edu.upeu.sysventas.repository.InfraccionRepository;
import pe.edu.upeu.sysventas.repository.MarcaRepository;
import pe.edu.upeu.sysventas.repository.PropietarioRepository;
import pe.edu.upeu.sysventas.repository.VehiculoRepository;
import pe.edu.upeu.sysventas.service.IInfraccionService;
import pe.edu.upeu.sysventas.service.impl.InfraccionServiceImp;
import pe.edu.upeu.sysventas.service.impl.MarcaServiceImp;
import pe.edu.upeu.sysventas.service.impl.PropietarioServiceImp;
import pe.edu.upeu.sysventas.service.impl.VehiculoServiceImp;

public class AppContext {

    // Singleton: una sola instancia en toda la app
    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // El "directorio": Clase → Objeto
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    // Constructor privado: aquí se arma toda la aplicación
    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    // CAPA 1 — REPOSITORIOS
    // Cada repositorio sabe hablar con una tabla de la base de datos.
    // No reciben dependencias: solo necesitan la conexión (DatabaseConfig).
    private void registrarRepositorios() {
        //registrar(CategoriaRepository.class, new CategoriaRepository());


        registrar(CategoriaRepository.class, new CategoriaRepository());
        registrar(MarcaRepository.class, new MarcaRepository());
        registrar(UnidadMedidaRepository.class, new UnidadMedidaRepository());
        registrar(ProductoRepository.class, new ProductoRepository());
        registrar(VehiculoRepository.class, new VehiculoRepository());
        registrar(PropietarioRepository.class, new PropietarioRepository());
        registrar(InfraccionRepository.class, new InfraccionRepository());

    }

    // CAPA 2 — SERVICIOS
    // Cada servicio recibe su repositorio por constructor.
    // Usamos getBean() para buscarlo en el directorio: no creamos nada nuevo.
    private void registrarServicios() {

        registrar(ICategoriaService.class, new CategoriaServiceImp(getBean(CategoriaRepository.class)));
        registrar(IMarcaService.class, new MarcaServiceImp(getBean(MarcaRepository.class)));
        registrar(IProductoService.class, new ProductoServiceImp(getBean(ProductoRepository.class)));
        registrar(IUnidadMedidaService.class, new UnidadMedidaServiceImp(getBean(UnidadMedidaRepository.class)));
        registrar(IVehiculoService.class, new VehiculoServiceImp(getBean(VehiculoRepository.class)));
        registrar(IPropietarioService.class, new PropietarioServiceImp(getBean(PropietarioRepository.class)));
        registrar(IInfraccionService.class, new InfraccionServiceImp(getBean(InfraccionRepository.class)));

    }

    // CAPA 3 — CONTROLADORES JavaFX
    // Cada controlador recibe los servicios que necesita por constructor.
    // El FXMLLoader los busca aquí a través de setControllerFactory().
    private void registrarControladores() {
        //registrar(LoginController.class, new LoginController(getBean(IUsuarioService.class)));
        registrar(ProductoController.class,
                new ProductoController(
                        getBean(IMarcaService.class),
                        getBean(ICategoriaService.class),
                        getBean(IProductoService.class),
                        getBean(IUnidadMedidaService.class)));
        registrar(VehiculoController.class,
                new VehiculoController(
                        getBean(IVehiculoService.class),
                        getBean(IMarcaService.class),
                        getBean(IPropietarioService.class)));
        registrar(PropietarioController.class,
                new PropietarioController(
                        getBean(IPropietarioService.class)));
        registrar(MarcaController.class,
                new MarcaController(
                        getBean(IMarcaService.class)));
        registrar(
                InfraccionController.class,
                new InfraccionController(
                        getBean(IInfraccionService.class),
                        getBean(IVehiculoService.class)));
        registrar(
                MainController.class,
                new MainController());
    }

    // API del contenedor — estos dos métodos son todo lo que hace la DI

    /**
     * Guarda un objeto en el directorio, indexado por su tipo o interfaz.
     */
    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    /**
     * Busca y devuelve un objeto por su tipo o interfaz.
     * Equivale a lo que hace Spring/Micronaut con @Inject automáticamente.
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            // Búsqueda por compatibilidad: sirve cuando se pide una interfaz
            // y el objeto guardado es su implementación concreta.
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName() +
                                    "\n→ ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}