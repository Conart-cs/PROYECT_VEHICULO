package pe.edu.upeu.sysventas.controller;
import java.util.LinkedHashMap;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.config.AppContext;
import pe.edu.upeu.sysventas.model.Marca;
import pe.edu.upeu.sysventas.model.Vehiculo;
import pe.edu.upeu.sysventas.service.IMarcaService;
import pe.edu.upeu.sysventas.service.IPropietarioService;
import pe.edu.upeu.sysventas.service.IVehiculoService;
import javafx.stage.Stage;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@RequiredArgsConstructor
public class VehiculoController {

    private final IVehiculoService vs;
    private final IMarcaService ms;
    private final IPropietarioService ps;

    @FXML
    private AnchorPane miContenedor;

    @FXML
    private TextField txtPlaca;

    @FXML
    private ComboBox<String> cmbMarca;

    @FXML
    private ComboBox<String> cmbModelo;

    @FXML
    private TextField txtAnio;

    @FXML
    private ComboBox<String> cmbColor;

    @FXML
    private ComboBox<String> cmbPropietario;

    @FXML
    private TextField txtFiltroDato;

    @FXML
    private TableView<Vehiculo> tableView;

    @FXML
    private Label lbnMsg;

    @FXML
    private Label lblTotalVehiculos;

    @FXML
    private Label lblTotalMarcas;

    @FXML
    private Label lblAnioReciente;

    @FXML
    private Label lblTotalPropietarios;

    @FXML
    private Label lblPlacaSeleccionada;

    @FXML
    private Label lblVehiculoSeleccionado;

    @FXML
    private Label lblDetalleVehiculo;

    @FXML
    private Label lblPropietarioSeleccionado;

    private ObservableList<Vehiculo> listarVehiculo =
            FXCollections.observableArrayList();

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    private Vehiculo formulario;

    private Long idVehiculoCE = 0L;

