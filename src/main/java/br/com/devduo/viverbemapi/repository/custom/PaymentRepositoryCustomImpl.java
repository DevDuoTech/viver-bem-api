package br.com.devduo.viverbemapi.repository.custom;

import br.com.devduo.viverbemapi.enums.PaymentStatus;
import br.com.devduo.viverbemapi.enums.PaymentType;
import br.com.devduo.viverbemapi.models.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;
import java.util.List;

public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private static final String INITIAL =
            "SELECT p FROM Payment p ";
    private static final String LEFT_JOIN_TENANT =
            "LEFT JOIN p.tenant t ";
    private static final String WHERE =
            "WHERE 1=1 ";

    @Override
    public List<Payment> findAll(
            Pageable pageable, PaymentStatus status,
            YearMonth competency, PaymentType paymentType,
            Long tenantId
    ) {
        StringBuilder query = new StringBuilder(INITIAL);
        query.append(LEFT_JOIN_TENANT);

        StringBuilder where = new StringBuilder(WHERE);

        if (tenantId != null) {
            where.append("AND t.id = :tenantId ");
        }

        if (status != null) {
            where.append("AND p.paymentStatus = :status ");
        }

        if (paymentType != null) {
            where.append("AND p.paymentType = :type ");
        }

        if (competency != null) {
            where.append("AND FUNCTION('TO_CHAR', p.competency, 'MM/YYYY') = :competency ");
        }

        String orderField = "p.createdAt";
        String orderDirection = "DESC";

        if (!pageable.getSort().isUnsorted()) {
            var sort = pageable.getSort().iterator().next();
            orderField = "p." + sort.getProperty();
            orderDirection = sort.getDirection().name();
        }

        query.append(where)
                .append("ORDER BY ")
                .append(orderField).append(" ")
                .append(orderDirection);

        var jpqlQuery = entityManager.createQuery(query.toString(), Payment.class);

        if (tenantId != null) {
            jpqlQuery.setParameter("tenantId", tenantId);
        }

        if (status != null) {
            jpqlQuery.setParameter("status", status);
        }

        if (paymentType != null) {
            jpqlQuery.setParameter("type", paymentType);
        }

        if (competency != null) {
            String compString = String.format("%02d/%04d", competency.getMonthValue(), competency.getYear());
            jpqlQuery.setParameter("competency", compString);
        }

        jpqlQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        jpqlQuery.setMaxResults(pageable.getPageSize());

        return jpqlQuery.getResultList();
    }
}
