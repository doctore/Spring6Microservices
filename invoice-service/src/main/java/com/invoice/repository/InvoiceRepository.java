package com.invoice.repository;

import com.invoice.model.Invoice;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Logger;

@Repository
public interface InvoiceRepository extends ExtendedJpaRepository<Invoice, Integer> {

    Logger LOG = Logger.getLogger(InvoiceRepository.class.getName());

    // Columns defined in the table of database
    String COST_COLUMN = "cost";


    /**
     * Gets the {@link Invoice}s whose cost is among those provided.
     *
     * @param costGreaterOrEqual
     *    Lower limit to compare {@link Invoice#getCost()}
     * @param costLessOrEqual
     *    Upper limit to compare {@link Invoice#getCost()}
     *
     * @return {@link Invoice}s with cost greater than or equal to {@code costGreaterOrEqual} and
     *         less than or equal to {@code costLessOrEqual}
     */
    default List<Invoice> findByCostRange(final double costGreaterOrEqual,
                                          final double costLessOrEqual) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Invoice> criteriaQuery = criteriaBuilder.createQuery(
                Invoice.class
        );
        Root<Invoice> invoiceRoot = criteriaQuery.from(
                Invoice.class
        );
        Predicate costPredicate = criteriaBuilder.and(
                criteriaBuilder.ge(
                        invoiceRoot.get(COST_COLUMN),
                        costGreaterOrEqual
                ),
                criteriaBuilder.le(
                        invoiceRoot.get(COST_COLUMN),
                        costLessOrEqual
                )
        );
        criteriaQuery.where(costPredicate);
        criteriaQuery.orderBy(
                criteriaBuilder.asc(
                        invoiceRoot.get(COST_COLUMN)
                )
        );
        TypedQuery<Invoice> query = getEntityManager()
                .createQuery(
                        criteriaQuery
                );
        LOG.info(
                getHQLQuery(query)
        );
        return query.getResultList();
    }

}