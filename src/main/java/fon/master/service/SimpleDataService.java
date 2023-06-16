package fon.master.service;

import fon.master.exception.DataNotFoundException;
import fon.master.exception.SimpleDataNotFoundException;
import fon.master.model.SimpleData;
import fon.master.repository.SimpleDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SimpleDataService {

    private final SimpleDataRepository simpleDataRepository;

    @Autowired
    public SimpleDataService(SimpleDataRepository simpleDataRepository) {
        this.simpleDataRepository = simpleDataRepository;
    }

    @PreAuthorize("hasAnyAuthority('WRITE', 'ROLE_WRITE')")
    public SimpleData addData(SimpleData simpleData) {
        if(simpleDataRepository.findById(simpleData.getId()).isPresent())
            throw new RuntimeException("Simple data already exists!");

        return simpleDataRepository.save(simpleData);
    }

    public SimpleData addData(String value) {
        return addData(new SimpleData(value));
    }

    public SimpleData findById(int id) {
        return simpleDataRepository.findById(id).orElseThrow(() -> new SimpleDataNotFoundException("Data with id "+id+" not found!"));
    }

    public List<SimpleData> findByValue(String value) {
        List<SimpleData> result = simpleDataRepository.findByValue(value);
        if(result.isEmpty())
            throw new SimpleDataNotFoundException("Simple data don't exist!");

        return result;
    }

    public List<SimpleData> findAll() {
        return simpleDataRepository.findAll();
    }

    @PreAuthorize("hasAuthority('DELETE')")
    public SimpleData deleteData(int id) {
        Optional<SimpleData> simpleData = simpleDataRepository.findById(id);

        if(simpleData.isEmpty())
            throw new SimpleDataNotFoundException("Simple data don't exist!");

        simpleDataRepository.delete(new SimpleData(id));

        return simpleData.get();
    }

    public int getMaxId() {
        return simpleDataRepository.getMaxId().orElseThrow(
                () -> new DataNotFoundException("No data present!")
        );
    }
}
