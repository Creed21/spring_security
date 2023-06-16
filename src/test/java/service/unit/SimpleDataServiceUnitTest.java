package service.unit;

import fon.master.OAuthMain;
import fon.master.exception.SimpleDataNotFoundException;
import fon.master.model.SimpleData;
import fon.master.service.SimpleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;


@SpringBootTest(classes = {OAuthMain.class})
public class SimpleDataServiceUnitTest {

    @Mock
    private SimpleDataService simpleDataService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addValue() {
        when(simpleDataService.addData(new SimpleData("Testing insert new value")))
                .thenReturn(new SimpleData(1,"Testing insert new value"));
    }

    @Test
    public void findExistingValue() {
        SimpleData simpleData = new SimpleData("Testing insert new value");

        simpleDataService.addData(simpleData);
        when(simpleDataService.findById(1))
                .thenReturn(simpleData);
    }

    @Test
    void findNotExistingValue() {
        when(simpleDataService.findById(-99))
                .thenThrow(SimpleDataNotFoundException.class);

        when(simpleDataService.findByValue("123jnkfcsfdig-3ir23n 4lcxvu9i9034"))
                .thenThrow(SimpleDataNotFoundException.class);

    }

    @Test
    void deleteData() {
        // Create a sample SimpleData object
        SimpleData savedData = new SimpleData("Data to be deleted");

        // Stub the addData method
        when(simpleDataService.addData("Data to be deleted"))
                .thenReturn(savedData);

        // Call the addData method to obtain the saved data
        SimpleData addedData = simpleDataService.addData("Data to be deleted");
        System.out.println("saved data: " + addedData);

        // Stub the findById method
        when(simpleDataService.findById(1))
                .thenReturn(savedData);

        // Delete the data
        simpleDataService.deleteData(savedData.getId());

        // Stub the findById method to throw an exception
        when(simpleDataService.findById(savedData.getId()))
                .thenThrow(SimpleDataNotFoundException.class);
    }

}
