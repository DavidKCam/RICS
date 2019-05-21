package Controllers;

import Models.AlertHelper;
import Models.DBManager;
import Models.Part;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class IssuePartController
{

    @FXML
    private JFXTextField txt_quantity;

    @FXML
    private JFXTextField txt_issuedTo;

    @FXML
    private Label lbl_partNo;

    @FXML
    private JFXButton btn_cancel;




    public void setLabel(String partNo)
    {
        try
        {
            lbl_partNo.setText(partNo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void on_issueClick()
    {
        int qty = Integer.parseInt(txt_quantity.getText());
        String partNo = lbl_partNo.getText();
        Window window = btn_cancel.getScene().getWindow();

        DBManager dbm = new DBManager();
        ObservableList<Part> parts = dbm.loadParts();
        try
        {
            for (Part part : parts)
            {
                if (part.getPartNumber().equals(partNo) && part.getOnHand() >= qty && qty > 0)
                {
                    int newStockLevel = part.getOnHand() - qty;
                    dbm.updateStockLevel(newStockLevel, partNo);
                    dbm.saveTransaction(part, 'I', qty, txt_issuedTo.getText());


                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/Partmaster.fxml"));
                    Stage partMaster = new Stage(StageStyle.TRANSPARENT);
                    partMaster.setTitle("RICS 1.0 PartMaster");
                    partMaster.setScene(new Scene(loader.load()));
                    PartMasterController controller = loader.getController();
                    controller.refresh(partNo);
                    partMaster.show();
                    closeIssuePart();


                    AlertHelper.showAlert(Alert.AlertType.CONFIRMATION, window, "Part issued", "you have " +
                            "issued " + qty + " of " + partNo + ".");

                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @FXML
    private void on_cancelClick()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/Partmaster.fxml"));
            Stage partMaster = new Stage(StageStyle.TRANSPARENT);
            partMaster.setTitle("RICS 1.0 PartMaster");
            partMaster.setScene(new Scene(loader.load()));
            PartMasterController controller = loader.getController();
            controller.refresh(lbl_partNo.getText());

            partMaster.show();
            closeIssuePart();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    @FXML
    private void closeIssuePart()
    {
        Stage stage = (Stage)btn_cancel.getScene().getWindow();
        stage.close();
    }
}
