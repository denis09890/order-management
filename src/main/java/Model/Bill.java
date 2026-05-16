package Model;

import java.time.LocalDateTime;

public record Bill (
    int bill_id,
    String client_name,
    String product_name,
    int quantity,
    LocalDateTime orderTime
){}
