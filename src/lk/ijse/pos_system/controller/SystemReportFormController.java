package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXDatePicker;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lk.ijse.pos_system.business.BOFactory;
import lk.ijse.pos_system.business.custom.ReportBO;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.util.NavigationUtil;
import lk.ijse.pos_system.view.tm.CustomerWiseIncomeReportTM;
import lk.ijse.pos_system.view.tm.ReportTM;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SystemReportFormController {

    public AnchorPane contextReports;
    public Label lblMostMovableItem;
    public Label lblLeastMovableItem;
    public Label lblDate;
    public Label lblTime;
    public JFXDatePicker datePicker;

    public TabPane tabPane;
    public Tab tabDailyReport;
    public Tab tabMonthlyReport;
    public Tab tabAnnualReport;
    public Tab tabCustomerWiseReport;

    public AnchorPane contextDailyReport;
    public TableView<ReportTM> tblDailyReport;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colSalesQuantity;
    public TableColumn colIncome;

    public TableView<CustomerWiseIncomeReportTM> tblCustomerWiseIncome;
    public TableColumn colCustomerId;
    public TableColumn colCustomerTitle;
    public TableColumn colCustomerName;
    public TableColumn colCustomerCity;
    public TableColumn colCustomerIncome;

    public TableView<ReportTM> tblMonthlyReport;
    public TableColumn colItemCode1;
    public TableColumn colDescription1;
    public TableColumn colSalesQuantity1;
    public TableColumn colIncome1;

    public TableView<ReportTM> tblAnnualReport;
    public TableColumn colItemCode2;
    public TableColumn colDescription2;
    public TableColumn colSalesQuantity2;
    public TableColumn colIncome2;

    private final ReportBO reportBO = (ReportBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.REPORT);
    private String date = null;
    private URL resource;
    private String reportType;

    private final ArrayList<ReportTM> tmDailyReport = new ArrayList<>();
    private final ArrayList<ReportTM> tmMonthlyReport = new ArrayList<>();
    private final ArrayList<ReportTM> tmAnnualReport = new ArrayList<>();
    private final ArrayList<CustomerWiseIncomeReportTM> tmCustomerWiseIncomeReport = new ArrayList<>();

    public void initialize() {
        loadDateAndTime();

        date = lblDate.getText();
        reportType  = "Daily Report";

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.getText().equals("Daily Report")) {
                reportType = newValue.getText();

            } else if (newValue.getText().equals("Monthly Report")) {
                reportType = newValue.getText();

            } else if (newValue.getText().equals("Annual Report")) {
                reportType = newValue.getText();

            }

            try {
                loadMostMovableItem(date);
                loadLeastMovableItem(date);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        });

        try {
            loadMostMovableItem(date);
            loadLeastMovableItem(date);

            loadDailyReport(date);
            loadCustomerWiseIncomeReport(date);
            loadMonthlyReport(date);
            loadAnnualReport(date);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSalesQuantity.setCellValueFactory(new PropertyValueFactory<>("salesQuantity"));
        colIncome.setCellValueFactory(new PropertyValueFactory<>("income"));

        colItemCode1.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription1.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSalesQuantity1.setCellValueFactory(new PropertyValueFactory<>("salesQuantity"));
        colIncome1.setCellValueFactory(new PropertyValueFactory<>("income"));

        colItemCode2.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription2.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSalesQuantity2.setCellValueFactory(new PropertyValueFactory<>("salesQuantity"));
        colIncome2.setCellValueFactory(new PropertyValueFactory<>("income"));

        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colCustomerTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCustomerCity.setCellValueFactory(new PropertyValueFactory<>("customerCity"));
        colCustomerIncome.setCellValueFactory(new PropertyValueFactory<>("income"));

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            //date = newValue;
            date = String.valueOf(newValue);
            try {
                loadMostMovableItem(date);
                loadLeastMovableItem(date);

                loadDailyReport(date);
                loadCustomerWiseIncomeReport(date);
                loadMonthlyReport(date);
                loadAnnualReport(date);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

    }

    private void loadAnnualReport(String date) throws SQLException, ClassNotFoundException {
        tmAnnualReport.clear();
        ArrayList<CustomDTO> dtoAnnualReport = reportBO.getAnnualReport(date);
        for (CustomDTO dto : dtoAnnualReport) {
            tmAnnualReport.add(new ReportTM(
                    dto.getItemCode(),
                    dto.getDescription(),
                    dto.getSalesQuantity(),
                    dto.getIncome()
            ));
        }
        tblAnnualReport.getItems().clear();
        tblAnnualReport.setItems(FXCollections.observableArrayList(tmAnnualReport));
    }

    private void loadMonthlyReport(String date) throws SQLException, ClassNotFoundException {
        tmMonthlyReport.clear();
        ArrayList<CustomDTO> dtoMonthlyReport = reportBO.getMonthlyReport(date);
        for (CustomDTO dto : dtoMonthlyReport) {
            tmMonthlyReport.add(new ReportTM(
                    dto.getItemCode(),
                    dto.getDescription(),
                    dto.getSalesQuantity(),
                    dto.getIncome()
            ));
        }
        tblMonthlyReport.getItems().clear();
        tblMonthlyReport.setItems(FXCollections.observableArrayList(tmMonthlyReport));
    }

    private void loadDailyReport(String date) throws SQLException, ClassNotFoundException {
        tmDailyReport.clear();
        ArrayList<CustomDTO> dtoDailyReport = reportBO.getDailyReport(date);
        for (CustomDTO dto : dtoDailyReport) {
            tmDailyReport.add(new ReportTM(
                    dto.getItemCode(),
                    dto.getDescription(),
                    dto.getSalesQuantity(),
                    dto.getIncome()
            ));
        }
        tblDailyReport.getItems().clear();
        tblDailyReport.setItems(FXCollections.observableArrayList(tmDailyReport));
    }

    private void loadCustomerWiseIncomeReport(String date) throws SQLException, ClassNotFoundException {
        tmCustomerWiseIncomeReport.clear();
        ArrayList<CustomDTO> dtoCustomerWiseIncomeReport = reportBO.getCustomerWiseIncome(date);
        for (CustomDTO dto : dtoCustomerWiseIncomeReport) {

            String custTitle = null;
            String custName = null;
            String custCity = null;

            if (dto.getCustName() == null) {
                custTitle = "Unknown";
                custName = "Unknown";
                custCity = "Unknown";
            } else {
                custTitle = dto.getCustTitle();
                custName = dto.getCustName();
                custCity = dto.getCustName();
            }

            tmCustomerWiseIncomeReport.add(new CustomerWiseIncomeReportTM(
                    dto.getCustID(),
                    custTitle,
                    custName,
                    custCity,
                    dto.getTotalOrderCost()
            ));
        }
        tblCustomerWiseIncome.getItems().clear();
        tblCustomerWiseIncome.setItems(FXCollections.observableArrayList(tmCustomerWiseIncomeReport));
    }

    private void loadLeastMovableItem(String date) throws SQLException, ClassNotFoundException {
        String leastMovableItem = reportBO.getLeastMovableItem(reportType, date);
        lblLeastMovableItem.setText(leastMovableItem);
    }

    private void loadMostMovableItem(String date) throws SQLException, ClassNotFoundException {
        String mostMovableItem = reportBO.getMostMovableItem(reportType, date);
        lblMostMovableItem.setText(mostMovableItem);
    }

    public void loadDateAndTime() {
        // load Date
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(f.format(date));

        // load Time
        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {

            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");

            Calendar cal = new GregorianCalendar();
            Date date1 = cal.getTime();
            lblTime.setText(simpleTimeFormat.format(date1));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/AdminDashBoardForm.fxml");
        NavigationUtil.navigateToPage(resource, contextReports);
    }

    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/MainForm.fxml");
        NavigationUtil.logOutOnAction(resource, contextReports);
    }

    public void printDailyReportOnAction(ActionEvent actionEvent) {

        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("../view/reports/DailyReport.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            ObservableList<ReportTM> items = tblDailyReport.getItems();

            double totalIncome = 0;
            for (ReportTM tm : tmDailyReport) {
                totalIncome += tm.getIncome();
            }

            String date = String.valueOf(datePicker.getValue());

            /*Setting parameter values*/
            HashMap map = new HashMap();
            map.put("date", date);
            map.put("totalIncome", totalIncome);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(items.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void printMonthlyReportOnAction(ActionEvent actionEvent) {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("../view/reports/MonthlyReport.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            ObservableList<ReportTM> items = tblMonthlyReport.getItems();

            double totalIncome = 0;
            for (ReportTM tm : tmMonthlyReport) {
                totalIncome += tm.getIncome();
            }

            String date = String.valueOf(datePicker.getValue());

            HashMap map = new HashMap();
            map.put("date", date);
            map.put("totalIncome", totalIncome);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(items.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void printAnnualReportOnAction(ActionEvent actionEvent) {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("../view/reports/AnnualReport.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            ObservableList<ReportTM> items = tblAnnualReport.getItems();

            double totalIncome = 0;
            for (ReportTM tm : tmAnnualReport) {
                totalIncome += tm.getIncome();
            }

            String date = String.valueOf(datePicker.getValue());

            HashMap map = new HashMap();
            map.put("date", date);
            map.put("totalIncome", totalIncome);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(items.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void printCustomerWiseIncomeReportOnAction(ActionEvent actionEvent) {

        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("../view/reports/CustomerWiseReport.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            ObservableList<CustomerWiseIncomeReportTM> customers = tblCustomerWiseIncome.getItems();

            double totalIncome = 0;
            for (CustomerWiseIncomeReportTM tm : tmCustomerWiseIncomeReport) {
                totalIncome += tm.getIncome();
            }

            String date = String.valueOf(datePicker.getValue());

            HashMap map = new HashMap();
            map.put("date", date);
            map.put("totalIncome", totalIncome);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, new JRBeanArrayDataSource(customers.toArray()));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
