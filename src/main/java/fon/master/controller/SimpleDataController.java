package fon.master.controller;

import fon.master.config.securityAnnotations.IsAdmin;
import fon.master.config.securityAnnotations.IsUser;
import fon.master.model.SimpleData;
import fon.master.service.SimpleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simpledata")
public class SimpleDataController {

    @Autowired
    private SimpleDataService simpleDataService;

    @IsUser
    @GetMapping("/{id}")
    public SimpleData findById(@PathVariable("id") int id) {
        return simpleDataService.findById(id);
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping("/add")
    public SimpleData addData(@RequestParam("value") String value) {
        return simpleDataService.addData(value);
    }

    @IsUser
    @GetMapping("/getAll")
    public List<SimpleData> getAllData() {
        return simpleDataService.findAll();
    }

    @IsAdmin
    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/delete")
    public String deleteData(@RequestParam("id") int id)  {
        simpleDataService.deleteData(id);
        return "Data has been deleted!";
    }
}
