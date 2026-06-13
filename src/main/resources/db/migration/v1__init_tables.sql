CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        customer_name VARCHAR(255) NOT NULL,
                        order_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                        status VARCHAR(50) NOT NULL
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             product_name VARCHAR(255) NOT NULL,
                             quantity INTEGER NOT NULL,
                             price NUMERIC(10, 2) NOT NULL,
                             order_id UUID NOT NULL,

                             CONSTRAINT fk_order_items_orders
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders (id)
                                     ON DELETE CASCADE
);

CREATE INDEX idx_orders_status ON orders(status);

CREATE INDEX idx_orders_customer_name ON orders(customer_name);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);