package br.com.devduo.viverbemapi.repository.custom;

import br.com.devduo.viverbemapi.dtos.PaymentPendingDTO;
import br.com.devduo.viverbemapi.dtos.PaymentSummaryDTO;
import br.com.devduo.viverbemapi.enums.PaymentStatus;
import br.com.devduo.viverbemapi.enums.PaymentType;
import br.com.devduo.viverbemapi.models.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        setParams(status, competency, paymentType, tenantId, jpqlQuery);

        jpqlQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        jpqlQuery.setMaxResults(pageable.getPageSize());

        return jpqlQuery.getResultList();
    }

    @Override
    public List<PaymentPendingDTO> getPendingSummary(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);         // primeiro dia do mês
            endDate = now.withDayOfMonth(now.lengthOfMonth()); // último dia do mês
        }

        StringBuilder jpql = new StringBuilder("""
                    SELECT new br.com.devduo.viverbemapi.dtos.PaymentPendingDTO(
                        t.id,
                        t.name,
                        SUM(p.value),
                        COUNT(p)
                    )
                    FROM Payment p
                    JOIN p.tenant t
                    WHERE p.paymentStatus = 'PAYABLE'
                """);

        if (startDate != null) {
            jpql.append(" AND p.competency >= :startDate ");
        }

        if (endDate != null) {
            jpql.append(" AND p.competency <= :endDate ");
        }

        jpql.append(" GROUP BY t.id, t.name ORDER BY t.name ASC");

        TypedQuery<PaymentPendingDTO> query =
                entityManager.createQuery(jpql.toString(), PaymentPendingDTO.class);

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return query.getResultList();
    }

    @Override
    public List<PaymentSummaryDTO> getSummaryPayments(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            YearMonth today = YearMonth.now();
            YearMonth start = today.minusMonths(11);

            startDate = start.atDay(1);
            endDate = today.atEndOfMonth();
        }

        String jpql = """
                    SELECT FUNCTION('TO_CHAR', p.competency, 'YYYY-MM') AS comp,
                           SUM(p.value) AS total
                    FROM Payment p
                    WHERE p.competency BETWEEN :startDate AND :endDate
                      AND p.paymentStatus = br.com.devduo.viverbemapi.enums.PaymentStatus.PAID
                    GROUP BY FUNCTION('TO_CHAR', p.competency, 'YYYY-MM')
                    ORDER BY comp
                """;

        var list = entityManager.createQuery(jpql, Object[].class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        Map<String, Double> summary = new HashMap<>();
        for (Object[] row : list) {
            String month = (String) row[0];
            Double total = (Double) row[1];
            summary.put(month, total);
        }

        List<PaymentSummaryDTO> result = new ArrayList<>();

        YearMonth cursor = YearMonth.from(startDate);
        YearMonth limit = YearMonth.from(endDate);

        while (!cursor.isAfter(limit)) {
            result.add(new PaymentSummaryDTO(
                    cursor,
                    summary.getOrDefault(cursor.toString(), 0.0)
            ));

            cursor = cursor.plusMonths(1);
        }

        return result;
    }

    private static void setParams(
            PaymentStatus status, YearMonth competency,
            PaymentType paymentType, Long tenantId,
            TypedQuery<Payment> jpqlQuery
    ) {
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
    }
}
