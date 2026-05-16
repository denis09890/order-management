package BusinessLogic.Validators;

import Model.Client;

public class EmailValidator implements Validator <Client>{
    public void validate(Client t) {
        if (!t.getEmail().matches("^(.+)@(.+)$")) {
            throw new IllegalArgumentException("The email is not valid!");
        }
    }
}