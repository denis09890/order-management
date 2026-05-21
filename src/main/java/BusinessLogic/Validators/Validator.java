package BusinessLogic.Validators;

/**
 * Generic interface for object validation.
 * @param <T> The type of the object to validate.
 */
public interface Validator <T>{
    /**
     * Validates the specified object.
     * @param t The object to validate.
     */
    public void validate(T t);
}