package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.components.ToltipCustom;
import pe.edu.upeu.sysventas.model.Propietario;
import pe.edu.upeu.sysventas.service.IPropietarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PropietarioController {

    private final IPropietarioService ps;

    @FXML
    TextField txtDni, txtNombre, txtApellido,
            txtTelefono, txtDireccion, txtFiltroDato;

    @FXML
    private TableView<Propietario> tableView;

    @FXML
    private AnchorPane miContenedor;

    @FXML
    Label lbnMsg;

    ObservableList<Propietario> listarPropietario;

    Propietario formulario;

    Long idPropietarioCE = 0L;

    private Validator validator;

    private final ToltipCustom ttc = new ToltipCustom();
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

        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();

        TableViewHelper<Propietario> tableViewHelper =
                new TableViewHelper<>();

        LinkedHashMap<String, ColumnInfo> columns =
                new LinkedHashMap<>();

        columns.put("DNI",
                new ColumnInfo("dni", 100.0));

        columns.put("Nombre",
                new ColumnInfo("nombre", 130.0));

        columns.put("Apellido",
                new ColumnInfo("apellido", 150.0));

        columns.put("Teléfono",
                new ColumnInfo("telefono", 110.0));

        columns.put("Dirección",
                new ColumnInfo("direccion", 220.0));


        Consumer<Propietario> updateAction = propietario -> {

            editForm(propietario);

            idPropietarioCE =
                    propietario.getIdPropietario();

            txtDni.setDisable(true);
        };


        Consumer<Propietario> deleteAction = propietario -> {

            ps.delete(
                    propietario.getIdPropietario()
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
                        -> filtrarPropietarios()
        );

        listar();
    }


    public void listar() {

        listarPropietario =
                FXCollections.observableArrayList(
                        ps.findAll()
                );

        tableView.getItems().clear();

        tableView.getItems().addAll(
                listarPropietario
        );
    }


    @FXML
    public void limpiarFiltro() {

        txtFiltroDato.clear();

        listar();
    }


    @FXML
    public void validarFormulario() {

        formulario = new Propietario();

        formulario.setDni(
                txtDni.getText().trim()
        );

        formulario.setNombre(
                txtNombre.getText().trim()
        );

        formulario.setApellido(
                txtApellido.getText().trim()
        );

        formulario.setTelefono(
                txtTelefono.getText().trim()
        );

        formulario.setDireccion(
                txtDireccion.getText().trim()
        );


        Set<ConstraintViolation<Propietario>> violaciones =
                validator.validate(formulario);


        List<ConstraintViolation<Propietario>>
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


    private void mostrarErroresValidacion(
            List<ConstraintViolation<Propietario>> violaciones) {

        limpiarError();

        Map<String, Control> campos =
                new LinkedHashMap<>();

        campos.put("dni", txtDni);
        campos.put("nombre", txtNombre);
        campos.put("apellido", txtApellido);
        campos.put("telefono", txtTelefono);
        campos.put("direccion", txtDireccion);

        LinkedHashMap<String, String> erroresOrdenados =
                new LinkedHashMap<>();

        final Control[] primerCtrl = {null};


        for (String campo : campos.keySet()) {

            violaciones.stream()
                    .filter(v ->
                            v.getPropertyPath()
                                    .toString()
                                    .equals(campo)
                    )
                    .findFirst()
                    .ifPresent(v -> {

                        erroresOrdenados.put(
                                campo,
                                v.getMessage()
                        );

                        Control c = campos.get(campo);

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


        if (!erroresOrdenados.isEmpty()) {

            lbnMsg.setText(
                    erroresOrdenados
                            .entrySet()
                            .iterator()
                            .next()
                            .getValue()
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


    private void procesarFormulario() {

        Stage stage =
                (Stage) miContenedor
                        .getScene()
                        .getWindow();

        limpiarError();

        double w = stage.getWidth() / 1.5;
        double h = stage.getHeight() / 2;


        // EDITAR

        if (idPropietarioCE > 0L) {

            formulario.setIdPropietario(
                    idPropietarioCE
            );

            ps.update(
                    idPropietarioCE,
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

            // REGISTRAR

            ps.save(formulario);

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


    @FXML
    public void filtrarPropietarios() {

        String texto =
                txtFiltroDato.getText()
                        .trim()
                        .toLowerCase();


        if (texto.isEmpty()) {

            listar();

            return;
        }


        List<Propietario> propietarios =
                ps.findAll();


        List<Propietario> filtrados =
                propietarios.stream()
                        .filter(p ->
                                p.getDni()
                                        .toLowerCase()
                                        .contains(texto)

                                        || p.getNombre()
                                        .toLowerCase()
                                        .contains(texto)

                                        || p.getApellido()
                                        .toLowerCase()
                                        .contains(texto)

                                        || p.getTelefono()
                                        .toLowerCase()
                                        .contains(texto)

                                        || p.getDireccion()
                                        .toLowerCase()
                                        .contains(texto)
                        )
                        .toList();


        tableView.getItems().clear();

        tableView.getItems().addAll(
                filtrados
        );
    }


    public void editForm(Propietario propietario) {

        txtDni.setText(
                propietario.getDni()
        );

        txtNombre.setText(
                propietario.getNombre()
        );

        txtApellido.setText(
                propietario.getApellido()
        );

        txtTelefono.setText(
                propietario.getTelefono()
        );

        txtDireccion.setText(
                propietario.getDireccion()
        );

        idPropietarioCE =
                propietario.getIdPropietario();

        limpiarError();
    }


    public void limpiarError() {

        List.of(
                txtDni,
                txtNombre,
                txtApellido,
                txtTelefono,
                txtDireccion
        ).forEach(c -> {

            c.getStyleClass()
                    .remove("text-field-error");

            ttc.limpiarCampo(c);
        });
    }


    public void clearForm() {

        txtDni.clear();

        txtNombre.clear();

        txtApellido.clear();

        txtTelefono.clear();

        txtDireccion.clear();

        idPropietarioCE = 0L;

        txtDni.setDisable(false);

        lbnMsg.setText("");

        limpiarError();
    }
}