package com.bms.observer.overdraft;

import com.bms.domain.entity.OverdraftEvent;
import com.bms.persistence.ConfiguredDAOFactory;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.OverdraftEventDAO;

public class DatabaseOverdraftObserver implements OverdraftObserver {
    private final OverdraftEventDAO overdraftEventDAO;

    public DatabaseOverdraftObserver() {
        this(ConfiguredDAOFactory.getInstance());
    }

    public DatabaseOverdraftObserver(DAOFactory factory) {
        this.overdraftEventDAO = factory.createOverdraftEventDAO();
    }

    @Override
    public void onOverdraft(OverdraftEvent event) {
        int overdraftId = overdraftEventDAO.insert(event);
        if (overdraftId > 0) {
            event.setOverdraftId(overdraftId);
        }
    }
}
