package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.ItemRepo;
import lk.ijse.pos_system.repository.custom.QueryRepo;
import lk.ijse.pos_system.dto.CustomDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryRepoImpl implements QueryRepo {

    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);

    @Override
    public ArrayList<CustomDTO> getCustomerWiseIncome(String date) throws SQLException, ClassNotFoundException {
        ArrayList<CustomDTO> custIncomeTable = new ArrayList<>();

        ResultSet rst = CrudUtil.executeQuery("SELECT o.custID, c.custTitle, c.custName, c.city, SUM(o.orderCost)\n" +
                "FROM Orders o INNER JOIN Customer c\n" +
                "ON c.custID = o.custID\n" +
                "WHERE o.orderDate = ?\n" +
                "GROUP BY custID", date
        );

        while (rst.next()) {
            custIncomeTable.add(new CustomDTO(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getDouble(5)
            ));
        }
        return custIncomeTable;
    }

    @Override
    public ArrayList<CustomDTO> getOrderedItems(String orderSelected, CustomDTO itemSelected) throws SQLException, ClassNotFoundException {
        ArrayList<CustomDTO> listOfOrderedItems = new ArrayList<>();

        int discountAsPercentage = 0;

        if (itemSelected != null) {

            ResultSet rst1 = CrudUtil.executeQuery("SELECT discount FROM Discount WHERE itemCode = ?", itemSelected.getItemCode());
            if (rst1.next()) {
                discountAsPercentage = rst1.getInt(1);
            }
        }

        ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, i.packSize, i.unitPrice, od.orderQTY, od.discount\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "where orderId = ?", orderSelected);

        double discount = 0;

        while (rst2.next()) {

            int packSize = itemRepo.splitPackSize(rst2.getString(1), rst2.getString(3));
            double unitPrice = rst2.getDouble(4);
            int orderQty = rst2.getInt(5);
            double subTotal = unitPrice * packSize * orderQty;

            if (discountAsPercentage != 0) {
                discount = subTotal * discountAsPercentage / 100;
            } else {
                discount = rst2.getDouble(6);
            }

            double total = subTotal - discount;

            listOfOrderedItems.add(new CustomDTO(
                    rst2.getString(1),
                    rst2.getString(2),
                    rst2.getString(3),
                    unitPrice,
                    orderQty,
                    subTotal,
                    discount,
                    total
            ));
        }
        return listOfOrderedItems;
    }

    @Override
    public ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {
        ArrayList<Double> oldPaymentInfo = new ArrayList<>();

        double orderSubtotal = 0;
        double orderDiscount = 0;
        double orderTotal = 0;

        double unitPrice = 0;
        int packSize = 0;
        int orderQty = 0;

        ResultSet rst = CrudUtil.executeQuery("SELECT od.orderId, i.itemCode, i.unitPrice, i.packSize, od.orderQTY, od.discount\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "WHERE od.orderID = ? AND i.itemCode IN (SELECT itemCode FROM OrderDetail WHERE orderId = ?)", orderSelected, orderSelected);

        while (rst.next()) {

            unitPrice = rst.getDouble(3);
            packSize = Integer.parseInt(rst.getString(4).split(" ")[0]);
            orderQty = rst.getInt(5);

            orderDiscount += rst.getDouble(6);
            orderSubtotal += (unitPrice * packSize * orderQty);
            orderTotal += (orderSubtotal - orderDiscount);

        }

        ResultSet rst2 = CrudUtil.executeQuery("SELECT orderCost FROM Orders WHERE orderId = ?", orderSelected);

        if (rst2.next()) {
            orderTotal = rst2.getDouble(1);
        }

        oldPaymentInfo.add(0, orderSubtotal);
        oldPaymentInfo.add(1, orderDiscount);
        oldPaymentInfo.add(2, orderTotal);

        return oldPaymentInfo;
    }

    @Override
    public ArrayList<CustomDTO> getAllItems() throws SQLException, ClassNotFoundException {  // INNER JOIN

        ResultSet rst = CrudUtil.executeQuery("SELECT i.itemCode, i.description,i.packSize,i.unitPrice,i.qtyOnHand,d.discount\n" +
                "FROM Item i LEFT JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode;");

        ArrayList<CustomDTO> itemsWithDiscountList = new ArrayList<>();

        while (rst.next()) {
            itemsWithDiscountList.add(new CustomDTO(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5),
                    rst.getDouble(6)
            ));
        }
        return itemsWithDiscountList;
    }

    @Override
    public ArrayList<CustomDTO> getDailyReport(String date) throws SQLException, ClassNotFoundException {
        ArrayList<CustomDTO> dailyReportTable = new ArrayList<>();

        ResultSet rst1 = CrudUtil.executeQuery("SELECT i.itemCode, i.unitPrice, i.packSize,  SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "WHERE o.orderDate = ?\n" +
                "GROUP BY od.itemCode\n" +
                "ORDER BY od.itemCode", date);

            ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, SUM(i.unitPrice * i.packSize * od.orderQTY * (100-d.discount) / 100)\n" +
                    "FROM Item i LEFT JOIN Discount d\n" +
                    "ON i.itemCode = d.itemCode\n" +
                    "INNER JOIN OrderDetail od\n" +
                    "ON i.itemCode = od.itemCode\n" +
                    "INNER JOIN Orders o\n" +
                    "ON o.orderID = od.orderId\n" +
                    "WHERE o.orderDate = ?\n" +
                    "GROUP BY i.itemCode\n" +
                    "ORDER BY i.itemCode", date);

        while (rst2.next()) {
            if (rst1.next()) {
                if (rst1.getString(1).equals(rst2.getString(1))) {
                    // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                    String itemCode = rst2.getString(1);
                    String description = rst2.getString(2);
                    int qtySold = rst1.getInt(4);
                    double income = rst2.getDouble(3);

                    double unitPrice = rst1.getDouble(2);
                    int packSize = itemRepo.splitPackSize(itemCode,rst1.getString(3));
                    int orderQTY = rst1.getInt(4);

                    if (income == 0) {
                        income = unitPrice * packSize * orderQTY;
                    }

                    dailyReportTable.add(new CustomDTO(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return dailyReportTable;
    }

    @Override
    public ArrayList<CustomDTO> getMonthlyReport(String date) throws SQLException, ClassNotFoundException {
        String str = String.valueOf(date);
        String[] arrOfStr = str.split("-");
        String d = arrOfStr[1];

        ArrayList<CustomDTO> monthlyReportTable = new ArrayList<>();
        ResultSet rst1 = CrudUtil.executeQuery("SELECT i.itemCode, i.unitPrice, i.packSize, SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where orderDate LIKE '"+"____-"+d+"-__'\n"+
                "GROUP BY od.itemCode\n" +
                "ORDER BY od.itemCode");

        ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, SUM(i.unitPrice * i.packSize * od.orderQTY * (100-d.discount) / 100)\n" +
                "FROM Item i LEFT JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode\n" +
                "INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where orderDate LIKE '"+"____-"+d+"-__'\n" +
                "GROUP BY i.itemCode\n" +
                "ORDER BY i.itemCode");

        while (rst2.next()) {
            if (rst1.next()) {
                if (rst1.getString(1).equals(rst2.getString(1))) {
                    // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                    String itemCode = rst2.getString(1);
                    String description = rst2.getString(2);
                    int qtySold = rst1.getInt(4);
                    double income = rst2.getDouble(3);

                    double unitPrice = rst1.getDouble(2);
                    int packSize = itemRepo.splitPackSize(itemCode,rst1.getString(3));
                    int orderQTY = rst1.getInt(4);

                    if (income == 0) {
                        income = unitPrice * packSize * orderQTY;
                    }

                    monthlyReportTable.add(new CustomDTO(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return monthlyReportTable;
    }

    @Override
    public ArrayList<CustomDTO> getAnnualReport(String date) throws SQLException, ClassNotFoundException {
        String str = String.valueOf(date);
        String[] arrOfStr = str.split("-");
        String yy = arrOfStr[0];

        ArrayList<CustomDTO> annualReportTable = new ArrayList<>();
        ResultSet rst1 = CrudUtil.executeQuery("SELECT i.itemCode, i.unitPrice, i.packSize, SUM(od.orderQTY)\n" +
                "FROM Item i INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where orderDate LIKE '"+yy+"-__-__'\n" +
                "GROUP BY od.itemCode\n" +
                "ORDER BY od.itemCode");

        ResultSet rst2 = CrudUtil.executeQuery("SELECT i.itemCode, i.description, SUM(orderCost) \n" +
                "FROM Item i LEFT JOIN Discount d\n" +
                "ON i.itemCode = d.itemCode\n" +
                "INNER JOIN OrderDetail od\n" +
                "ON i.itemCode = od.itemCode\n" +
                "INNER JOIN Orders o\n" +
                "ON o.orderID = od.orderId\n" +
                "where o.orderDate LIKE '"+yy+"-__-__'\n" +
                "GROUP BY i.itemCode\n" +
                "ORDER BY i.itemCode");

        while (rst2.next()) {
            if (rst1.next()) {
                if (rst1.getString(1).equals(rst2.getString(1))) {
                    // dailyReportTable.add(rst2.getString(1), rst2.getString(2), rst1.getInt(2),rst2.getDouble(3));
                    String itemCode = rst2.getString(1);
                    String description = rst2.getString(2);
                    int qtySold = rst1.getInt(4);
                    double income = rst2.getDouble(3);

                    double unitPrice = rst1.getDouble(2);
                    int packSize = itemRepo.splitPackSize(itemCode,rst1.getString(3));
                    int orderQTY = rst1.getInt(4);

                    if (income == 0) {
                        income = unitPrice * packSize * orderQTY;
                    }

                    annualReportTable.add(new CustomDTO(
                            itemCode,
                            description,
                            qtySold,
                            income
                    ));
                }
            }
        }
        return annualReportTable;
    }

    @Override
    public String getMostMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException {
        String mostMovableItem = "I-000";

        String[] arrOfStr = date.split("-");
        String mm = null;
        String yy = null;

        ResultSet rst = null;

        if (reportType.equals("Daily Report")) {
            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate = ? \n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) DESC LIMIT 1;",date);

        } else if (reportType.equals("Monthly Report")) {
            mm = arrOfStr[1];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+"____-"+mm+"-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) DESC LIMIT 1;");

        } else if (reportType.equals("Annual Report")) {
            yy = arrOfStr[0];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+yy+"-__-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) DESC LIMIT 1;");
        }

        if (rst.next()) {
            mostMovableItem = rst.getString(1);
            return mostMovableItem;
        }
        return mostMovableItem;
    }

    @Override
    public String getLeastMovableItem(String reportType, String date) throws SQLException, ClassNotFoundException {
        String leastMovableItem = "I-000";

        String[] arrOfStr = date.split("-");
        String mm = null;
        String yy = null;

        ResultSet rst = null;

        if (reportType.equals("Daily Report")) {
            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate = ? \n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) \n" +
                    "ASC LIMIT 1;",date);

        } else if (reportType.equals("Monthly Report")) {
            mm = arrOfStr[1];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+"____-"+mm+"-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) \n" +
                    "ASC LIMIT 1;");

        } else if (reportType.equals("Annual Report")) {
            yy = arrOfStr[0];

            rst = CrudUtil.executeQuery("SELECT DISTINCT od.itemCode, count(od.itemCode), SUM(od.orderQty) \n" +
                    "FROM OrderDetail od INNER JOIN Orders o\n" +
                    "ON od.orderId = o.orderID\n" +
                    "WHERE o.orderDate LIKE '"+yy+"-__-__'\n" +
                    "GROUP BY od.itemCode\n" +
                    "ORDER BY SUM(od.orderQty) \n" +
                    "ASC LIMIT 1;");
        }

        if (rst.next()) {
            leastMovableItem = rst.getString(1);
            return leastMovableItem;
        }
        return leastMovableItem;
    }

}
