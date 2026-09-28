package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.components.ToltipCustom;
import pe.edu.upeu.sysventas.config.AppContext;
import pe.edu.upeu.sysventas.model.Infraccion;
import pe.edu.upeu.sysventas.model.Vehiculo;
import pe.edu.upeu.sysventas.service.IInfraccionService;
import pe.edu.upeu.sysventas.service.IVehiculoService;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class InfraccionController {

    private final IInfraccionService is;
    private final IVehiculoService vs;

    @FXML
    TextField txtCodigo,
            txtDescripcion,
            txtGravedad,
            txtMulta,
            txtFiltroDato;

    @FXML
    private ComboBox<Vehiculo> cmbVehiculo;

    @FXML
    private TableView<Infraccion> tableView;

    @FXML
    private AnchorPane miContenedor;

    @FXML
    Label lbnMsg;

    ObservableList<Infraccion> listarInfraccion;

    Infraccion formulario;

    Long idInfraccionCE = 0L;

    private Validator validator;

    private final ToltipCustom ttc = new ToltipCustom();


    // =========================================================
    // VOLVER AL INICIO
    // =========================================================

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


    // =========================================================
    // INICIALIZAR
    // =========================================================

    @FXML
    public void initialize() {

        cargarVehiculos();

        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();

        TableViewHelper<Infraccion> tableViewHelper =
                new TableViewHelper<>();

        LinkedHashMap<String, ColumnInfo> columns =
                new LinkedHashMap<>();

        columns.put(
                "ID",
                new ColumnInfo("idInfraccion", 60.0)
        );

        columns.put(
                "Código",
                new ColumnInfo("codigo", 90.0)
        );

        columns.put(
                "Descripción",
                new ColumnInfo("descripcion", 220.0)
        );

        columns.put(
                "Gravedad",
                new ColumnInfo("gravedad", 120.0)
        );

        columns.put(
                "Multa",
                new ColumnInfo("multa", 100.0)
        );

        columns.put(
                "Vehículo",
                new ColumnInfo("vehiculo", 120.0)
        );


        Consumer<Infraccion> updateAction = infraccion -> {

            editForm(infraccion);

            idInfraccionCE =
                    infraccion.getIdInfraccion();
        };


        Consumer<Infraccion> deleteAction = infraccion -> {

            is.delete(
                    infraccion.getIdInfraccion()
            );

            Stage stage =
                    (Stage) miContenedor
                            .getScene()
                            .getWindow();

            double w = stage.getWidth() / 1.5;
            double h = stage.getHeight() / 2;

            Toast.showToast(
                    stage,
                    "Se eliminó correctamente!!",
                    2000,
                    w,
                    h
            );

            listar();
        };


        tableViewHelper.addColumnsInOrderWithSize(
                tableView,
                columns,
                updateAction,
                deleteAction
        );

        tableView.setTableMenuButtonVisible(true);


        txtFiltroDato.textProperty().addListener(
                (observable, oldValue, newValue)
                        -> filtrarInfracciones()
        );


        listar();
    }


    // =========================================================
    // CARGAR VEHÍCULOS EN EL COMBOBOX
    // =========================================================

    private void cargarVehiculos() {

        cmbVehiculo.getItems().clear();

        cmbVehiculo.getItems().addAll(
                vs.findAll()
        );


        cmbVehiculo.setCellFactory(lista ->
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Vehiculo vehiculo,
                            boolean empty) {

                        super.updateItem(
                                vehiculo,
                                empty
                        );

                        if (empty || vehiculo == null) {

                            setText(null);

                        } else {

                            setText(
                                    vehiculo.getPlaca()
                                            + " - "
                                            + vehiculo.getMarca()
                                            + " "
                                            + vehiculo.getModelo()
                            );
                        }
                    }
                }
        );


        cmbVehiculo.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Vehiculo vehiculo,
                            boolean empty) {

                        super.updateItem(
                                vehiculo,
                                empty
                        );

                        if (empty || vehiculo == null) {

                            setText(null);

                        } else {

                            setText(
                                    vehiculo.getPlaca()
                                            + " - "
                                            + vehiculo.getMarca()
                                            + " "
                                            + vehiculo.getModelo()
                            );
                        }
                    }
                }
        );
    }


    // =========================================================
    // LISTAR
    // =========================================================

    public void listar() {

        listarInfraccion =
                FXCollections.observableArrayList(
                        is.findAll()
                );

        tableView.getItems().clear();

        tableView.getItems().addAll(
                listarInfraccion
        );
    }


    // =========================================================
    // LIMPIAR FILTRO
    // =========================================================

    @FXML
    public void limpiarFiltro() {

        txtFiltroDato.clear();

        listar();
    }


    // =========================================================
    // VALIDAR FORMULARIO
    // =========================================================

    @FXML
    public void validarFormulario() {

        formulario = new Infraccion();


        formulario.setCodigo(
                txtCodigo.getText().trim()
        );


        formulario.setDescripcion(
                txtDescripcion.getText().trim()
        );


        formulario.setGravedad(
                txtGravedad.getText().trim()
        );


        try {

            formulario.setMulta(
                    Double.parseDouble(
                            txtMulta.getText().trim()
                    )
            );

        } catch (NumberFormatException e) {

            formulario.setMulta(null);
        }


        // VALIDAR QUE SE HAYA SELECCIONADO UN VEHÍCULO

        if (cmbVehiculo.getValue() == null) {

            lbnMsg.setText(
                    "Debe seleccionar un vehículo."
            );

            lbnMsg.setStyle(
                    "-fx-text-fill: red; -fx-font-size: 16px;"
            );

            cmbVehiculo.requestFocus();

            return;
        }


        // GUARDAR LA PLACA DEL VEHÍCULO

        formulario.setVehiculo(
                cmbVehiculo.getValue().getPlaca()
        );


        // VALIDACIÓN CON BEAN VALIDATION

        Set<ConstraintViolation<Infraccion>> violaciones =
                validator.validate(formulario);


        List<ConstraintViolation<Infraccion>>
                violacionesOrdenadas =
                violaciones.stream()
                        .sorted(
                                Comparator.comparing(
                                        v -> v.getPropertyPath()
                                                .toString()
                                )
                        )
                        .toList();


        if (violacionesOrdenadas.isEmpty()) {

            procesarFormulario();

        } else {

            mostrarErroresValidacion(
                    violacionesOrdenadas
            );
        }
    }


    // =========================================================
    // MOSTRAR ERRORES
    // =========================================================

    private void mostrarErroresValidacion(
            List<ConstraintViolation<Infraccion>>
                    violaciones) {

        limpiarError();


        Map<String, Control> campos =
                new LinkedHashMap<>();


        campos.put(
                "codigo",
                txtCodigo
        );

        campos.put(
                "descripcion",
                txtDescripcion
        );

        campos.put(
                "gravedad",
                txtGravedad
        );

        campos.put(
                "multa",
                txtMulta
        );


        final Control[] primerCtrl =
                {null};


        for (String campo : campos.keySet()) {

            violaciones.stream()
                    .filter(
                            v ->
                                    v.getPropertyPath()
                                            .toString()
                                            .equals(campo)
                    )
                    .findFirst()
                    .ifPresent(v -> {

                        Control c =
                                campos.get(campo);


                        if (c != null) {

                            ttc.marcarError(
                                    c,
                                    v.getMessage().trim()
                            );
                        }


                        if (primerCtrl[0] == null) {

                            primerCtrl[0] = c;
                        }
                    });
        }


        if (!violaciones.isEmpty()) {

            lbnMsg.setText(
                    violaciones.get(0).getMessage()
            );

            lbnMsg.setStyle(
                    "-fx-text-fill: red; -fx-font-size: 16px;"
            );


            if (primerCtrl[0] != null) {

                Platform.runLater(
                        primerCtrl[0]::requestFocus
                );
            }
        }
    }


    // =========================================================
    // PROCESAR FORMULARIO
    // =========================================================

    private void procesarFormulario() {

        Stage stage =
                (Stage) miContenedor
                        .getScene()
                        .getWindow();

        limpiarError();


        double w =
                stage.getWidth() / 1.5;

        double h =
                stage.getHeight() / 2;


        // EDITAR

        if (idInfraccionCE > 0L) {

            formulario.setIdInfraccion(
                    idInfraccionCE
            );


            is.update(
                    idInfraccionCE,
                    formulario
            );


            Toast.showToast(
                    stage,
                    "Se actualizó correctamente!!",
                    2000,
                    w,
                    h
            );


        } else {

            // GUARDAR

            is.save(formulario);


            Toast.showToast(
                    stage,
                    "Se guardó correctamente!!",
                    2000,
                    w,
                    h
            );
        }


        clearForm();

        listar();
    }


    // =========================================================
    // FILTRAR INFRACCIONES
    // =========================================================

    @FXML
    public void filtrarInfracciones() {

        String texto =
                txtFiltroDato.getText()
                        .trim()
                        .toLowerCase();


        if (texto.isEmpty()) {

            listar();

            return;
        }


        List<Infraccion> infracciones =
                is.findAll();


        List<Infraccion> filtradas =
                infracciones.stream()
                        .filter(i ->

                                i.getCodigo()
                                        .toLowerCase()
                                        .contains(texto)

                                        ||

                                        i.getDescripcion()
                                                .toLowerCase()
                                                .contains(texto)

                                        ||

                                        i.getGravedad()
                                                .toLowerCase()
                                                .contains(texto)

                                        ||

                                        i.getVehiculo()
                                                .toLowerCase()
                                                .contains(texto)

                        )
                        .toList();


        tableView.getItems().clear();

        tableView.getItems().addAll(
                filtradas
        );
    }


    // =========================================================
    // EDITAR
    // =========================================================

    public void editForm(Infraccion infraccion) {

        txtCodigo.setText(
                infraccion.getCodigo()
        );


        txtDescripcion.setText(
                infraccion.getDescripcion()
        );


        txtGravedad.setText(
                infraccion.getGravedad()
        );


        txtMulta.setText(
                String.valueOf(
                        infraccion.getMulta()
                )
        );


        // BUSCAR EL VEHÍCULO POR SU PLACA

        for (Vehiculo vehiculo :
                cmbVehiculo.getItems()) {

            if (
                    vehiculo.getPlaca()
                            .equalsIgnoreCase(
                                    infraccion.getVehiculo()
                            )
            ) {

                cmbVehiculo.setValue(
                        vehiculo
                );

                break;
            }
        }


        idInfraccionCE =
                infraccion.getIdInfraccion();


        limpiarError();
    }


    // =========================================================
    // LIMPIAR ERRORES
    // =========================================================

    public void limpiarError() {

        List.of(
                txtCodigo,
                txtDescripcion,
                txtGravedad,
                txtMulta
        ).forEach(c -> {

            c.getStyleClass()
                    .remove("text-field-error");

            ttc.limpiarCampo(c);
        });


        lbnMsg.setText("");
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    @FXML
    public void clearForm() {

        txtCodigo.clear();

        txtDescripcion.clear();

        txtGravedad.clear();

        txtMulta.clear();


        // LIMPIAR COMBOBOX

        cmbVehiculo.setValue(null);


        idInfraccionCE = 0L;


        lbnMsg.setText("");


        limpiarError();
    }
}