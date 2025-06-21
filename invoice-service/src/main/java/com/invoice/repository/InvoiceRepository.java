package com.invoice.repository;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Logger;

@Repository
public interface InvoiceRepository extends ExtendedJpaRepository<Invoice, Integer> {

    Logger LOG = Logger.getLogger(InvoiceRepository.class.getName());

    /**
     * Gets the {@link Invoice}s whose cost is among those provided.
     *
     * @apiNote
     *    If {@code costGreaterOrEqual} and {@code costLessOrEqual} are {@code null} then all the {@link Invoice}s will
     * be returned.
     *
     * @param costGreaterOrEqual
     *    Lower limit to compare {@link Invoice#getCost()}
     * @param costLessOrEqual
     *    Upper limit to compare {@link Invoice#getCost()}
     *
     * @return {@link Invoice}s with cost greater than or equal to {@code costGreaterOrEqual} and
     *         less than or equal to {@code costLessOrEqual}
     */
    default List<Invoice> findByCostRange(final Double costGreaterOrEqual,
                                          final Double costLessOrEqual) {
        if (null == costGreaterOrEqual && null == costLessOrEqual) {
            return findAll();
        }
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Invoice> criteriaQuery = criteriaBuilder.createQuery(
                Invoice.class
        );
        Root<Invoice> invoiceRoot = criteriaQuery.from(
                Invoice.class
        );
        Predicate costPredicate;
        if (null != costGreaterOrEqual && null != costLessOrEqual) {
            costPredicate = criteriaBuilder.and(
                    criteriaBuilder.ge(
                            invoiceRoot.get(
                                    Invoice.COST_COLUMN
                            ),
                            costGreaterOrEqual
                    ),
                    criteriaBuilder.le(
                            invoiceRoot.get(
                                    Invoice.COST_COLUMN
                            ),
                            costLessOrEqual
                    )
            );
        }
        else if (null == costGreaterOrEqual) {
            costPredicate = criteriaBuilder.le(
                    invoiceRoot.get(
                            Invoice.COST_COLUMN
                    ),
                    costLessOrEqual
            );
        }
        else {
            costPredicate = criteriaBuilder.ge(
                    invoiceRoot.get(
                            Invoice.COST_COLUMN
                    ),
                    costGreaterOrEqual
            );
        }
        criteriaQuery.where(
                costPredicate
        );
        criteriaQuery.orderBy(
                criteriaBuilder.asc(
                        invoiceRoot.get(
                                Invoice.COST_COLUMN
                        )
                )
        );
        TypedQuery<Invoice> query = getEntityManager()
                .createQuery(
                        criteriaQuery
                );
        LOG.info(
                getHQLQuery(
                        query
                )
        );
        return query.getResultList();
    }


    /**
     *    Gets paged all the {@link Invoice}s with their {@link Customer}s using the given {@link Pageable}
     * to configure the required one.
     *
     * @apiNote
     *    In this case, the pagination will do in database, not in memory as provided by default in:
     *    {@link PagingAndSortingRepository#findAll(Pageable)}.
     *
     * @param pageable
     *    {@link Pageable} with the desired page to get
     *
     * @return {@link Page} of {@link Invoice}
     */
    default Page<Invoice> findAllNoMemoryPagination(@Nullable final Pageable pageable) {
        if (null == pageable) {
            return new PageImpl<>(
                 findAll()
            );
        }
        int rankInitial = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        int rankFinal = rankInitial + pageable.getPageSize() - 1;

        String orderByClause = (null == pageable.getSort() || pageable.getSort().isUnsorted())
                ? Invoice.ID_COLUMN + " desc "
                : buildRawOrder(
                        pageable.getSort()
                  );

        final String query = "select i_c.* "
                + "           from (select *, dense_rank() over (order by " + orderByClause + ") rank "
                + "                 from (select i." + Invoice.ID_COLUMN
                + "                                   ,i." + Invoice.CODE_COLUMN
                + "                                   ,i." + Invoice.ORDER_ID_COLUMN
                + "                                   ,i." + Invoice.COST_COLUMN
                + "                                   ,i." + Invoice.CREATED_AT_COLUMN
                + "                             ,c." + Customer.ID_COLUMN + " " + PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.ID_COLUMN
                + "                                   ,c." + Customer.CODE_COLUMN + " " + PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.CODE_COLUMN
                + "                                   ,c." + Customer.ADDRESS_COLUMN + " " + PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.ADDRESS_COLUMN
                + "                                   ,c." + Customer.PHONE_COLUMN + " " + PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.PHONE_COLUMN
                + "                                   ,c." + Customer.EMAIL_COLUMN + " " + PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.EMAIL_COLUMN
                + "                                   ,c." + Customer.CREATED_AT_COLUMN + " " + PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.CREATED_AT_COLUMN
                + "                       from " + PersistenceConfiguration.SCHEMA + "." + PersistenceConfiguration.TABLE.INVOICE + " i "
                + "                       join " + PersistenceConfiguration.SCHEMA + "." + PersistenceConfiguration.TABLE.CUSTOMER + " c on c.id = i.customer_id "
                + "                       order by " + orderByClause
                + "                ) "
                + "           ) i_c "
                + "where i_c.rank between :rankInitial and :rankFinal";

        LOG.info(
                query
        );
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = getEntityManager().createNativeQuery(
                query,
                Invoice.INVOICE_CUSTOMER_MAPPING
        )
        .setParameter(
                "rankInitial",
                rankInitial
        )
        .setParameter(
                "rankFinal",
                rankFinal
        )
        .getResultList();

        return new PageImpl<>(
                CollectionUtil.map(
                        rawResults,
                        array -> (Invoice) array[0]
                ),
                pageable,
                this.count()
        );
    }

}