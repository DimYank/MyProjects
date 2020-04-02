package capitals;

import java.util.List;

public class CapitalService {

    private CapitalDao dao;
    private RestcountriesConnector connector;

    public CapitalService(CapitalDao dao, RestcountriesConnector connector) {
        this.dao = dao;
        this.connector = connector;
    }

    public List<Capital> getCapitalsInfo(){
        List<String> names = dao.getCapitals();
        return connector.getCapitalsInfo(names);
    }
}
