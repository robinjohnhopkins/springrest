package theredredrobin.com.springrest.services;


import org.springframework.data.repository.CrudRepository;
import theredredrobin.com.springrest.model.Address;

public interface AddressService extends CrudRepository<Address, Integer> {
}
