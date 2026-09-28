package pe.edu.upeu.sysventas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;

public class MainController {
    @FXML
    public void volverInicio(ActionEvent event) {

        cambiarPantalla(
                "main_principal.fxml",
                "Padrón Vehicular",
                event
        );
    }

    @FXML
    public void abrirVehiculos(ActionEvent event) {
        cambiarPantalla(
                "main_vehiculo.fxml",
                "Padrón Vehicular",
                event
        );
    }

    @FXML
    public void abrirPropietarios(ActionEvent event) {
        cambiarPantalla(
                "main_propietario.fxml",
                "Propietarios",
                event
        );
    }


    @FXML
    public void abrirMarcas(ActionEvent event) {
        cambiarPantalla(
                "main_marca.fxml",
                "Marcas",
                event
        );
    }

    @FXML
    public void abrirInfracciones(ActionEvent event) {
        cambiarPantalla(
                "main_infraccion.fxml",
                "Infracciones",
                event
        );
    }

    private void cambiarPantalla(
            String archivo,
            String titulo,
            ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/view/" + archivo
                    )
            );

            loader.setControllerFactory(
                    AppContext.getInstance()::getBean
            );

            Parent root = loader.load();

            Node source = (Node) event.getSource();

            Stage stage =
                    (Stage) source.getScene().getWindow();

            stage.setTitle(titulo);

            stage.getScene().setRoot(root);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}