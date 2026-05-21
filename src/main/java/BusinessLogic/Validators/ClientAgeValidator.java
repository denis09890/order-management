package BusinessLogic.Validators;

import Model.Client;

/**
 * Validates the age of a client.
 */
public class ClientAgeValidator implements Validator<Client>  {
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 100;

    /**
     * Checks if the client's age is within the legal range.
     * @param t The client to validate.
     */
    public void validate(Client t) {
        if (t.getAge() < MIN_AGE || t.getAge() > MAX_AGE) {
            throw new IllegalArgumentException("Client s age must be between " + MIN_AGE + " and " + MAX_AGE + "!");
        }
    }
}