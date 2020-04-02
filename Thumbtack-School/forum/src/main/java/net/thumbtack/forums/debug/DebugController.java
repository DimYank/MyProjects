package net.thumbtack.forums.debug;

import net.thumbtack.forums.error.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug/")
public class DebugController {

    private DebugService debugService;

    @Autowired
    public DebugController(DebugService debugService) {
        this.debugService = debugService;
    }

    @PostMapping("clear")
    public void clearDataBase() throws ServerException {
        debugService.clearDataBase();
    }
}
