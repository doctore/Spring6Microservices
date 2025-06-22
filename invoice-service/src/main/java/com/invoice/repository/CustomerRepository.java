package com.invoice.repository;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import com.invoice.model.Customer;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public interface CustomerRepository extends ExtendedJpaRepository<Customer, Integer> {

    Logger LOG = Logger.getLogger(CustomerRepository.class.getName());


    /**
     * Gets paged all the {@link Customer}s using the given {@link Pageable} to configure the required one.
     *
     * @apiNote
     *    In this case, the pagination will do in database, not in memory as provided by default in:
     *    {@link PagingAndSortingRepository#findAll(Pageable)}.
     *
     * @param pageable
     *    {@link Pageable} with the desired page to get
     *
     * @return {@link Page} of {@link Customer}
     */
    default Page<Customer> findAllNoMemoryPagination(@Nullable final Pageable pageable) {
        if (null == pageable) {
            return new PageImpl<>(
                    findAll()
            );
        }
        int rankInitial = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        int rankFinal = rankInitial + pageable.getPageSize() - 1;

        String orderByClause = (null == pageable.getSort() || pageable.getSort().isUnsorted())
                ? Customer.ID_COLUMN + " desc "
                : buildRawOrder(
                        pageable.getSort()
                  );

        final String query = "select c.* "
                + "           from (select *, dense_rank() over (order by " + orderByClause + ") rank "
                + "                 from (select * "
                + "                       from " + PersistenceConfiguration.SCHEMA + "." + PersistenceConfiguration.TABLE.CUSTOMER
                + "                       order by " + orderByClause
                + "                ) "
                + "           ) c "
                + "where c.rank between :rankInitial and :rankFinal";

        LOG.info(
                query
        );

        @SuppressWarnings("unchecked")
        List<Customer> rawResults = getEntityManager().createNativeQuery(
                        query,
                        Customer.class
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
                rawResults,
                pageable,
                this.count()
        );
    }


    /**
     *    Returns an {@link Optional} with the {@link Customer} if there is one which {@link Customer#getCode()}
     * matches with {@code code}, {@link Optional#empty()} otherwise.
     *
     * @param code
     *    {@link Customer#getCode()} to find
     *
     * @return {@link Optional} with the {@link Customer} which code matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    Optional<Customer> findByCode(@Nullable final String code);

}
