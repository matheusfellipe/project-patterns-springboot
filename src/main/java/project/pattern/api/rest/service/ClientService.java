package project.pattern.api.rest.service;

import project.pattern.api.rest.model.Client;


public interface ClientService {

	Iterable<Client> searchAll();

	Client searchById(Long id);

	void insert(Client client);

	void update(Long id, Client client);

	void delete(Long id);

}
