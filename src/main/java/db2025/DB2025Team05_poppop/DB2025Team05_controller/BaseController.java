package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_common.AppSession;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public abstract class BaseController {
    protected User currentUser;

    @FXML
    public void initialize() {
        currentUser = AppSession.getCurrentUser();
    }
}

