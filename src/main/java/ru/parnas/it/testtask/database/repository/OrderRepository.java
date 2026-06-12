package ru.parnas.it.testtask.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.dictionary.OrderStatus;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") UUID id);

    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status WHERE o.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") OrderStatus status);

    @Query("""
        SELECT SUM(item.price * item.quantity)
        FROM OrderEntity o
        JOIN o.items item
        WHERE o.customerName = :customerName
    """)
    BigDecimal getTotalAmountByCustomerName(@Param("customerName") String customerName);
}
