package impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Client;
import model.ClientRepository;
import model.Address;
import model.AddressRepository;
import service.ClientService;
import service.ViaCepService;


@Service
public class ClientServiceImpl implements ClientService {

	
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	

	@Override
	public Iterable<Client> searchAll() {
		// Buscar todos os Clientes.
		return clientRepository.findAll();
	}

	@Override
	public Client searchById(Long id) {
		// Buscar Cliente por ID.
		Optional<Client> cliente = clientRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void insert(Client client) {
		salveClientWithCep(client);
	}

	@Override
	public void update(Long id, Client client) {
		// Buscar Cliente por ID, caso exista:
		Optional<Client> clienteBd = clientRepository.findById(id);
		if (clienteBd.isPresent()) {
			salveClientWithCep(client);
		}
	}

	@Override
	public void delete(Long id) {
		// Deletar Cliente por ID.
		clientRepository.deleteById(id);
	}

	private void salveClientWithCep(Client cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getAddress().getCep();
		Address address = addressRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Address newAddress = viaCepService.consultarCep(cep);
			addressRepository.save(newAddress);
			return newAddress;
		});
		cliente.setAddress(address);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clientRepository.save(cliente);
	}

}
