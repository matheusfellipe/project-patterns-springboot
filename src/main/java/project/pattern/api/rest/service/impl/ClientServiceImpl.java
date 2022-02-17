package project.pattern.api.rest.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.pattern.api.rest.model.Address;
import project.pattern.api.rest.model.AddressRepository;
import project.pattern.api.rest.model.Client;
import project.pattern.api.rest.model.ClientRepository;
import project.pattern.api.rest.service.ClientService;
import project.pattern.api.rest.service.ViaCepService;

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
		saveClientWithCep(client);
	}

	@Override
	public void update(Long id, Client client) {
		// Buscar Cliente por ID, caso exista:
		Optional<Client> clienteBd = clientRepository.findById(id);
		if (clienteBd.isPresent()) {
			saveClientWithCep(client);
		}
	}

	@Override
	public void delete(Long id) {
		// Deletar Cliente por ID.
		clientRepository.deleteById(id);
	}

	private void saveClientWithCep(Client cliente) {
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
