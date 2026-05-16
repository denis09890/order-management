package BusinessLogic;

import BusinessLogic.Validators.ClientAgeValidator;
import BusinessLogic.Validators.EmailValidator;
import BusinessLogic.Validators.Validator;
import DataAccess.ClientDAO;
import Model.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ClientBLL {
    private List<Validator<Client>> validators;
    private ClientDAO clientDAO;

    public ClientBLL() {
        validators = new ArrayList<>();
        validators.add(new EmailValidator());
        validators.add(new ClientAgeValidator());
        clientDAO = new ClientDAO();
    }

    public Client findClientById(int id) {
        Client cl = clientDAO.findById(id);
        if (cl == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return cl;
    }

    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    public Client insertClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        return clientDAO.insert(client);
    }

    public void updateClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        clientDAO.update(client);
    }

    public void deleteClient(int id) {
        clientDAO.delete(id);
    }
}