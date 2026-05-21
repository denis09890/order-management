package Model;

import java.time.LocalDateTime;

/**
 * Immutable Record model representing a generated order receipt (Bill).
 */
public record Bill (
        int bill_id,
        String client_name,
        String product_name,
        int quantity,
        LocalDateTime orderTime
){}