    @FXML
    public void volverInicio(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/view/main_principal.fxml"
                    )
            );

            loader.setControllerFactory(
                    AppContext.getInstance()::getBean
            );

            Parent root = loader.load();

            Node source = (Node) event.getSource();

            Stage stage =
                    (Stage) source.getScene().getWindow();

            stage.setTitle("Padrón Vehicular");

            stage.getScene().setRoot(root);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {

        cargarMarcas();
        cargarPropietarios();

        cmbMarca.setOnAction(event -> {
            cargarModelos();
            cargarColores();
        });

        listar();

        txtFiltroDato.textProperty().addListener(
                (observable, oldValue, newValue) -> filtrarVehiculos()
        );

        tableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    mostrarVehiculoSeleccionado(newValue);
                });
    }


    // =========================================================
    // CARGAR MARCAS
    // =========================================================

    private void cargarMarcas() {

        List<String> marcas = ms.findAll()
                .stream()
                .map(Marca::getNombre)
                .sorted()
                .collect(Collectors.toList());

        cmbMarca.setItems(
                FXCollections.observableArrayList(marcas)
        );
    }


    // =========================================================
    // CARGAR MODELOS SEGÚN MARCA
    // =========================================================

    private void cargarModelos() {

        cmbModelo.getItems().clear();

        String marcaSeleccionada = cmbMarca.getValue();

        if (marcaSeleccionada == null || marcaSeleccionada.isBlank()) {
            return;
        }

        for (Marca marca : ms.findAll()) {

            if (marca.getNombre().equalsIgnoreCase(marcaSeleccionada)) {

                if (marca.getModelos() != null) {

                    cmbModelo.setItems(
                            FXCollections.observableArrayList(
                                    marca.getModelos()
                            )
                    );
                }

                break;
            }
        }
    }


    // =========================================================
    // CARGAR COLORES SEGÚN MARCA
    // =========================================================

    private void cargarColores() {

        cmbColor.getItems().clear();

        String marcaSeleccionada = cmbMarca.getValue();

        if (marcaSeleccionada == null || marcaSeleccionada.isBlank()) {
            return;
        }

        for (Marca marca : ms.findAll()) {

            if (marca.getNombre().equalsIgnoreCase(marcaSeleccionada)) {

                if (marca.getColores() != null) {

                    cmbColor.setItems(
                            FXCollections.observableArrayList(
                                    marca.getColores()
                            )
                    );
                }

                break;
            }
        }
    }


    // =========================================================
    // CARGAR PROPIETARIOS
    // =========================================================

    private void cargarPropietarios() {

        List<String> propietarios = ps.findAll()
                .stream()
                .map(propietario ->
                        propietario.getDni() + " - "
                                + propietario.getNombre() + " "
                                + propietario.getApellido())
                .sorted()
                .collect(Collectors.toList());

        cmbPropietario.setItems(
                FXCollections.observableArrayList(propietarios)
        );
    }


    // =========================================================
    // LISTAR VEHÍCULOS
    // =========================================================
    private void listar() {

        listarVehiculo.setAll(vs.findAll());

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();

        columns.put("Placa", new ColumnInfo("placa", 100.0));
        columns.put("Marca", new ColumnInfo("marca", 100.0));
        columns.put("Modelo", new ColumnInfo("modelo", 120.0));
        columns.put("Año", new ColumnInfo("anio", 80.0));
        columns.put("Color", new ColumnInfo("color", 100.0));
        columns.put("Propietario", new ColumnInfo("propietario", 180.0));

        TableViewHelper<Vehiculo> tableHelper =
                new TableViewHelper<>();

        tableHelper.addColumnsInOrderWithSize(
                tableView,
                columns,
                this::editForm,
                this::eliminar
        );

        tableView.setItems(listarVehiculo);

        actualizarEstadisticas();
    }

    // =========================================================
    // VALIDAR FORMULARIO
    // =========================================================

    @FXML
    private void validarFormulario() {

        lbnMsg.setText("");

        try {

            String placa = txtPlaca.getText() == null
                    ? ""
                    : txtPlaca.getText().trim().toUpperCase();

            String marca = cmbMarca.getValue();
            String modelo = cmbModelo.getValue();
            String color = cmbColor.getValue();
            String propietario = cmbPropietario.getValue();

            Integer anio = null;

            if (txtAnio.getText() != null &&
                    !txtAnio.getText().trim().isEmpty()) {

                try {
                    anio = Integer.parseInt(
                            txtAnio.getText().trim()
                    );

                } catch (NumberFormatException e) {
                    lbnMsg.setText("El año debe ser un número.");
                    return;
                }
            }


            // Validar placa única solamente al registrar
            if (idVehiculoCE == 0 &&
                    vs.Existe_Placa(placa)) {

                lbnMsg.setText(
                        "La placa ya está registrada."
                );

                return;
            }


            formulario = Vehiculo.builder()
                    .idVehiculo(idVehiculoCE == 0
                            ? null
                            : idVehiculoCE)
                    .placa(placa)
                    .marca(marca)
                    .modelo(modelo)
                    .anio(anio)
                    .color(color)
                    .propietario(propietario)
                    .build();


            Set<ConstraintViolation<Vehiculo>> violations =
                    validator.validate(formulario);

            if (!violations.isEmpty()) {

                String mensaje = violations.stream()
                        .sorted(Comparator.comparing(
                                v -> v.getPropertyPath().toString()
                        ))
                        .map(ConstraintViolation::getMessage)
                        .findFirst()
                        .orElse("Datos inválidos.");

                lbnMsg.setText(mensaje);
                return;
            }


            procesarFormulario();

        } catch (Exception e) {

            lbnMsg.setText(
                    "Ocurrió un error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // GUARDAR / ACTUALIZAR
    // =========================================================
    private void procesarFormulario() {

        Stage stage = (Stage) miContenedor.getScene().getWindow();

        if (idVehiculoCE == 0) {

            vs.save(formulario);

            Toast.showToast(
                    stage,
                    "Vehículo registrado correctamente.",
                    2000,
                    stage.getX() + stage.getWidth() - 300,
                    stage.getY() + 80
            );

        } else {

            vs.update(idVehiculoCE, formulario);

            Toast.showToast(
                    stage,
                    "Vehículo actualizado correctamente.",
                    2000,
                    stage.getX() + stage.getWidth() - 300,
                    stage.getY() + 80
            );
        }

        listar();
        clearForm();
    }
    // =========================================================
    // EDITAR
    // =========================================================

    private void editForm(Vehiculo vehiculo) {

        formulario = vehiculo;

        idVehiculoCE = vehiculo.getIdVehiculo();

        txtPlaca.setText(vehiculo.getPlaca());
        txtPlaca.setDisable(true);

        cmbMarca.setValue(vehiculo.getMarca());

        cargarModelos();
        cargarColores();

        cmbModelo.setValue(vehiculo.getModelo());
        cmbColor.setValue(vehiculo.getColor());

        txtAnio.setText(
                vehiculo.getAnio() == null
                        ? ""
                        : String.valueOf(vehiculo.getAnio())
        );

        cmbPropietario.setValue(
                vehiculo.getPropietario()
        );

        lbnMsg.setText("");
    }


    // =========================================================
    // ELIMINAR
    // =========================================================
    private void eliminar(Vehiculo vehiculo) {

        vs.delete(vehiculo.getIdVehiculo());

        Stage stage = (Stage) miContenedor.getScene().getWindow();

        Toast.showToast(
                stage,
                "Se eliminó correctamente!!",
                2000,
                stage.getX() + stage.getWidth() - 300,
                stage.getY() + 80
        );

        listar();
        clearForm();
    }
    // =========================================================
    // FILTRAR
    // =========================================================

    private void filtrarVehiculos() {

        String filtro = txtFiltroDato.getText();

        if (filtro == null) {
            filtro = "";
        }

        filtro = filtro.trim().toLowerCase();

        String finalFiltro = filtro;

        List<Vehiculo> filtrados = vs.findAll()
                .stream()
                .filter(v ->
                        v.getPlaca() != null &&
                                v.getPlaca()
                                        .toLowerCase()
                                        .contains(finalFiltro)

                                ||

                                v.getMarca() != null &&
                                        v.getMarca()
                                                .toLowerCase()
                                                .contains(finalFiltro)

                                ||

                                v.getModelo() != null &&
                                        v.getModelo()
                                                .toLowerCase()
                                                .contains(finalFiltro)

                                ||

                                v.getColor() != null &&
                                        v.getColor()
                                                .toLowerCase()
                                                .contains(finalFiltro)

                                ||

                                v.getPropietario() != null &&
                                        v.getPropietario()
                                                .toLowerCase()
                                                .contains(finalFiltro)
                )
                .collect(Collectors.toList());

        listarVehiculo.setAll(filtrados);
        tableView.setItems(listarVehiculo);
    }


    // =========================================================
    // LIMPIAR FILTRO
    // =========================================================

    @FXML
    private void limpiarFiltro() {

        txtFiltroDato.clear();

        listar();
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    @FXML
    private void clearForm() {

        idVehiculoCE = 0L;

        formulario = null;

        txtPlaca.clear();
        txtPlaca.setDisable(false);

        cmbMarca.setValue(null);

        cmbModelo.getItems().clear();
        cmbModelo.setValue(null);

        cmbColor.getItems().clear();
        cmbColor.setValue(null);

        txtAnio.clear();

        cmbPropietario.setValue(null);

        lbnMsg.setText("");

        tableView.getSelectionModel().clearSelection();
    }


    // =========================================================
    // MOSTRAR VEHÍCULO SELECCIONADO
    // =========================================================

    private void mostrarVehiculoSeleccionado(Vehiculo vehiculo) {

        if (vehiculo == null) {

            lblPlacaSeleccionada.setText("-");
            lblVehiculoSeleccionado.setText("-");
            lblDetalleVehiculo.setText("-");
            lblPropietarioSeleccionado.setText("-");

            return;
        }

        lblPlacaSeleccionada.setText(
                vehiculo.getPlaca()
        );

        lblVehiculoSeleccionado.setText(
                vehiculo.getMarca() + " "
                        + vehiculo.getModelo()
        );

        lblDetalleVehiculo.setText(
                "Año: " + vehiculo.getAnio()
                        + " | Color: "
                        + vehiculo.getColor()
        );

        lblPropietarioSeleccionado.setText(
                vehiculo.getPropietario()
        );
    }


    // =========================================================
    // ESTADÍSTICAS
    // =========================================================

    private void actualizarEstadisticas() {

        List<Vehiculo> vehiculos = vs.findAll();

        lblTotalVehiculos.setText(
                String.valueOf(vehiculos.size())
        );

        lblTotalMarcas.setText(
                String.valueOf(
                        vehiculos.stream()
                                .map(Vehiculo::getMarca)
                                .filter(m -> m != null && !m.isBlank())
                                .distinct()
                                .count()
                )
        );

        int anioReciente = vehiculos.stream()
                .map(Vehiculo::getAnio)
                .filter(a -> a != null)
                .max(Integer::compareTo)
                .orElse(0);

        lblAnioReciente.setText(
                anioReciente == 0
                        ? "-"
                        : String.valueOf(anioReciente)
        );

        lblTotalPropietarios.setText(
                String.valueOf(
                        vehiculos.stream()
                                .map(Vehiculo::getPropietario)
                                .filter(p -> p != null && !p.isBlank())
                                .distinct()
                                .count()
                )
        );
    }
}