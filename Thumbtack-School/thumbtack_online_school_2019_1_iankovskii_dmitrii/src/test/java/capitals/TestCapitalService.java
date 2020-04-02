package capitals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCapitalService {

    @Test
    public void testGetCapitalsInfoNoMock(){
        CapitalService service = new CapitalService(new CapitalDao(), new RestcountriesConnector());

        List<Capital> capitalsExp = Arrays.asList(
                new Capital("London", "United Kingdom of Great Britain and Northern Ireland", "British pound"),
                new Capital("Moscow", "Russian Federation", "Russian ruble"),
                new Capital("Paris", "France", "Euro"),
                new Capital("Washington", "United States of America", "United States dollar")
        );

        List<Capital> capitalsAct = service.getCapitalsInfo();
        assertAll(
                ()-> assertEquals(capitalsExp.get(0).getName(), capitalsAct.get(0).getName()),
                ()-> assertEquals(capitalsExp.get(0).getCountry(), capitalsAct.get(0).getCountry()),
                ()-> assertEquals(capitalsExp.get(0).getCurrency(), capitalsAct.get(0).getCurrency())
                );
        assertAll(
                ()-> assertEquals(capitalsExp.get(1).getName(), capitalsAct.get(1).getName()),
                ()-> assertEquals(capitalsExp.get(1).getCountry(), capitalsAct.get(1).getCountry()),
                ()-> assertEquals(capitalsExp.get(1).getCurrency(), capitalsAct.get(1).getCurrency())
        );
        assertAll(
                ()-> assertEquals(capitalsExp.get(2).getName(), capitalsAct.get(2).getName()),
                ()-> assertEquals(capitalsExp.get(2).getCountry(), capitalsAct.get(2).getCountry()),
                ()-> assertEquals(capitalsExp.get(2).getCurrency(), capitalsAct.get(2).getCurrency())
        );
        assertAll(
                ()-> assertEquals(capitalsExp.get(3).getName(), capitalsAct.get(3).getName()),
                ()-> assertEquals(capitalsExp.get(3).getCountry(), capitalsAct.get(3).getCountry()),
                ()-> assertEquals(capitalsExp.get(3).getCurrency(), capitalsAct.get(3).getCurrency())
        );

    }

    @Test
    public void testGetCapitalsInfoWithMock(){
        CapitalDao dao = mock(CapitalDao.class);
        RestcountriesConnector connector = mock(RestcountriesConnector.class);
        CapitalService service = new CapitalService(dao, connector);

        List<String> names = Arrays.asList("London", "Paris");
        List<Capital> capitalsExp = Arrays.asList(
                new Capital(names.get(0), "England", "Pound"),
                new Capital(names.get(1), "France", "Euro")
        );
        when(dao.getCapitals()).thenReturn(names);
        when(connector.getCapitalsInfo(names)).thenReturn(capitalsExp);

        List<Capital> capitalsAct = service.getCapitalsInfo();
        assertAll(
                ()-> assertEquals(capitalsExp.get(1).getName(), capitalsAct.get(1).getName()),
                ()-> assertEquals(capitalsExp.get(1).getCountry(), capitalsAct.get(1).getCountry()),
                ()-> assertEquals(capitalsExp.get(1).getCurrency(), capitalsAct.get(1).getCurrency())
        );
        verify(dao, times(1)).getCapitals();
        verify(connector, times(1)).getCapitalsInfo(names);
    }
}
