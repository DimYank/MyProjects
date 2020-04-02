package net.thumbtack.forums.debug;

import net.thumbtack.forums.database.dao.iface.CommonDao;
import net.thumbtack.forums.error.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DebugService {

    private CommonDao commonDao;

    @Autowired
    public DebugService(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    public void clearDataBase() throws ServerException {
        commonDao.clearDB();
    }
}
