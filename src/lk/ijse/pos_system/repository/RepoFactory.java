package lk.ijse.pos_system.repository;

import lk.ijse.pos_system.repository.custom.impl.*;

public class RepoFactory {

    private static RepoFactory repoFactory;

    private RepoFactory(){}

    public static RepoFactory getRepoFactoryInstance(){
        return (repoFactory == null) ? repoFactory = new RepoFactory() : repoFactory;
    }

    public enum RepoTypes{
        CUSTOMER, ITEM, ORDER, ORDERDETAIL, DISCOUNT, USER, QUERY
    }

    public SuperRepo getRepo(RepoTypes type) {
        switch (type) {
            case CUSTOMER:
                return new CustomerRepoImpl();
            case ITEM:
                return new ItemRepoImpl();
            case ORDER:
                return new OrderRepoImpl();
            case ORDERDETAIL:
                return new OrderDetailRepoImpl();
            case DISCOUNT:
                return new DiscountRepoImpl();
            case QUERY:
                return new QueryRepoImpl();
            case USER:
                return new UserRepoImpl();
            default:
                return null;
        }
    }
}
