package fon.master.model;

import javax.persistence.*;

@Entity
@Table(name = "simple_data")
public class SimpleData {

    @Id
    @GeneratedValue(generator = "simple_data_sequ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simple_data_sequ", sequenceName = "simple_data_sequ", initialValue = 1, allocationSize = 1)
    private int id;
    private String value;

    public SimpleData() {    }

    public SimpleData(int id) {
        this.id = id;
    }

    public SimpleData(String value) {
        this.value = value;
    }

    public SimpleData(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleData that = (SimpleData) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "SimpleData{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
