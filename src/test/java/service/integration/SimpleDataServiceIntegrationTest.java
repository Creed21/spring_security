package service.integration;

import fon.master.OAuthMain;
import fon.master.exception.SimpleDataNotFoundException;
import fon.master.model.SimpleData;
import fon.master.service.SimpleDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = OAuthMain.class)
public class SimpleDataServiceIntegrationTest {

    @Autowired
    SimpleDataService realService;

    @DisplayName("Integration test: find existing value")
    @Test
    public void findExistingValue() {
        String value = "testing_data";

//        SimpleData savedData = realService.addData(new SimpleData(1, value));
        assertEquals(2, realService.findById(2).getId());
        assertEquals(value, realService.findById(2).getValue());
    }

    @DisplayName("Integration test: find non existing value")
    @Test
    public void findNonExistingValue() {
        assertThrows(SimpleDataNotFoundException.class, () -> realService.findById(-99));
        assertThrows(SimpleDataNotFoundException.class, () -> realService.findByValue("123jasnd zx,mc12034u0891na"));
    }

    @DisplayName("Integration test: Delete data")
    @Test
    public void deleteData() {
        String value = "Data to delete";

        SimpleData savedData = realService.addData(new SimpleData(100, value));
        assertEquals(value, savedData.getValue());
        assertTrue(savedData.getId() > 0);

        realService.deleteData(savedData.getId());

        assertThrows(SimpleDataNotFoundException.class, () -> realService.findById(savedData.getId()));
    }

    @DisplayName("Integration test: Delete non existing data")
    @Test
    public void deleteNonExistingData() {
        assertThrows(SimpleDataNotFoundException.class, () -> realService.deleteData(999), "Data with id -99 not found!");
        assertThrows(SimpleDataNotFoundException.class, () -> realService.deleteData(0), "Data with id 0 not found!");
        assertThrows(SimpleDataNotFoundException.class, () -> realService.deleteData(-1), "Data with id -1 not found!");
    }
}
