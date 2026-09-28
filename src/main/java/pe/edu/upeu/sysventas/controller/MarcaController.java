package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.components.ToltipCustom;
import pe.edu.upeu.sysventas.model.Marca;
import pe.edu.upeu.sysventas.service.IMarcaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MarcaController {

    private final IMarcaService ms;

    @FXML
    private AnchorPane miContenedor;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtColor;

    @FXML
    private TextField txtModelo;

    @FXML
    private TextField txtFiltroDato;

    @FXML
    private ListView<String> listaColores;

    @FXML
    private ListView<String> listaModelos;

    @FXML
    private TableView<Marca> tableView;

    @FXML
    private Label lbnMsg;


    private ObservableList<Marca> listarMarca =
            FXCollections.observableArrayList();

    private ObservableList<String> colores =
            FXCollections.observableArrayList();

    private ObservableList<String> modelos =
            FXCollections.observableArrayList();

    private Marca formulario;

    private Long idMarcaCE = 0L;

    private Validator validator;

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

        listaColores.setItems(colores);
        listaModelos.setItems(modelos);

        listar();
    }


    // ==========================
    // AGREGAR COLOR
    // ==========================

    @FXML
    public void agregarColor() {

        String color =
                txtColor.getText().trim();

        if (color.isEmpty()) {

            mostrarMensaje(
                    "Ingrese un color"
            );

            return;
        }

        boolean existe =
                colores.stream()
                        .anyMatch(c ->
                                c.equalsIgnoreCase(color));

        if (existe) {

            mostrarMensaje(
                    "Ese color ya está registrado"
            );

            return;
        }

        colores.add(color);

        txtColor.clear();

        lbnMsg.setText("");
    }


    // ==========================
    // ELIMINAR COLOR
    // ==========================

    @FXML
    public void eliminarColor() {

        String color =
                listaColores.getSelectionModel()
                        .getSelectedItem();

        if (color == null) {

            mostrarMensaje(
                    "Seleccione un color"
            );

            return;
        }

        colores.remove(color);

        lbnMsg.setText("");
    }


    // ==========================
    // AGREGAR MODELO
    // ==========================

    @FXML
    public void agregarModelo() {

        String modelo =
                txtModelo.getText().trim();

        if (modelo.isEmpty()) {

            mostrarMensaje(
                    "Ingrese un modelo"
            );

            return;
        }

        boolean existe =
                modelos.stream()
                        .anyMatch(m ->
                                m.equalsIgnoreCase(modelo));

        if (existe) {

            mostrarMensaje(
                    "Ese modelo ya está registrado"
            );

            return;
        }

        modelos.add(modelo);

        txtModelo.clear();

        lbnMsg.setText("");
    }


    // ==========================
    // ELIMINAR MODELO
    // ==========================

    @FXML
    public void eliminarModelo() {

        String modelo =
                listaModelos.getSelectionModel()
                        .getSelectedItem();

        if (modelo == null) {

            mostrarMensaje(
                    "Seleccione un modelo"
            );

            return;
        }

        modelos.remove(modelo);

        lbnMsg.setText("");
    }


    // ==========================
    // LISTAR
    // ==========================

    public void listar() {

        listarMarca.clear();

        listarMarca.addAll(
                ms.findAll()
        );

        LinkedHashMap<String, ColumnInfo> columnas =
                new LinkedHashMap<>();

        columnas.put(
                "ID",
                new ColumnInfo("idMarca", 60.0)
        );

        columnas.put(
                "Marca",
                new ColumnInfo("nombre", 130.0)
        );

        columnas.put(
                "Colores",
                new ColumnInfo("colores", 180.0)
        );

        columnas.put(
                "Modelos",
                new ColumnInfo("modelos", 180.0)
        );

        TableViewHelper<Marca> tableHelper =
                new TableViewHelper<>();

        tableHelper.addColumnsInOrderWithSize(
                tableView,
                columnas,
                this::editForm,
                marca -> eliminar(
                        marca.getIdMarca()
                )
        );

        tableView.setItems(listarMarca);
    }
    // ==========================
    // VALIDAR FORMULARIO
    // ==========================

    @FXML
    public void validarFormulario() {

        formulario = Marca.builder()
                .idMarca(
                        idMarcaCE == 0L
                                ? null
                                : idMarcaCE
                )
                .nombre(
                        txtNombre.getText().trim()
                )
                .colores(
                        new ArrayList<>(colores)
                )
                .modelos(
                        new ArrayList<>(modelos)
                )
                .build();


        Set<ConstraintViolation<Marca>> errores =
                validator.validate(formulario);

        if (!errores.isEmpty()) {

            String mensaje =
                    errores.stream()
                            .map(
                                    ConstraintViolation::getMessage
                            )
                            .collect(
                                    Collectors.joining("\n")
                            );

            mostrarMensaje(mensaje);

            return;
        }

        procesarFormulario();
    }


    // ==========================
    // GUARDAR / ACTUALIZAR
    // ==========================
    private void procesarFormulario() {

        Stage stage =
                (Stage) miContenedor.getScene().getWindow();

        if (idMarcaCE == 0L) {

            ms.save(formulario);

            Toast.showToast(
                    stage,
                    "Marca registrada correctamente",
                    2000,
                    stage.getX() + stage.getWidth() - 300,
                    stage.getY() + 80
            );

        } else {

            ms.update(
                    idMarcaCE,
                    formulario
            );

            Toast.showToast(
                    stage,
                    "Marca actualizada correctamente",
                    2000,
                    stage.getX() + stage.getWidth() - 300,
                    stage.getY() + 80
            );
        }

        listar();

        clearForm();
    }

    // ==========================
    // EDITAR
    // ==========================

    private void editForm(Marca marca) {

        formulario = marca;

        idMarcaCE =
                marca.getIdMarca();

        txtNombre.setText(
                marca.getNombre()
        );


        colores.clear();

        if (marca.getColores() != null) {

            colores.addAll(
                    marca.getColores()
            );
        }


        modelos.clear();

        if (marca.getModelos() != null) {

            modelos.addAll(
                    marca.getModelos()
            );
        }
    }


    // ==========================
    // ELIMINAR
    // ==========================

    private void eliminar(Long id) {

        ms.delete(id);

        Stage stage =
                (Stage) miContenedor.getScene().getWindow();

        Toast.showToast(
                stage,
                "Se eliminó correctamente",
                2000,
                stage.getX() + stage.getWidth() - 300,
                stage.getY() + 80
        );

        listar();

        clearForm();
    }


    // ==========================
    // FILTRAR
    // ==========================

    private void filtrarMarcas() {

        String texto =
                txtFiltroDato.getText()
                        .trim()
                        .toLowerCase();

        if (texto.isEmpty()) {

            listarMarca.setAll(
                    ms.findAll()
            );

            return;
        }

        List<Marca> filtradas =
                ms.findAll()
                        .stream()
                        .filter(m ->
                                contiene(
                                        m.getNombre(),
                                        texto
                                )
                        )
                        .toList();

        listarMarca.setAll(
                filtradas
        );
    }


    private boolean contiene(
            String valor,
            String texto) {

        return valor != null &&
                valor.toLowerCase()
                        .contains(texto);
    }


    // ==========================
    // LIMPIAR FILTRO
    // ==========================

    @FXML
    public void limpiarFiltro() {

        txtFiltroDato.clear();

        listarMarca.setAll(
                ms.findAll()
        );
    }


    // ==========================
    // LIMPIAR FORMULARIO
    // ==========================

    @FXML
    public void clearForm() {

        idMarcaCE = 0L;

        txtNombre.clear();
        txtColor.clear();
        txtModelo.clear();

        colores.clear();
        modelos.clear();

        lbnMsg.setText("");
    }


    // ==========================
    // MENSAJE
    // ==========================

    private void mostrarMensaje(String mensaje) {

        lbnMsg.setText(mensaje);
    }
}