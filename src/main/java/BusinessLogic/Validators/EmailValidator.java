package BusinessLogic.Validators;

import Model.Client;

/**
 * Validates the email format.
 */
public class EmailValidator implements Validator <Client>{
    /**
     * Validates the email address using regex.
     * @param t The client to validate.
     */
    public void validate(Client t) {
        if (!t.getEmail().matches("^(.+)@(.+)$")) {
            throw new IllegalArgumentException("The email is not valid!");
        }
    }
}