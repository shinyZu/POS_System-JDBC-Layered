package lk.ijse.pos_system.business;

import lk.ijse.pos_system.business.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getBOFactoryInstance() {
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes{
        ITEM, MANAGE_ORDER, PURCHASE_ORDER, REPORT, VERIFY_USER
    }

    public SuperBO getBO(BOTypes type) {
        switch (type) {
            case ITEM:
                return new ItemBOImpl();
            case MANAGE_ORDER:
                return new ManageOrderBOImpl();
            case PURCHASE_ORDER:
                return new PurchaseOrderBOImpl();
            case REPORT:
                return new ReportBOImpl();
            case VERIFY_USER:
                return new VerifyUserBOImpl();
            default:
                return null;
        }
    }
}
