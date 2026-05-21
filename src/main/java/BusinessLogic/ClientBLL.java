package BusinessLogic;

import BusinessLogic.Validators.ClientAgeValidator;
import BusinessLogic.Validators.EmailValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.ClientDAO;
import Model.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business Logic Layer for Client operations.
 */
public class ClientBLL {
    private List<Validator<Client>> validators;
    private ClientDAO clientDAO;

    public ClientBLL() {
        validators = new ArrayList<>();
        validators.add(new EmailValidator());
        validators.add(new ClientAgeValidator());
        clientDAO = new ClientDAO();
    }

    /**
     * Finds a client by their unique ID.
     * @param id The ID of the client.
     * @return The found Client object.
     */
    public Client findClientById(int id) {
        Client cl = clientDAO.findById(id);
        if (cl == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return cl;
    }

    /**
     * Retrieves all clients from the database.
     * @return A list of all clients.
     */
    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    /**
     * Validates and inserts a new client.
     * @param client The client data to insert.
     * @return The inserted Client object.
     */
    public Client insertClient(Client client) {
        validators.forEach(v -> v.validate(client));
        return clientDAO.insert(client);
    }

    /**
     * Validates and updates an existing client.
     * @param client The client data to update.
     */
    public void updateClient(Client client) {
        validators.forEach(v -> v.validate(client));
        clientDAO.update(client);
    }

    /**
     * Deletes a client from the database by ID.
     * @param id The ID of the client to delete.
     */
    public void deleteClient(int id) {
        clientDAO.delete(id);
    }
}