package br.com.devduo.viverbemapi.repository.custom;

import br.com.devduo.viverbemapi.models.Tenant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class TenantRepositoryCustomImpl implements TenantRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private static final String INITIAL = "SELECT t FROM Tenant t ";
    private static final String LEFT_JOIN_CONTRACT = "LEFT JOIN t.contract c ";
    private static final String WHERE = "WHERE 1=1 ";

    @Override
    public List<Tenant> findAll(Pageable pageable, String name, Boolean isActive, Boolean hasContractActive) {
        StringBuilder query = new StringBuilder(INITIAL);

        if (hasContractActive != null) {
            query.append(LEFT_JOIN_CONTRACT);
        }

        StringBuilder where = new StringBuilder(WHERE);

        if (name != null) {
            where.append("AND LOWER(t.name) LIKE :name ");
        }

        if (isActive != null) {
            where.append("AND t.isActive = :isActive ");
        }

        if (hasContractActive != null) {
            if (hasContractActive) {
                where.append("AND c.isActive = true ");
            } else {
                where.append("AND (c.uuid IS NULL OR c.isActive = false) ");
            }
        }

        String orderField = "t.createdAt";
        String orderDirection = "DESC";

        if (!pageable.getSort().isUnsorted()) {
            var sort = pageable.getSort().iterator().next();
            orderField = "t." + sort.getProperty();
            orderDirection = sort.getDirection().name();
        }

        query.append(where)
                .append("ORDER BY ")
                .append(orderField).append(" ")
                .append(orderDirection);

        var jpqlQuery = entityManager.createQuery(query.toString(), Tenant.class);

        if (name != null) {
            jpqlQuery.setParameter("name", "%" + name.toLowerCase() + "%");
        }
        if (isActive != null) {
            jpqlQuery.setParameter("isActive", isActive);
        }

        jpqlQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        jpqlQuery.setMaxResults(pageable.getPageSize());

        return jpqlQuery.getResultList();
    }
}
