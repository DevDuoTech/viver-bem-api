-- Inserção do usuário
INSERT INTO tb_user (
    email,
    password,
    profile_picture,
    is_account_non_expired,
    is_account_non_locked,
    is_credentials_non_expired,
    is_enabled
) VALUES (
    'zenadio@yahoo.com.br',
    '$2a$10$YvP.gzmOfhAsCPYGOkVI2uVZiJOFL3RXSstMHQCqhZpxjfZOVxmiq',
    NULL,
    TRUE,
    TRUE,
    TRUE,
    TRUE
);

-- Associação do usuário à role ADMIN
INSERT INTO user_roles (role_id, user_id)
VALUES (
    1,
    1
);